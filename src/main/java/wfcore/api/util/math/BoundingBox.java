package wfcore.api.util.math;

import net.minecraft.nbt.NBTTagCompound;

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

    @Override
    public String toString() {
        return "{" + min.toString() + ", " + max.toString() + "}";
    }

    public NBTTagCompound toNBT() {
        var nbt = new NBTTagCompound();
        nbt.setTag("min", min.toNBT());
        nbt.setTag("max", max.toNBT());
        return nbt;
    }

    public static BoundingBox fromNBT(NBTTagCompound nbt) {
        return new BoundingBox(IntCoord2.fromNBT(nbt.getCompoundTag("min")), IntCoord2.fromNBT(nbt.getCompoundTag("max")));
    }
}
