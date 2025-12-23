package wfcore.api.util.math;

import net.minecraft.util.math.BlockPos;
import org.apache.commons.math3.ml.clustering.Clusterable;

//Simple integer tuple implementation, no point getting external Libraries involved
// Implements clusterable for use in dbscan
public class IntCoord2 implements Clusterable {
    private final int X, Z;

    public IntCoord2(int xVal, int zVal) {
        this.X = xVal;
        this.Z = zVal;
    }

    public IntCoord2(BlockPos pos) {
        this.X = (int) pos.getX();
        this.Z = (int) pos.getZ();
    }

    public int getX() {
        return X;
    }

    public int getZ() {
        return Z;
    }

    @Override
    public double[] getPoint() {
        return new double[]{X, Z};
    }

    @Override
    public String toString() {
        return "(" + X + ", " + Z + ")";
    }
}
