package wfcore.api.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

//Holds single valid data item
//It should be able to auto input and output, but not nessesary
//It should have methods that allow for writing, reading information from the data item
public interface IDataSlot {

    @NotNull ItemStack getDataItem(boolean var1);

    void setDataItem(@NotNull ItemStack var1);

    void setLocked(boolean var1);

    EnumFacing getFrontFacing();

    @NotNull IItemHandler getAsHandler();
}
