package wfcore.common.items;

import gregtech.api.util.LocalizationUtils;
import gregtech.api.util.TextComponentUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import wfcore.api.capability.data.IData;
import wfcore.api.capability.data.IDataStorage;
import wfcore.api.capability.data.NBTFileSys;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

// TODO: make this extend a base "data storage" item class
public class PenDrive extends BaseItem implements IDataStorage {
    private final static BigInteger BIT_CAPACITY = BigInteger.valueOf(16 * G);

    private final static int NUM_PERCENT_DEC_FIGURES = 2;
    private final static BigInteger PERCENT_SCALAR = BigInteger.valueOf((long) Math.pow(10, NUM_PERCENT_DEC_FIGURES));
    private final static BigInteger PERCENT_HUNDRED = PERCENT_SCALAR.divide(BigInteger.valueOf(100L));

    public PenDrive(String s, String texturePath) {
        super(s, texturePath);
    }

    public static String padZeroes(String string, int targetLength) {
        StringBuilder builder = new StringBuilder(string);
        while (builder.length() < targetLength) { builder.append('0'); }
        return builder.toString();
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        boolean isPressingShift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        // calculate the raw amounts
        BigInteger bitsTaken = this.numBitsTaken(stack);
        BigInteger bitsFree = bitCapacity().subtract(bitsTaken);
        ITextComponent totalStorage = IDataStorage.formatBitCount(bitCapacity(), !isPressingShift, 3);
        ITextComponent storageTaken = IDataStorage.formatBitCount(bitsTaken, !isPressingShift, 3);
        ITextComponent storageAvailable = IDataStorage.formatBitCount(bitsFree, !isPressingShift, 3);

        // calculate percentages to certain number of places
        BigInteger[] percentTaken = bitsTaken.multiply(PERCENT_SCALAR).divide(bitCapacity()).divideAndRemainder(PERCENT_HUNDRED);
        BigInteger[] percentFree = bitsFree.multiply(PERCENT_SCALAR).divide(bitCapacity()).divideAndRemainder(PERCENT_HUNDRED);

        // we get the unformatted text because we dont want the conversion to insert text formatting reset characters; we just want text
        TextComponentTranslation defaultText = new TextComponentTranslation("info.pen_drive.default", totalStorage);
        TextComponentString spaceTaken = TextComponentUtil.stringWithColor(TextFormatting.RED, (" %s (%s.%s%%) %s").formatted(
                storageTaken.getUnformattedText(),
                percentTaken[0].toString(10),
                padZeroes(percentTaken[1].toString(10), NUM_PERCENT_DEC_FIGURES),
                LocalizationUtils.format("info.data_storage.taken")
        ));

        TextComponentString spaceFree = TextComponentUtil.stringWithColor(TextFormatting.DARK_GREEN, (" %s (%s.%s%%) %s").formatted(
                storageAvailable.getUnformattedText(),
                percentFree[0].toString(10),
                padZeroes(percentFree[1].toString(10), NUM_PERCENT_DEC_FIGURES),
                LocalizationUtils.format("info.data_storage.free")
        ));

        StringBuilder result = new StringBuilder(defaultText.getFormattedText());
        if (bitsTaken.compareTo(BigInteger.ZERO) > 0) {
            result.append(spaceTaken.getFormattedText());
        }

        if (bitsFree.compareTo(BigInteger.ZERO) > 0) {
            if (!defaultText.getSiblings().isEmpty()) {
                result.append(", ");
            }

            result.append(spaceFree.getFormattedText());
        }

        tooltip.add(result.toString());
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    @Override
    public boolean doOverwrites() {
        return false;
    }

    @Override
    public BigInteger bitCapacity() {
        return BIT_CAPACITY;
    }

    @Override
    public long maxCyclesPerSec() {
        return M;
    }

    @Override
    public long numBitsWrittenPerCycle() {
        return 1;
    }

    @Override
    public long numBitsReadPerCycle() {
        return 1;
    }

    @Override
    public long energyPerWrite() {
        return 0;
    }

    @Override
    public long energyPerRead() {
        return 0;
    }

    @Override
    public long operatingVoltage() {
        return 0;
    }

    @Override
    public BigInteger numBitsTaken(ItemStack stack) {
        return IDataStorage.bitsUsed(readData(stack, NBTFileSys.ROOT_PATH));
    }

    @Override
    public BigInteger numBitsFree(ItemStack stack) {
        return bitCapacity().subtract(numBitsTaken(stack));
    }

    @Override
    public NBTTagCompound getStorageNBT(ItemStack stack) {
        return null;
    }

    @Override
    public boolean hasData(ItemStack stack, String path) {
        return false;
    }

    @Override
    public boolean deleteData(ItemStack stack, String path) {
        return false;
    }

    @Override
    public boolean writeData(ItemStack stack, IData data) {
        return writeData(stack, NBTFileSys.ROOT_PATH, data);
    }

    @Override
    public boolean writeData(ItemStack stack, String path, IData data) {
        var stackNBT = stack.getTagCompound();
        if (stackNBT == null) {
            stackNBT = new NBTTagCompound();
            stack.setTagCompound(stackNBT);
        }

        return NBTFileSys.writeData(stackNBT, path, data);
    }

    @Override
    public ArrayList<IData> readData(ItemStack stack, String path) {
        var stackNBT = stack.getTagCompound();
        if (stackNBT == null) {
            // if there is no stack nbt there is definitely no data
            return new ArrayList<>();
        }

        return NBTFileSys.readData(stackNBT, path);
    }

}
