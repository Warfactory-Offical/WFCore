package wfcore.api.capability.data;

import net.minecraft.nbt.NBTTagCompound;

import java.math.BigInteger;

import static wfcore.api.capability.data.NBTFileSys.DATA_HANDLER;

// ALL INHERITORS MUST REGISTER THEMSELVES IN THE DATA HANDLER CLASS
public interface IData {

    public static Class<? extends IData> getDataClass(IData data) {
        return data.getTypeId().clazz;
    }

    public abstract DataHandler.DataClassIdentifier getTypeId();

    // used to get around potential size issues with long, but can be used for easier organization
    public abstract BigInteger numBits();

    public abstract NBTTagCompound toNBT();
}
