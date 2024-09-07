package wfcore.api.recipes;
import gregtech.api.GTValues;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.gui.widgets.ProgressWidget.MoveType;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMapBuilder;
import gregtech.api.recipes.builders.ComputationRecipeBuilder;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.recipes.builders.PrimitiveRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.core.sound.GTSoundEvents;
import net.minecraft.init.SoundEvents;

import static gregtech.api.recipes.RecipeMaps.*;

public class WFCoreRecipeMaps {

    public static final RecipeMap<SimpleRecipeBuilder> PROJECTOR_RECIPES = new RecipeMapBuilder<>("projector",
            new SimpleRecipeBuilder())
            .itemInputs(3)
            .itemOutputs(3)
            .fluidInputs(5)
            .fluidOutputs(2)
            .progressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> Steam_PCB_Factory_Recipes = new RecipeMapBuilder<>("steam_pcb_factory",
            new SimpleRecipeBuilder())
            .itemInputs(2)
            .itemOutputs(1)
            .fluidInputs(0)
            .fluidOutputs(0)
            .progressBar(GuiTextures.PROGRESS_BAR_ARC_FURNACE)
            .build();
    public static final RecipeMap<PrimitiveRecipeBuilder> Large_Blast_Furnace = new RecipeMapBuilder<>("large_blast_furnace",
            new PrimitiveRecipeBuilder())
            .itemInputs(2)
            .itemOutputs(1)
            .fluidInputs(0)
            .fluidOutputs(1)
            .progressBar(GuiTextures.PROGRESS_BAR_ARROW)
            .build();
    public static final RecipeMap<PrimitiveRecipeBuilder> StrandCaster = new RecipeMapBuilder<>("strand_caster",
            new PrimitiveRecipeBuilder())
            .itemInputs(1)
            .itemOutputs(1)
            .fluidInputs(2)
            .fluidOutputs(1)
            .progressBar(GuiTextures.PROGRESS_BAR_ARROW)
            .build();
}
