package wfcore.common;


import gregtech.api.unification.material.event.PostMaterialEvent;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import wfcore.api.material.modifications.WFCoreMaterialExtraFlags;
import wfcore.api.material.ore.WFCoreOrePrefix;
import wfcore.api.material.ore.WFCoreRecipeHandler;

@Mod.EventBusSubscriber
public class EventHandlers {

    @SubscribeEvent
    public static void materialChanges(PostMaterialEvent event) {
        MetaItems.addOrePrefix(WFCoreOrePrefix.billet);
        MetaItems.addOrePrefix(WFCoreOrePrefix.ntmpipe);
        MetaItems.addOrePrefix(WFCoreOrePrefix.wiredense);
        MetaItems.addOrePrefix(WFCoreOrePrefix.shell);
        MetaItems.addOrePrefix(WFCoreOrePrefix.plateTriple);
        MetaItems.addOrePrefix(WFCoreOrePrefix.plateSextuple);
        WFCoreMaterialExtraFlags.register();
    }



        @SubscribeEvent
        public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
            // Initialize recipe handlers
            WFCoreRecipeHandler.init();

        }


    }
