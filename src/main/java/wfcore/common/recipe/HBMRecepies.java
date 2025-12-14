package wfcore.common.recipe;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.recipes.SolderingRecipes;
import com.hbm.inventory.recipes.anvil.AnvilRecipes;
import com.hbm.items.ItemEnums;
import com.hbm.items.ModItems;
import gregtech.common.items.MetaItems;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

import static com.hbm.inventory.OreDictManager.PB;

public class HBMRecepies {

    //IMPORTANT: Reason why we do recipe registration exactly at the end is to avoid race conditions of the mod
    //Basically, Mod may load before gt but after HBM, causing null items.
    //On FMLLoadCompleteEvent, everything must be registered and all mods must be loaded

    public static void init(FMLLoadCompleteEvent event) {
        addSolderingRecepies(event);
        handleAnvilRecepies(event);
    }

    private static void handleAnvilRecepies(FMLLoadCompleteEvent event) {
        AnvilRecipes.smithingRecipes.clear();
        AnvilRecipes.constructionRecipes.clear();
    }

    private static void addSolderingRecepies(FMLLoadCompleteEvent event) {
        ;
        var solderingRecepies = SolderingRecipes.recipes;
        solderingRecepies.clear();
        solderingRecepies.add(
                new SolderingRecipes.SolderingRecipe(
                        MetaItems.ELECTRONIC_CIRCUIT_LV.getStackForm(1),
                        120,
                        130,
                        new RecipesCommon.AStack[]{
                                new RecipesCommon.ComparableStack(ModItems.circuit, 3, ItemEnums.EnumCircuitType.VACUUM_TUBE),
                                new RecipesCommon.ComparableStack(ModItems.circuit, 2, ItemEnums.EnumCircuitType.CAPACITOR)
                        },
                        new RecipesCommon.AStack[]{new RecipesCommon.ComparableStack(ModItems.circuit, 4, ItemEnums.EnumCircuitType.PCB)},
                        new RecipesCommon.AStack[]{new RecipesCommon.OreDictStack(PB.wireFine(), 4)})
        );


    }

}
