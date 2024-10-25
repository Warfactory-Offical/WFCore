package wfcore.api.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public interface IDataSlot {

    @NotNull ItemStack getDataItem(boolean var1);

    void setDataItem(@NotNull ItemStack var1);

    void setLocked(boolean var1);

    EnumFacing getFrontFacing();

    @NotNull IItemHandler getAsHandler();
}
