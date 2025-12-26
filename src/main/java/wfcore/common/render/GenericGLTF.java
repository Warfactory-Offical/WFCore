package wfcore.common.render;

import com.modularmods.mcgltf.MCglTF;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.Animation;
import wfcore.Tags;
import wfcore.api.metatileentity.IAnimatedMTE;
import wfcore.api.metatileentity.MteRenderer;
import wfcore.common.metatileentities.multi.electric.MetaTileEntityRadar;

public class GenericGLTF<T extends MetaTileEntity & IAnimatedMTE> extends MteRenderer<T> {

    public final ResourceLocation modelResource;
    public GenericGLTF(ResourceLocation modelResource) {
        this.modelResource = modelResource;
    }

    @Override
    public ResourceLocation getModelLocation() {

        return modelResource;
    }


    public <T extends MetaTileEntity & IAnimatedMTE> void renderGLTF(T mte, float partialTicks) {
        float worldTimeS = Animation.getWorldTime(mte.getWorld(), partialTicks);
        var animation = animations.get(mte.getAnimState());
        float epochS = mte.getAnimEpoch()/20f;
        float time = worldTimeS - epochS;
        if (animation != null && time >= 0) {
            animation.update(time);
        }

        if (MCglTF.getInstance().isShaderModActive()) {
            renderedScene.renderForShaderMod();
        } else {
            renderedScene.renderForVanilla();
        }
    }
}
