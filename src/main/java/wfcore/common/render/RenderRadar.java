package wfcore.common.render;

import com.modularmods.mcgltf.MCglTF;
import com.modularmods.mcgltf.animation.InterpolatedChannel;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.Animation;
import wfcore.Tags;
import wfcore.api.metatileentity.IAnimatedMTE;
import wfcore.api.metatileentity.MteRenderer;
import wfcore.common.metatileentities.multi.electric.MetaTileEntityRadar;

public class RenderRadar extends MteRenderer<MetaTileEntityRadar> {


    @Override
    public ResourceLocation getModelLocation() {

        return new ResourceLocation(Tags.MODID, "model/radar.glb");
    }


    @Override
    public <T extends MetaTileEntity & IAnimatedMTE> void renderGLTF(T mte, float partialTicks) {
        float time = Animation.getWorldTime(mte.getWorld(), partialTicks);
        var animation = animations.get(mte.getAnimState());
        if (animation != null) {
            animation.update(time);
        }

        if (MCglTF.getInstance().isShaderModActive()) {
            renderedScene.renderForShaderMod();
        } else {
            renderedScene.renderForVanilla();
        }
    }
}
