package wfcore.common.render;

import com.modularmods.mcgltf.MCglTF;
import com.modularmods.mcgltf.RenderedGltfModel;
import com.modularmods.mcgltf.RenderedGltfScene;
import com.modularmods.mcgltf.animation.GltfAnimationCreator;
import com.modularmods.mcgltf.animation.InterpolatedChannel;
import de.javagl.jgltf.model.AnimationModel;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.Animation;
import wfcore.Tags;
import wfcore.api.metatileentity.IAnimatedMTE;
import wfcore.api.metatileentity.MteRenderer;
import wfcore.common.metatileentities.multi.electric.MetaTileEntityRadar;

import java.util.ArrayList;
import java.util.List;

public class RenderRadar extends MteRenderer<MetaTileEntityRadar> {

    protected RenderedGltfScene renderedScene;

    protected List<List<InterpolatedChannel>> animations;

     @Override
    public void onReceiveSharedModel(RenderedGltfModel renderedModel) {
        renderedScene = renderedModel.renderedGltfScenes.get(0);
        List<AnimationModel> animationModels = renderedModel.gltfModel.getAnimationModels();
        animations = new ArrayList<List<InterpolatedChannel>>(animationModels.size());
        for(AnimationModel animationModel : animationModels) {
            animations.add(GltfAnimationCreator.createGltfAnimation(animationModel));
        }
    }

    @Override
    public ResourceLocation getModelLocation() {

        return new ResourceLocation(Tags.MODID, "model/radar.glb");
    }


    @Override
    public <T extends MetaTileEntity & IAnimatedMTE> void renderGLTF(T mte, float partialTicks) {
        float time = Animation.getWorldTime(mte.getWorld(), partialTicks);
        for(List<InterpolatedChannel> animation : animations) {
            animation.forEach((channel) -> {
                float[] keys = channel.getKeys();
                channel.update(time % keys[keys.length - 1]);
            });
        }

        if(MCglTF.getInstance().isShaderModActive()) {
            renderedScene.renderForShaderMod();
        }
        else {
            renderedScene.renderForVanilla();
        }
    }
}
