package wfcore.api.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface ICustomBlockItem {
    @Nullable
    public default<T extends Block> Function<T, ItemBlock> getItemBlock(){return null;}
}
