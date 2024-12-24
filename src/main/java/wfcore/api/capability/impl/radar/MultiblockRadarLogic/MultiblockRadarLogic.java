package wfcore.api.capability.impl.radar.MultiblockRadarLogic;

import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.jetbrains.annotations.NotNull;
import wfcore.api.utils.IntCoord2;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MultiblockRadarLogic {
    public static final int MIN_PTS = 15;
    public static final int EPS = 10;
    private static final Set<Class<? extends TileEntity>> TE_WHITELIST = new HashSet<>();

    static {
        //TODO: add any viable tileenttity from GT, AE2, WARFORGE and more
    }

    private int voltageTier;
    private int overclockAmount;
    private int speed;
    private MetaTileEntity MetaTileEntity;


    public MultiblockRadarLogic(@NotNull MetaTileEntity MetaTileEntity, int speed) {
        this.MetaTileEntity = MetaTileEntity;
        this.speed = speed;

    }

    public static boolean isOnTEWhitelist(TileEntity tileEntity) {
        return TE_WHITELIST.contains(tileEntity);
    }

    static public IntCoord2 getCoordPair(BlockPos pos) {
        return new IntCoord2(pos);
    }

    private HashMap<Object, IntCoord2> collectValidEntites() {
        World world = Minecraft.getMinecraft().world;
        HashMap<Object, IntCoord2> entityPosMap = new HashMap<>();

        if (world != null && !world.isRemote) {
            List<EntityPlayer> worldPlayers = world.playerEntities;
            for(EntityPlayer player :  worldPlayers){
                entityPosMap.put(player, getCoordPair(player.getPosition()));
            }

            List<TileEntity> worldTileEntites = world.loadedTileEntityList;
            for (TileEntity tileEntity : worldTileEntites) {
                if (isOnTEWhitelist(tileEntity)) entityPosMap.put(tileEntity, getCoordPair(tileEntity.getPos()));
            }

        }

        return entityPosMap;
    }

    /*
    Async method that is supposed to calculate all data relating to base data, by obtaining list of TEs, finding their
    bounding boxes and centers.
     */
    private CompletableFuture<List<ClusterData>> calculateDBSCAN(HashMap<Object, IntCoord2> objList) {

        // TODO: Account for players inside clusters, giving it higher credibility
        return CompletableFuture.supplyAsync(() -> {


            DBSCANClusterer<IntCoord2> dbscan = new DBSCANClusterer<>(EPS, MIN_PTS);
            List<Cluster<IntCoord2>> clusters = dbscan.cluster(objList.values());

            List<ClusterData> clusterDataList = new ArrayList<>();
            for (Cluster<IntCoord2> cluster : clusters) {
                List<IntCoord2> clusterPoints = cluster.getPoints();

                //Variables for the bounding box
                int minX = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
                int sumX = 0, sumZ = 0;

                //Iterate over points until the size of the bounding box is found ( there is a good reason to offthread this)
                for (IntCoord2 point : clusterPoints) {
                    int x = point.getX();
                    int z = point.getZ();

                    minX = Math.min(minX, x);
                    minZ = Math.min(minZ, z);
                    maxX = Math.max(maxX, x);
                    maxZ = Math.max(maxZ, z);

                    sumX += x;
                    sumZ += z;

                }
                IntCoord2 boundingBoxMin = new IntCoord2(minX, minZ);
                IntCoord2 boundingBoxMax = new IntCoord2(maxX, maxZ);
                IntCoord2 clusterCenter = new IntCoord2(sumX / clusterPoints.size(), sumZ / clusterPoints.size());

                clusterDataList.add(new ClusterData(clusterPoints, clusterCenter, new Tuple<>(boundingBoxMin, boundingBoxMax)));
            }

            return clusterDataList;


        });
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

    private class ClusterData {
        private final List<IntCoord2> coordinates;
        private final IntCoord2 centerPoint;
        //Left –> Min, Right –> Max
        private final Tuple<IntCoord2, IntCoord2> boundingBox;

        public ClusterData(List<IntCoord2> coordinates, IntCoord2 centerPoint, Tuple<IntCoord2, IntCoord2> boundingBox) {
            this.coordinates = coordinates;
            this.centerPoint = centerPoint;
            this.boundingBox = boundingBox;
        }

        public List<IntCoord2> getCoordinates() {
            return coordinates;
        }

        public IntCoord2 getCenterPoint() {
            return centerPoint;
        }

        public Tuple<IntCoord2, IntCoord2> getBoundingBox() {
            return boundingBox;
        }
    }
}
