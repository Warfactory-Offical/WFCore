package wfcore.api.capability.data;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import wfcore.WFCore;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.function.Function;

public interface IDataStorage {
    public static long K = 1_000L;
    public static long M = 1_000_000L;
    public static long G = 1_000_000_000L;
    public static long T = 1_000_000_000_000L;
    public static long P = 1_000_000_000_000_000L;
    public static long E = 1_000_000_000_000_000_000L;

    public static BigInteger Z = BigInteger.valueOf(E).multiply(BigInteger.valueOf(1000L));
    public static BigInteger Y = Z.multiply(BigInteger.valueOf(1000L));

    public static BigInteger[] prefixes = {
            BigInteger.ONE,
            BigInteger.valueOf(K), BigInteger.valueOf(M), BigInteger.valueOf(G),
            BigInteger.valueOf(T), BigInteger.valueOf(P), BigInteger.valueOf(E),
            Z, Y
    };

    public static String[] prefixNames = {
            "", "K", "M", "G", "T", "P", "E", "Z", "Y"
    };

    // each data inheritor must specify the method it uses to create an instance of itself from NBT
    public static DataHandler DATA_HANDLER = new DataHandler().initializeDataHandler();

    // DO NOT CHANGE - WILL AFFECT SAVED NBT DATA
    public static String DATA_NBT_KEY = "wfc_data";
    public static String DATA_CLASS_ID_KEY = "data_class_id";

    // set num decimal places to -1 to get full answer
    @SideOnly(Side.CLIENT)
    public static ITextComponent formatBitCount(BigInteger numBits, boolean doPrefixScaling, int numDecimalPlaces) {
        String exactValue = numBits.toString(10);
        if (!doPrefixScaling) {
            return new TextComponentString(exactValue + " ")
                    .appendSibling(new TextComponentTranslation("wfcore.data.bit_or_bits"));
        }

        // clamp the power of ten needed for this number of bits
        int prefixFlooredPowOfTen = exactValue.length() / 3;
        if (prefixFlooredPowOfTen >= prefixNames.length) { prefixFlooredPowOfTen = prefixNames.length - 1; }

        // Use the correct power of ten to divide this number
        var result = numBits.divideAndRemainder(prefixes[prefixFlooredPowOfTen]);
        String remainderString = result[1].toString(10);
        boolean wasExact = true;

        // trim the remainder if needed
        if (remainderString.length() > numDecimalPlaces) {
            var trimmedRemainder = result[1].divideAndRemainder(BigInteger.valueOf((long) Math.pow(10, (double) remainderString.length() - numDecimalPlaces)));
            wasExact = trimmedRemainder[1].compareTo(BigInteger.ZERO) == 0;  // only exact trim if there was no remainder
            remainderString = trimmedRemainder[0].toString(10);
        }

        // return the formatted bit count
        return new TextComponentString((wasExact ? "" : "~") + result[0].toString(10) + "." + remainderString
                + " " + prefixNames[prefixFlooredPowOfTen])
                .appendSibling(new TextComponentTranslation("b"));
    }

    private static boolean hasClassId(int classId) {
        return DATA_HANDLER.DATA_READER_REGISTRY.containsKey(classId);
    }

    private static Function<NBTTagCompound, ? extends IData> getNBTSupplier(int classId) {
        return DATA_HANDLER.DATA_READER_REGISTRY.get(classId);
    }

    public static <T extends IData> void writeDataToNBT(T data, NBTTagCompound nbt) {
        // write the data tag list to nbt if needed
        if (!nbt.hasKey(DATA_NBT_KEY )) {
            nbt.setTag(DATA_NBT_KEY , new NBTTagList());
        }

        // prepare this instance to be added to nbt data
        var thisNBT = data.toNBT();
        thisNBT.setInteger(DATA_CLASS_ID_KEY, data.getId().id);

        // add this instance to the nbt
        var dataList = nbt.getTagList(DATA_NBT_KEY , 10);
        dataList.appendTag(thisNBT);

        // store into nbt
        nbt.setTag(DATA_NBT_KEY, dataList);
    }

    public static ArrayList<IData> readDataFromNBT(NBTTagCompound nbt) {
        if (!nbt.hasKey(DATA_NBT_KEY)) { return null; }
        var result = new ArrayList<IData>();

        nbt.getTagList(DATA_NBT_KEY, 10).tagList.forEach(tag -> {
            var tagCompound = (NBTTagCompound) tag;
            if (!tagCompound.hasKey(DATA_CLASS_ID_KEY)) {
                WFCore.LOGGER.atError().log("Malformed data nbt found w/o id tag (" + tagCompound.toString() + "), ignoring");
                return;
            }

            int classId = tagCompound.getInteger(DATA_CLASS_ID_KEY);
            if (!hasClassId(classId)) {
                WFCore.LOGGER.atError().log("Malformed data nbt found w/ unknown id (" + tagCompound.toString() + "), ignoring");
                return;
            }

            result.add(getNBTSupplier(classId).apply(tagCompound));
        });

        return result;
    }

    public static BigInteger bitsUsed(ArrayList<IData> data) {
        BigInteger bitsUsed = BigInteger.ZERO;
        for (var currData : data) {
            bitsUsed = bitsUsed.add(currData.numBits());
        }

        return bitsUsed;
    }


    // IO Capabilities
    public boolean canRead();
    public boolean canWrite();
    public boolean doOverwrites();

    // Storage capabilities
    public BigInteger bitCapacity();

    // IO Specs
    public long maxCyclesPerSec();
    public long numCyclesToWriteWord();
    public long numCyclesToReadWord();

    // Stored data
    public BigInteger numBitsTaken(ItemStack stack);
    public BigInteger numBitsFree(ItemStack stack);
    public boolean writeData(ItemStack stack, ArrayList<IData> data);
    public ArrayList<IData> readData(ItemStack stack);

}
