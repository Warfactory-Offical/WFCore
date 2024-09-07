package wfcore.common.proxy;

import gregtech.api.GregTechAPI;
import gregtech.api.block.VariantItemBlock;
import gregtech.api.metatileentity.registry.MTEManager;
import gregtech.api.unification.material.event.MaterialEvent;
import gregtech.api.unification.material.event.PostMaterialEvent;
import gregtech.common.items.MetaItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import wfcore.Tags;
import wfcore.WFCore;
import wfcore.api.recipes.WFCoreRecipeMaps;
import wfcore.common.materials.WFCoreMaterials;
import wfcore.common.metatileentities.WFCoreMetaTileEntities;

import java.util.Objects;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = WFCore.MODID)
public class CommonProxy {

    public void preLoad(){
    }

    public void load() {
    }

    @SubscribeEvent
    public static void registerBlocks(@NotNull RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        WFCoreMetaTileEntities.init();


    }

    @SubscribeEvent
    public static void registerItems(@NotNull RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();



    }

    @SubscribeEvent
    public static void registerMTERegistry(MTEManager.MTERegistryEvent event) {
        GregTechAPI.mteManager.createRegistry(Tags.MODID);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerMaterials(@NotNull MaterialEvent event) {
        WFCoreMaterials.register();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void postRegisterMaterials(@NotNull PostMaterialEvent event) {


        //SusyMaterials.removeFlags();
    }

    @SubscribeEvent()
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {

    }

    private static <T extends Block> ItemBlock createItemBlock(T block, Function<T, ItemBlock> producer) {
        ItemBlock itemBlock = producer.apply(block);
        itemBlock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        return itemBlock;
    }
}
