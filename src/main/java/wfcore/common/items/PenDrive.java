package wfcore.common.items;

import ca.weblite.objc.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import wfcore.api.capability.data.IData;
import wfcore.api.capability.data.IDataStorage;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PenDrive extends BaseItem implements IDataStorage {
    private final static BigInteger BIT_CAPACITY = BigInteger.valueOf(16 * G);
    private final static int NUM_PERCENT_FIGURES = 4;
    private final static BigInteger PERCENT_SCALAR = BigInteger.valueOf((long) Math.pow(10, (double) NUM_PERCENT_FIGURES));

    public PenDrive(String s, String texturePath) {
        super(s, texturePath);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        boolean isPressingShift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        // calculate the raw amounts
        BigInteger bitsTaken = this.numBitsTaken(stack);
        BigInteger bitsFree = bitCapacity().subtract(bitsTaken);
        ITextComponent totalStorage = IDataStorage.formatBitCount(bitCapacity(), !isPressingShift, 3);
        ITextComponent storageAvailable = IDataStorage.formatBitCount(bitsFree, !isPressingShift, 3);
        ITextComponent storageTaken = IDataStorage.formatBitCount(bitsTaken, !isPressingShift, 3);

        // calculate percentages to certain number of places
        BigInteger percentFree = bitsFree.multiply(PERCENT_SCALAR).divide(bitCapacity());
        BigInteger percentTaken = bitsFree.multiply(PERCENT_SCALAR).divide(bitCapacity());

        tooltip.add(
                new TextComponentTranslation(
                        "info.pen_drive.default"
                        )
                        .setStyle(new Style().setColor(TextFormatting.GRAY)).getFormattedText());

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
    public long numCyclesToWriteWord() {
        return 1;
    }

    @Override
    public long numCyclesToReadWord() {
        return 1;
    }

    @Override
    public BigInteger numBitsTaken(ItemStack stack) {
        return IDataStorage.bitsUsed(readData(stack));
    }

    @Override
    public BigInteger numBitsFree(ItemStack stack) {
        return bitCapacity().subtract(numBitsTaken(stack));
    }

    @Override
    public boolean writeData(ItemStack stack, ArrayList<IData> data) {
        var bitsNeeded = IDataStorage.bitsUsed(data);
        var bitsAvailable = numBitsFree(stack);

        // not enough space to store
        if (bitsNeeded.compareTo(bitsAvailable) < 0) {
            return false;
        }

        // read the nbt, update it, and write back
        var stackNBT = stack.serializeNBT();
        data.forEach(dataInst -> IDataStorage.writeDataToNBT(dataInst, stackNBT));
        stack.deserializeNBT(stackNBT);

        return true;
    }

    @Override
    public ArrayList<IData> readData(ItemStack stack) {
        return IDataStorage.readDataFromNBT(stack.serializeNBT());
    }

}
