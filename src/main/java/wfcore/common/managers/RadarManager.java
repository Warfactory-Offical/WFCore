package wfcore.common.managers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import wfcore.api.util.math.BoundingBox;
import wfcore.api.util.math.ClusterData;
import wfcore.api.util.math.IntCoord2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//TODO: Figure out if this can be removed; does this even do anything?
public class RadarManager {

    public static final RadarManager INSTANCE = new RadarManager();

    public static final int MIN_PTS = 15;
    public static final int EPS = 10;

    public HashMap<Object, IntCoord2> collectValidEntites() {
        World world = Minecraft.getMinecraft().world;
        HashMap<Object, IntCoord2> entityPosMap = new HashMap<>();
        List<TileEntity> worldTileEntites = world.loadedTileEntityList;
        List<EntityPlayer> worldPlayers = world.playerEntities;

        for (EntityPlayer player : worldPlayers) {
            entityPosMap.put(player, new IntCoord2(player.getPosition()));

        }

        for (TileEntity tileEntity : worldTileEntites) {
            if (isOnTEWhitelist(tileEntity)) entityPosMap.put(tileEntity, new IntCoord2(tileEntity.getPos()));
        }
        return entityPosMap;
    }

    private boolean isOnTEWhitelist(TileEntity tileEntity) {
        return false;
    }


    public CompletableFuture<List<ClusterData>> calculateDBSCAN(HashMap<Object, IntCoord2> objList) {
        return CompletableFuture.supplyAsync(() -> {
            DBSCANClusterer<IntCoord2> dbscan = new DBSCANClusterer<>(EPS, MIN_PTS);
            List<Cluster<IntCoord2>> clusters = dbscan.cluster(objList.values());
            List<ClusterData> clusterDataList = new ArrayList<>();

            for (Cluster<IntCoord2> cluster : clusters) {

                List<IntCoord2> clusterPoints = cluster.getPoints();
                int minX = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
                int sumX = 0, sumZ = 0;

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
                clusterDataList.add(new ClusterData(clusterPoints, clusterCenter, new BoundingBox(boundingBoxMin, boundingBoxMax), -1));
            }

            return clusterDataList;


        });

    }


}
