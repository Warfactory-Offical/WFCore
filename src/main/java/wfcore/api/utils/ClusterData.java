package wfcore.api.utils;

import wfcore.api.capability.impl.radar.MultiblockRadarLogic.MultiblockRadarLogic;

import java.util.List;

public class ClusterData {
    private final List<IntCoord2> coordinates;
    private final IntCoord2 centerPoint;
    private final BoundingBox boundingBox;
    private final int playerPopulation;

    public ClusterData(List<IntCoord2> coordinates, IntCoord2 centerPoint, BoundingBox boundingBox, int playerPopulation) {
        this.coordinates = coordinates;
        this.centerPoint = centerPoint;
        this.boundingBox = boundingBox;
        this.playerPopulation = playerPopulation;
    }

    public List<IntCoord2> getCoordinates() {
        return coordinates;
    }

    public IntCoord2 getCenterPoint() {
        return centerPoint;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public int getPlayerPopulation() {
        return playerPopulation;
    }

    private class BoundingBox {
        private final IntCoord2 min, max;

        BoundingBox(IntCoord2 min, IntCoord2 max) {
            this.min = min;
            this.max = max;
        }

        public IntCoord2 getMin() {
            return min;
        }

        public IntCoord2 getMax() {
            return max;
        }
    }
}
