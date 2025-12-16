package wfcore.common;


import gregtech.api.unification.material.event.PostMaterialEvent;
import gregtech.common.items.MetaItems;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import wfcore.api.SelfRegisteringModel;
import wfcore.api.material.modifications.WFCoreMaterialExtraFlags;
import wfcore.api.material.ore.WFCoreOrePrefix;
import wfcore.api.material.ore.WFCoreRecipeHandler;
import wfcore.common.items.ItemRegistry;

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

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onModelBake(ModelBakeEvent event) {
        SelfRegisteringModel.bakeModels(event);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onPreTextureStitch(TextureStitchEvent.Pre event) {
        SelfRegisteringModel.registerSprites(event.getMap());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRegisterModels(ModelRegistryEvent event) {
        SelfRegisteringModel.registerModels();
        SelfRegisteringModel.registerCustomStateMappers();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onItemColors(ColorHandlerEvent.Item event) {
        SelfRegisteringModel.registerItemColorHandlers(event);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onBlockColors(ColorHandlerEvent.Block event) {
        SelfRegisteringModel.registerBlockColorHandlers(event);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        ItemRegistry.ITEMS.forEach(registry::register);
    }

}
