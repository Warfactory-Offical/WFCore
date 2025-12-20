package wfcore.api.util.math;

public class BoundingBox {
    private final IntCoord2 min, max;

    public BoundingBox(IntCoord2 min, IntCoord2 max) {
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
