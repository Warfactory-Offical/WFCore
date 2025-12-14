package wfcore.common.data;


import net.minecraft.util.math.BlockPos;
import org.apache.commons.math3.ml.clustering.Clusterable;

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
}
