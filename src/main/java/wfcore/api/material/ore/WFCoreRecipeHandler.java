package wfcore.api.material.ore;

import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.loaders.recipe.handlers.RecyclingRecipeHandler;


public class WFCoreRecipeHandler {




    public static void init() {
        WFCoreOrePrefix.ntmpipe.addProcessingHandler(PropertyKey.DUST, RecyclingRecipeHandler::processCrushing);
        WFCoreOrePrefix.plateTriple.addProcessingHandler(PropertyKey.DUST, RecyclingRecipeHandler::processCrushing);
        WFCoreOrePrefix.plateSextuple.addProcessingHandler(PropertyKey.DUST, RecyclingRecipeHandler::processCrushing);
        WFCoreOrePrefix.shell.addProcessingHandler(PropertyKey.DUST, RecyclingRecipeHandler::processCrushing);
        WFCoreOrePrefix.billet.addProcessingHandler(PropertyKey.DUST, RecyclingRecipeHandler::processCrushing);
        WFCoreOrePrefix.wiredense.addProcessingHandler(PropertyKey.DUST, RecyclingRecipeHandler::processCrushing);

    }


}
