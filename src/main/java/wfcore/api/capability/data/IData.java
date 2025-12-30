package wfcore.api.capability.data;

import net.minecraft.nbt.NBTTagCompound;

import java.math.BigInteger;

// ALL INHERITORS MUST REGISTER THEMSELVES IN THE DATA HANDLER CLASS
public interface IData {

    public abstract DataHandler.DataClassIdentifier getId();

    // used to get around potential size issues with long, but can be used for easier organization
    public abstract BigInteger numBits();

    public abstract NBTTagCompound toNBT();
}
