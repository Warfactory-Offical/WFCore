package wfcore.api.metatileentity;

import com.google.common.collect.ImmutableMap;
import com.modularmods.mcgltf.IGltfModelReceiver;
import com.modularmods.mcgltf.RenderedGltfModel;
import com.modularmods.mcgltf.RenderedGltfScene;
import com.modularmods.mcgltf.animation.GltfAnimationCreator;
import de.javagl.jgltf.model.AnimationModel;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.util.RelativeDirection;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import wfcore.common.render.AnimationLoop;

import java.util.List;
import java.util.Locale;

public abstract class MteRenderer<T extends MetaTileEntity & IAnimatedMTE> implements IGltfModelReceiver {

    public ImmutableMap<String, AnimationLoop> animations;
    protected RenderedGltfScene renderedScene;

    public static void rotateToFace(EnumFacing face, EnumFacing spin) {
        int angle = spin == EnumFacing.EAST ? 90 : spin == EnumFacing.SOUTH ? 180 : spin == EnumFacing.WEST ? 270 : 0;
        switch (face) {
            case UP -> {
                GlStateManager.scale(-1, 1, 1);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(-angle, 0, 0, 1);
            }
            case DOWN -> {
                GlStateManager.rotate(270.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(spin == EnumFacing.EAST || spin == EnumFacing.WEST ? -angle : angle, 0, 0, 1);
            }
            case EAST -> {
                GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(angle, 0, 0, 1);
            }
            case WEST -> {
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(angle, 0, 0, 1);
            }
            case NORTH -> GlStateManager.rotate(angle, 0, 0, 1);
            case SOUTH -> {
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(angle, 0, 0, 1);
            }
        }
    }

    public static void flip(EnumFacing facing) {
        int fX = facing.getXOffset() == 0 ? 1 : -1;
        int fY = facing.getYOffset() == 0 ? 1 : -1;
        int fZ = facing.getZOffset() == 0 ? 1 : -1;
        GlStateManager.scale(fX, fY, fZ);
    }

    @Override
    public void onReceiveSharedModel(RenderedGltfModel renderedModel) {
        renderedScene = renderedModel.renderedGltfScenes.get(0);
        List<AnimationModel> animationModels = renderedModel.gltfModel.getAnimationModels();
        ImmutableMap.Builder<String, AnimationLoop> animations = ImmutableMap.builder();
        for (AnimationModel animationModel : animationModels) {
            var rawAnim = new AnimationLoop(GltfAnimationCreator.createGltfAnimation(animationModel));
            var name = animationModel.getName().toLowerCase(Locale.ROOT).split("_");
            if (name.length > 1 && name[1].equals("loop"))
                rawAnim.setLoop(true);
            animations.put(animationModel.getName(), rawAnim);
        }
        this.animations = animations.build();
    }

    protected void render(T mte, double x, double y, double z,
                          float partialTicks) {
        var vec3d = mte.getTransform();
        GlStateManager.pushMatrix();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        {


            EnumFacing front = mte.getFrontFacing();
            GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
            GlStateManager.translate(vec3d.x, vec3d.y, vec3d.z);

            if (mte instanceof MultiblockControllerBase controller) {
                EnumFacing upwards = controller.getUpwardsFacing();
                EnumFacing left = RelativeDirection.LEFT.getRelativeFacing(front, upwards, controller.isFlipped());

                if (controller.isFlipped()) flip(left);
                rotateToFace(front, upwards);
            }
            renderGLTF(mte, partialTicks);

        }
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.popMatrix();
    }


    abstract public <T extends MetaTileEntity & IAnimatedMTE> void renderGLTF(T mte, float partialTicks);

}
