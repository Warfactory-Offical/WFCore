package wfcore.api.capability.data;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;

// TODO: this could use such a massive rewrite; some type of filesystem implementation would be helpful
public interface IDataStorage {
    // UTILITY
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
            "", "k", "M", "G", "T", "P", "E", "Z", "Y"
    };

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
                + prefixNames[prefixFlooredPowOfTen] + "b");
    }

    public static BigInteger bitsUsed(ArrayList<IData> data) {
        BigInteger bitsUsed = BigInteger.ZERO;
        if(data == null) return bitsUsed;

        for (var currData : data) {
            bitsUsed = bitsUsed.add(currData.numBits());
        }

        return bitsUsed;
    }

    // IO Capabilities
    boolean canRead();
    boolean canWrite();
    boolean doOverwrites();

    // Storage capabilities
    BigInteger bitCapacity();

    // IO Specs
    long maxCyclesPerSec();
    long numBitsWrittenPerCycle();
    long numBitsReadPerCycle();

    // TODO: handle sub single EU writes/reads
    long energyPerWrite();
    long energyPerRead();
    long operatingVoltage();

    // Stored data
    BigInteger numBitsTaken(@NotNull ItemStack stack);
    BigInteger numBitsFree(@NotNull ItemStack stack);
    NBTTagCompound getStorageNBT(ItemStack stack);
    boolean hasData(ItemStack stack, String path);
    boolean deleteData(ItemStack stack, String path);
    boolean writeData(ItemStack stack, IData data);  // convenience for when path doesn't matter (use default path)
    boolean writeData(ItemStack stack, String path, IData data);
    ArrayList<IData> readData(ItemStack stack, String path);
}
