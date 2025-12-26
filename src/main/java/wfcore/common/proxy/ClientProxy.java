package wfcore.common.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import wfcore.common.events.ClientRegistryEvents;

public class ClientProxy extends CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(new ClientRegistryEvents());
    }
}
