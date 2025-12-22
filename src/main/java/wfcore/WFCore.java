package wfcore;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wfcore.api.recipes.MultiblockRadarLogic;
import wfcore.common.proxy.CommonProxy;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.12.2]",
        dependencies = "after:hbm"
)
public class WFCore {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);
    public static final String MODID = "wfcore";
    public static final boolean DEBUG = false;

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

        // all items and blocks from other mods have been loaded in post init, try to get blocks to target
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    // we need to check all placed blocks to see if they are on the radar whitelist
    @SubscribeEvent
    public void blockPlaced(BlockEvent.EntityPlaceEvent event) {
        // only handle on server
        if (event.getWorld().isRemote) {
            return;
        }


    }
}
