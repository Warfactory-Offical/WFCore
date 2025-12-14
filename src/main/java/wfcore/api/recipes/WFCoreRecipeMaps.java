package wfcore.api.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.BlastRecipeBuilder;
import gregtech.core.sound.GTSoundEvents;

public class WFCoreRecipeMaps {
    public static final RecipeMap<BlastRecipeBuilder> LARGE_BLAST_FURNACE =
            new RecipeMap<>(
                    "large_blast_furnace",
                    2,  // max item inputs
                    1,  // max item outputs
                    0,  // max fluid inputs
                    0,  // max fluid outputs
                    new BlastRecipeBuilder(),
                    false
            )
                    .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressWidget.MoveType.HORIZONTAL)
                    .setSound(GTSoundEvents.FURNACE);


}