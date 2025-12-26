package wfcore.api.util.math;

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

    // TODO: make this translatable
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        // summarize cluster
        str.append("Cluster Centered on ");
        str.append(centerPoint.toString());
        str.append(" with ");
        str.append(playerPopulation);
        str.append(" player(s) inside ");
        str.append(boundingBox.toString());

        // list points
        str.append("\nCOORDS: [\n    ");
        var coordIt = coordinates.iterator();
        int coordsOnLine = 0;
        while (coordIt.hasNext()) {
            IntCoord2 currCoord = coordIt.next();
            // list points
            str.append(currCoord.toString());
            ++coordsOnLine;

            // support next character if there will be one
            if (coordIt.hasNext()) {
                // only put five coordinates on a given line
                if (coordsOnLine >= 5) {
                    str.append(",\n    ");
                    coordsOnLine = 0;
                } else {
                    str.append(", ");
                }
            }
        }

        str.append("\n]");
        return str.toString();
    }

}
