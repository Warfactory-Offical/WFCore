package wfcore.api.block;

import com.modularmods.mcgltf.IGltfModelReceiver;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import wfcore.common.te.AnimatablePartTileEntity;

import javax.annotation.Nullable;

public interface IAnimatedTEProvider extends ITileEntityProvider, IGltfModelReceiver {
    PropertyBool ACTIVE = PropertyBool.create("active");

    default Block thisBlock() {
        return (Block) this;
    }

    default String getName() {
        return thisBlock().getTranslationKey();
    }


    @Nullable
    @Override
    default TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
        return new AnimatablePartTileEntity();
    }

    default AxisAlignedBB getRenderBoundingBox(World world, BlockPos pos, int meta) {
        return new AxisAlignedBB(pos);
    }
}
