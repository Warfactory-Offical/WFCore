package wfcore;

import gregtech.api.GregTechAPI;
import gregtech.api.unification.material.event.MaterialEvent;
import gregtech.core.unification.material.internal.MaterialRegistryManager;
import li.cil.oc.api.Driver;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import wfcore.common.OCDrivers.DriverRadar;
import wfcore.common.materials.WFCoreMaterials;
import wfcore.common.metatileentities.WFCoreMetaTileEntities;
import wfcore.common.proxy.CommonProxy;
import wfcore.common.radarActivity.RadarActivity;
import wfcore.common.recipe.chain.*;

import java.util.concurrent.ConcurrentHashMap;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.12.2]")
public class WFCore {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);
    public static final String MODID = "wfcore";

    public static CommonProxy proxy;

    @EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc. (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        // register to the event bus so that we can listen to events
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("I am " + Tags.MODNAME + " + at version " + Tags.VERSION);
        MaterialEvent materialEvent = new MaterialEvent();

    }


    @SubscribeEvent
    // Register recipes here (Remove if not needed)
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        SwordOfTheDesmosChain.init();
        SteamWiremillRecipes.init();
        CircuitFabricatorRecipes.init();
        LargeBlastFurnace.init();
        StrandCaster.init();
    }

    @SubscribeEvent
    // Register items here (Remove if not needed)
    public void registerItems(RegistryEvent.Register<Item> event) {

    }

    @SubscribeEvent
    // Register blocks here (Remove if not needed)
    public void registerBlocks(RegistryEvent.Register<Block> event) {

    }

    @EventHandler
    // load "Do your mod setup. Build whatever data structures you care about." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
    }

    public static final ConcurrentHashMap<Integer, RadarActivity> dimensionWiseRadar = new ConcurrentHashMap<>(16); // stores

    @EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        Driver.add(new DriverRadar());
    }

    @EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
    }
}
