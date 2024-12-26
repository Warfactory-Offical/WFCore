package wfcore.api.capability.impl.radar.MultiblockRadarLogic;

import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import wfcore.math.ClusterData;
import wfcore.api.utils.IntCoord2;
import wfcore.math.BoundingBox;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static wfcore.api.utils.RadarTeWhitelist.TE_WHITELIST;

public class MultiblockRadarLogic {
    //TODO: Perhaps make those values adjustable?
    public static final int MIN_PTS = 15;
    public static final int EPS = 10;

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
    Async method that is supposed to calculate all data relating to base data, by obtaining list of TEs, finding their
    bounding boxes and centers.
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

}

