package wfcore.common.metatileentities.multi.electric;
;
import gregtech.api.capability.IObjectHolder;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import wfcore.api.util.math.ClusterData;
import wfcore.api.util.math.IntCoord2;
import wfcore.api.util.math.BoundingBox;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MetaTileEntityRadar extends MultiblockWithDisplayBase {
    public MetaTileEntityRadar(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    // I think this is called when the multiblock is formed and valid, but allows extra checks such as muffler blocking
    @Override
    protected void updateFormedValid() {

    }

    // L = bottom (lower) concrete slab, S = Steel Casing, - = Air, N = Stainless Steel Casing,
    // B = ULV Casing (bearing), s = steel frame, n = stainless frame, Z = Controller,
    // R = right facing (looking at controller) top stair
    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        // direction subsequent chars, strings, and aisles travel in relative to controller faces, respectively
        return FactoryBlockPattern.start(RelativeDirection.BACK, RelativeDirection.UP, RelativeDirection.RIGHT)
                .aisle("LSL", "---", "-N-")
                .aisle("SBP", "ZBP", "-n-")
                .aisle("LSL", "-R-", "-s-")
                .where('Z', this.selfPredicate())
                .where('L', states(Blocks.STONE_SLAB.getDefaultState()))
                .where('-', any())
                .where('R', states(topRightCStair()))
                .where('S', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID)))
                .where('N', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STAINLESS_CLEAN)))
                .where('B', states(MetaBlocks.MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.ULV)))
                .where('s', states(MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel)))
                .where('n', states(MetaBlocks.FRAMES.get(Materials.StainlessSteel).getBlock(Materials.StainlessSteel)))
                .where('P', states(MetaBlocks.MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.ULV))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY)
                                .setMinGlobalLimited(1))
                        .or(this.maintenancePredicate())
                                .setExactLimit(1))
                .build();
    }

    private IBlockState topRightCStair() {
        // hopefully this lookup isn't too expensive - would prefer to ref directly, but I don't know where hbm keeps this
        // could also store a static instance, but rather not if this is fine
        var stair = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("hbm", "concrete_smooth_stairs"));
        if (stair == null) { return Blocks.COBBLESTONE.getDefaultState(); }

        return stair.getBlockState().getBaseState();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return sourcePart != null && !(sourcePart instanceof IObjectHolder) ? Textures.COMPUTER_CASING : Textures.ADVANCED_COMPUTER_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityRadar(this.metaTileEntityId);
    }

    // do radar stuff every tick on the server
    @Override
    public void update() {
        // ignore clients
        if (this.getWorld().isRemote) { return; }
    }

    public static ObjectOpenHashSet<TileEntity> TE_WHITELIST = new ObjectOpenHashSet<>();

    public class MultiblockRadarLogic {
        //TODO: Make those values adjustable in GUI
        public int MIN_PTS = 15;
        public int EPS = 10;

        private int voltageTier;
        private int overclockAmount;
        private int speed;
        private Map<IntCoord2, Object> LoadedValidObjects  = new HashMap<>();
        private List<ClusterData> scanResults = new ArrayList<>();
        private MetaTileEntity metaTileEntity;
        private boolean isFinished;
        private boolean isActive = false;
        private boolean isWorkingEnabled = true;

        public MultiblockRadarLogic(int voltageTier, int overclockAmount, int speed, gregtech.api.metatileentity.MetaTileEntity metaTileEntity) {
            this.voltageTier = voltageTier;
            this.overclockAmount = overclockAmount;
            this.speed = speed;
            this.metaTileEntity = metaTileEntity;
            this.isFinished = false;

        }

        /* Scan should do as follows:
        1. Make sure that its on server
        2. make sure it can scan and is enabled, it should not activate on power on, player must trigger scan
             in GUI by hand
        3. make sure dataslot (custom TE that can only hold data item such as memory stick or orb) has empty valid data item (should work with data bank)
        4. Grab snapshot of valid TEs and players (this ofc is done on server thread)
        5. Run calculation
        6. Put it on data stick so it can then be put into a printer (basically port of NH printer), for data to be put in a book
         */
        //Perhaps some visualization in the tablet?
        //OPTIONAL: integrate Map mod with the mod, so players have bounding boxes drawn on their minimap, with all valid TEs  pointed out and waypoints to the centers

        public void performScan() {
            if (metaTileEntity.getWorld().isRemote)
                return;

            if(!this.isWorkingEnabled)
                return;

            if(!checkCanScan())
                return;

            //Scanner cannot perform a scan if data is already written
            if(!dataSlotIsEmpty() && !dataSlotIsWritten()){
                //Get the snapshot of all loaded players TEs
                this.LoadedValidObjects  = this.collectValidEntites();
                //Run dbscan
                calculateDBSCAN(LoadedValidObjects).thenAccept(clusterData -> {
                    this.scanResults = clusterData;
                }).exceptionally(ex -> {
                    System.err.println("Error during DBSCAN calculation: " + ex.getMessage());
                    return null;
                });
            }
        }

        public static boolean isOnTEWhitelist(TileEntity tileEntity) {
            return TE_WHITELIST.contains(tileEntity);
        }

        static public IntCoord2 getCoordPair(BlockPos pos) {
            return new IntCoord2(pos);
        }

        //Collect snapshot of all players and valid TEs
        private HashMap<IntCoord2, Object> collectValidEntites() {
            MinecraftServer serverInstance = FMLCommonHandler.instance().getMinecraftServerInstance();
            HashMap<IntCoord2, Object> entityPosMap = new HashMap<>();

            List<EntityPlayerMP> worldPlayers = serverInstance.getPlayerList().getPlayers();
            for (EntityPlayerMP player : worldPlayers) {
                entityPosMap.put(getCoordPair(player.getPosition()), player);
            }

            List<TileEntity> worldTileEntites = serverInstance.getEntityWorld().loadedTileEntityList;
            for (TileEntity tileEntity : worldTileEntites) {
                if (isOnTEWhitelist(tileEntity)) entityPosMap.put(getCoordPair(tileEntity.getPos()), tileEntity);
            }

            return entityPosMap;
        }

        /*
        Radar takes all loaded valid entities and players, uses clustering algorithm DBSCAN and finds
        all clusters, which in this case are bases.
        It finds a bounding box and center of the base, scans how many players are inside. This is
        to avoid possible ghost bases. (generally player and TE dense areas are bases). This  SHOULD
        be ran async and must be done before the simulated scan is done (default time: 2000 seconds),
        values such as EPS and MIN_PTS should be adjustable in GUI by player.
         */
        private CompletableFuture<List<ClusterData>> calculateDBSCAN(Map<IntCoord2, Object> objMap) {

            return CompletableFuture.supplyAsync(() -> {
                DBSCANClusterer<IntCoord2> dbscan = new DBSCANClusterer<>(EPS, MIN_PTS);

                List<Cluster<IntCoord2>> clusters = dbscan.cluster(new ArrayList<>(objMap.keySet()));

                List<ClusterData> clusterDataList = new ArrayList<>(clusters.size());

                for (Cluster<IntCoord2> cluster : clusters) {
                    List<IntCoord2> clusterPoints = cluster.getPoints();

                    int minX = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
                    int maxX = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
                    int sumX = 0, sumZ = 0;

                    for (IntCoord2 point : clusterPoints) {
                        int x = point.getX();
                        int z = point.getZ();

                        if (x < minX) minX = x;
                        if (x > maxX) maxX = x;
                        if (z < minZ) minZ = z;
                        if (z > maxZ) maxZ = z;

                        sumX += x;
                        sumZ += z;
                    }

                    IntCoord2 boundingBoxMin = new IntCoord2(minX, minZ);
                    IntCoord2 boundingBoxMax = new IntCoord2(maxX, maxZ);
                    IntCoord2 clusterCenter = new IntCoord2(sumX / clusterPoints.size(), sumZ / clusterPoints.size());

                    int playerPopulation = calculatePlayerPopulation(objMap, clusterPoints);

                    clusterDataList.add(new ClusterData(
                            clusterPoints,
                            clusterCenter,
                            new BoundingBox(boundingBoxMin, boundingBoxMax),
                            playerPopulation
                    ));
                }
                return clusterDataList;
            });
        }

        private int calculatePlayerPopulation(Map<IntCoord2, Object> objMap, List<IntCoord2> clusterPoints) {
            int population = 0;
            for (IntCoord2 point : clusterPoints) {
                if (objMap.get(point) instanceof EntityPlayerMP) {
                    population++;
                }
            }
            return population;
        }


        public int getVoltageTier() {
            return voltageTier;
        }

        public void setVoltageTier(int tier) {
            this.voltageTier = tier;
        }

        public int getOverclockAmount() {
            return this.overclockAmount;
        }

        public void setOverclockAmount(int amount) {
            this.overclockAmount = amount;
        }

        public void getSpeed() {
        }

        public boolean isWorking() {
            return false;
        }

        public boolean checkCanScan(){
            //FIXME
            return true;
        }
        public boolean dataSlotIsEmpty(){
            //FIXME
            return false;
        }

        public boolean dataSlotIsWritten(){
            //FIXME
            return false;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public NBTTagCompound writeToNBT(NBTTagCompound data) {
            //FIXME
            return null;
        }
    }

}
