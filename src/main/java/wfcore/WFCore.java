package wfcore;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wfcore.api.radar.MultiblockRadarLogic;
import wfcore.common.proxy.CommonProxy;
import wfcore.common.recipe.GregtechRecipes;
import wfcore.common.recipe.HBMRecepies;
import wfcore.common.recipe.VanillaRecipes;
import wfcore.common.recipe.chain.LargeBlastFurnace;
import wfcore.common.recipe.chain.SteamWiremillRecipes;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.12.2]",
        dependencies = "after:hbm;after:mcheli;required:gregtech;required-after-client:mcgltf;required-client:ctm"
)
public class WFCore {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);
    public static final String MODID = "wfcore";
    public static final boolean DEBUG = true;

    @SidedProxy(
            clientSide = "wfcore.common.proxy.ClientProxy",
            serverSide = "wfcore.common.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

        // read the radar config file
        MultiblockRadarLogic.readRadarConfig();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    // we need may want to check blocks after they are placed to track them
    @SubscribeEvent
    public void blockPlaced(BlockEvent.EntityPlaceEvent event) {
        // only handle on server
        if (event.getWorld().isRemote) {
            return;
        }


    }

    // we may want to inspect all entities when they are created
    @SubscribeEvent
    public void entityConstructing(EntityEvent.EntityConstructing event) {
        // only handle on server
        if (event.getEntity().getEntityWorld().isRemote) {
            return;
        }
    }

}
