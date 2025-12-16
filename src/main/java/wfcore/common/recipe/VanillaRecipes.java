package wfcore.common.recipe;

import com.hbm.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.NotNull;
import wfcore.RefStrings;

import java.util.HashSet;
import java.util.Set;

import static wfcore.WFCore.MODID;


public class VanillaRecipes {
    private static final Set<IRecipe> RECIPES = new HashSet<>();


    public static void registerCTRecipes(RegistryEvent.Register<IRecipe> event) {
        ForgeRegistry<IRecipe> registry = (ForgeRegistry<IRecipe>) event.getRegistry();

        registry.remove(new ResourceLocation(RefStrings.HBM, "machine_ammo_press"));

        new ShapedOreRecSelfReg(
                new ResourceLocation(MODID, "hbm_ammo_press"),
                new ItemStack(ModBlocks.machine_ammo_press),
                "SCS",
                "P P",
                "PPP",
                'S', "screwSteel",
                'P', "plateSteel",
                'C', Blocks.PISTON
        ).setRegistryName(MODID, "hbm_ammo_press");


        RECIPES.forEach(registry::register);

    }

    public static class ShapedOreRecSelfReg extends ShapedOreRecipe {

        public ShapedOreRecSelfReg(ResourceLocation group, Block result, Object... recipe) {
            super(group, result, recipe);
            RECIPES.add(this);
        }

        public ShapedOreRecSelfReg(ResourceLocation group, Item result, Object... recipe) {
            super(group, result, recipe);
            RECIPES.add(this);
        }

        public ShapedOreRecSelfReg(ResourceLocation group, @NotNull ItemStack result, Object... recipe) {
            super(group, result, recipe);
            RECIPES.add(this);
        }

        public ShapedOreRecSelfReg(ResourceLocation group, @NotNull ItemStack result, CraftingHelper.ShapedPrimer primer) {
            super(group, result, primer);
            RECIPES.add(this);
        }
    }

}
