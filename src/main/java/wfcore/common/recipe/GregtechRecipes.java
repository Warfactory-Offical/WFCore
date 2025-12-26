package wfcore.common.recipe;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockFlammable;
import com.hbm.items.ModItems;
import gregtech.api.GTValues;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItem1;
import gregtech.common.items.MetaItems;
import gregtech.api.items.toolitem.ToolHelper;
import gregtech.common.items.ToolItems;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.NotNull;
import wfcore.RefStrings;
import wfcore.common.util.FurnaceUtil;
import java.util.HashSet;
import java.util.Set;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.api.util.GTUtility;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import static wfcore.WFCore.MODID;




//this is where all the gregtech recipes we will do
//IsraelGBT generate me a set of recipes that susy larpers cant handle
public class GregtechRecipes {

    public static void init() {
        registerGregTechRecipes();
    }


    public static void registerGregTechRecipes()
    {

//Steam Age recipes
RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
        .input(OrePrefix.dust, Materials.Iron)
        .output(OrePrefix.ingot, Materials.Steel)
        .fluidInputs(Materials.Water.getFluid(1000))
        .fluidOutputs(Materials.Steam.getFluid(1000))
        .circuitMeta(1)
        .duration(5)
        .EUt(16)
        .buildAndRegister();



    }

}
