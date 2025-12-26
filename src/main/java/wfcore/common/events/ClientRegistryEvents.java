package wfcore.common.events;

import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import wfcore.api.SelfRegisteringModel;

@SideOnly(Side.CLIENT)
public class ClientRegistryEvents {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onModelBake(ModelBakeEvent event) {
        SelfRegisteringModel.bakeModels(event);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPreTextureStitch(TextureStitchEvent.Pre event) {
        SelfRegisteringModel.registerSprites(event.getMap());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRegisterModels(ModelRegistryEvent event) {
        SelfRegisteringModel.registerModels();
        SelfRegisteringModel.registerCustomStateMappers();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemColors(ColorHandlerEvent.Item event) {
        SelfRegisteringModel.registerItemColorHandlers(event);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onBlockColors(ColorHandlerEvent.Block event) {
        SelfRegisteringModel.registerBlockColorHandlers(event);
    }

}
