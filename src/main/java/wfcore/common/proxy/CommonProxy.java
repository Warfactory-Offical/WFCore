package wfcore.common.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import wfcore.common.events.RegistryEvents;
import wfcore.common.recipe.HBMRecepies;

public class CommonProxy {


    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RegistryEvents());
    }

    public void init(FMLInitializationEvent event) {
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
        HBMRecepies.init(event);
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public final void serverStarting(FMLServerStartingEvent event) {
    }

}
