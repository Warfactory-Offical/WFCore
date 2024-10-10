package wfcore.math;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BoundingBox {
    List<BlockPos> coordinates = new ArrayList<>();
    private BlockPos[] boundingBox;

    public BoundingBox(List<BlockPos> coordinates) {
        this.coordinates = coordinates;
    }

    public static BlockPos[] calculateBoundingPos(List<BlockPos> pointList){
        int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE;
        int xMax = Integer.MIN_VALUE, yMax = Integer.MIN_VALUE;

        for(BlockPos p : pointList){
            if (p.getX() < xMin) xMin = p.getX();
            if (p.getX() > xMax) xMax = p.getX();
            if (p.getY() < yMin) yMin = p.getY();
            if (p.getY() > yMax) yMax = p.getY();
        }
        return new BlockPos[] {new BlockPos(xMin, yMin, 0), new BlockPos(xMax, yMax, 0)};
    }
    public static BlockPos calculateCentroid(List<BlockPos> pointList) {
        int xSum = 0, ySum = 0;
        int n = pointList.size();
        for (BlockPos pos : pointList) {
            xSum += pos.getX();
            ySum += pos.getY();
        }

        return new BlockPos(xSum / n, ySum / n, 0);
    }


    public List<BlockPos> getCoordinates() {
        return coordinates;
    }

    public BlockPos getCentroid() {
        return calculateCentroid(coordinates);
    }

    public BlockPos[] getBoundingBox() {
        return calculateBoundingPos(coordinates);
    }
}
