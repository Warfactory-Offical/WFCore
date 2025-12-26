package wfcore.api.metatileentity;

import com.modularmods.mcgltf.IGltfModelReceiver;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.util.RelativeDirection;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.opengl.GL11;

public abstract class MteRenderer<T extends MetaTileEntity & IAnimatedMTE> implements IGltfModelReceiver {


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


    protected void render(T mte, double x, double y, double z,
                          float partialTicks) {
        Vec3i vec3i = mte.getTransform();
        GlStateManager.pushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            );


            EnumFacing front = mte.getFrontFacing();
            GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
            GlStateManager.translate(vec3i.getX(), vec3i.getY(), vec3i.getZ());

            if (mte instanceof MultiblockControllerBase controller) {
                EnumFacing upwards = controller.getUpwardsFacing();
                EnumFacing left = RelativeDirection.LEFT.getRelativeFacing(front, upwards, controller.isFlipped());

                if (controller.isFlipped()) flip(left);
                rotateToFace(front, upwards);
            }
            renderGLTF(mte, partialTicks);

        }
        GL11.glPopAttrib();
        GlStateManager.popMatrix();
    }

    abstract public <T extends MetaTileEntity & IAnimatedMTE> void renderGLTF(T mte, float partialTicks);

}
