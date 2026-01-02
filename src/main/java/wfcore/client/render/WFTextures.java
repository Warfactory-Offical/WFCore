package wfcore.client.render;

import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WFTextures {

    public static  OrientedOverlayRenderer OVERLAY_RADAR;


    @SideOnly(Side.CLIENT)
    public static void registerTextures(){
        OVERLAY_RADAR = new OrientedOverlayRenderer("multiblock/radar");
    }

}
