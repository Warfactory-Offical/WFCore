package wfcore.api.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import java.util.Map;
import java.util.Objects;

public class FurnaceUtil {

    public static final Map<ItemStack, ItemStack> RAW_LIST  = FurnaceRecipes.instance().smeltingList;

    public static void removeByOutput(ItemStack output) {
                RAW_LIST.entrySet().
                removeIf(
                        e -> e.getValue().getItem().equals(output.getItem())  && output.getMetadata() ==  e.getValue().getMetadata()
                );
    }

    public static void removeByInput(ItemStack input) {
        RAW_LIST.entrySet().
                removeIf(
                        e -> e.getKey().getItem().equals(input.getItem())  && input.getMetadata() ==  e.getKey().getMetadata()
                );
    }
}
