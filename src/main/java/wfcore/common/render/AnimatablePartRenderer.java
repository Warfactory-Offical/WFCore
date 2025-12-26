package wfcore.common.render;

import com.modularmods.mcgltf.IGltfModelReceiver;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

public class AnimatablePartRenderer<T extends TileEntity & IGltfModelReceiver> extends TileEntitySpecialRenderer<T> {

    public static void rotateBlock(EnumFacing facing) {
        switch (facing) {
            case SOUTH -> GlStateManager.rotate(180, 0, 1, 0);
            case WEST -> GlStateManager.rotate(90, 0, 1, 0);
            case EAST -> GlStateManager.rotate(270, 0, 1, 0);
            case UP -> GlStateManager.rotate(90, 1, 0, 0);
            case DOWN -> GlStateManager.rotate(270, 1, 0, 0);
            default -> {
                /* No rotation needed for north */
            }
        }
    }

    private EnumFacing getFacing(T tile) {
        IBlockState blockState = tile.getWorld().getBlockState(tile.getPos());
        if (blockState.getPropertyKeys().contains(BlockHorizontal.FACING)) {
            return blockState.getValue(BlockHorizontal.FACING);
        } else {
            return blockState.getPropertyKeys().contains(BlockDirectional.FACING) ?
                    blockState.getValue(BlockDirectional.FACING) : EnumFacing.NORTH;
        }
    }

    public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {


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
            GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
            rotateBlock(getFacing(tile));



        }
        GlStateManager.popMatrix();
        GL11.glPopAttrib();
    }


}
