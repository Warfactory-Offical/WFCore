package wfcore.api.metatileentity;

import com.modularmods.mcgltf.IGltfModelReceiver;
import com.modularmods.mcgltf.RenderedGltfModel;
import com.modularmods.mcgltf.RenderedGltfScene;
import com.modularmods.mcgltf.animation.GltfAnimationCreator;
import com.modularmods.mcgltf.animation.InterpolatedChannel;
import de.javagl.jgltf.model.AnimationModel;
import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import wfcore.Tags;
import wfcore.common.network.SPacketUpdateRenderMask;
import wfcore.common.te.TERegistry;

import java.util.*;

public interface IAnimatedMTE extends IFastRenderMetaTileEntity {



    public default Vec3i getTransform(){return Vec3i.NULL_VECTOR;};

    Collection<BlockPos> getHiddenBlocks();

    public default String getAnimState(){
       return "default";
    }


    @SuppressWarnings("unchecked")
    default <T extends MetaTileEntity> T thisObject() {
        return (T) this;
    }

    default String getName() {
        return thisObject().metaTileEntityId.getPath();
    }

    // Should only be called on the server side
    default void disableBlockRendering(boolean disable) {
        World world = thisObject().getWorld();
        // Special case for server worlds that exists on client side
        // E.g., TrackedDummyWorld
        // This should at least cover the ones in CEu & MUI2
        if (world.getMinecraftServer() != null) {
            BlockPos pos = thisObject().getPos();
            int dimId = world.provider.getDimension();
            var packet = new SPacketUpdateRenderMask(pos, disable ? getHiddenBlocks() : null, dimId);
            GregTechAPI.networkHandler.sendToDimension(packet, dimId);
        }
    }

    public default boolean shouldRender(){return true;}

    // If this returns true, the TESR will keep rendering even when the chunk is culled.
    @Override
    default boolean isGlobalRenderer() {
        return true;
    }

    @Override
    default void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        if (thisObject().getWorld() == Minecraft.getMinecraft().world && shouldRender()) {
            TERegistry.getRenderer(thisObject().getClass()).render(thisObject(), x,y,z,partialTicks);
        }

    }
}
