package wfcore.common.events;

import gregtech.api.unification.material.event.MaterialEvent;
import gregtech.api.unification.material.event.PostMaterialEvent;
import gregtech.common.items.MetaItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import wfcore.api.material.modifications.WFCoreMaterialExtraFlags;
import wfcore.api.material.ore.WFCoreOrePrefix;
import wfcore.api.material.ore.WFCoreRecipeHandler;
import wfcore.common.items.ItemRegistry;
import wfcore.common.materials.WFCoreMaterials;
import wfcore.common.metatileentities.WFCoreMetaTileEntities;
import wfcore.common.recipe.VanillaRecipes;
import wfcore.common.recipe.chain.LargeBlastFurnace;
import wfcore.common.recipe.chain.SteamWiremillRecipes;

public class RegistryEvents {


    @SubscribeEvent(priority = EventPriority.HIGH)
    public void registerMaterials(@NotNull MaterialEvent event) {
        WFCoreMaterials.register();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void postRegisterMaterials(@NotNull PostMaterialEvent event) {
    }

    @SubscribeEvent
    public void registerBlocks(@NotNull RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        WFCoreMetaTileEntities.init();
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        WFCoreRecipeHandler.init();
        SteamWiremillRecipes.init();
        LargeBlastFurnace.init();
        VanillaRecipes.registerCTRecipes(event);
        VanillaRecipes.registerFurnaceRecipes(event);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        ItemRegistry.ITEMS.forEach(registry::register);
    }

    @SubscribeEvent
    public void materialChanges(PostMaterialEvent event) {
        MetaItems.addOrePrefix(WFCoreOrePrefix.billet);
        MetaItems.addOrePrefix(WFCoreOrePrefix.ntmpipe);
        MetaItems.addOrePrefix(WFCoreOrePrefix.wiredense);
        MetaItems.addOrePrefix(WFCoreOrePrefix.shell);
        MetaItems.addOrePrefix(WFCoreOrePrefix.plateTriple);
        MetaItems.addOrePrefix(WFCoreOrePrefix.plateSextuple);
        WFCoreMaterialExtraFlags.register();
    }
}
