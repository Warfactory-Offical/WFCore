package wfcore.common.metatileentities.multi;

import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.pipenet.tile.IPipeTile;
import gregtech.api.unification.material.Material;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

import static gregtech.api.metatileentity.multiblock.MultiblockControllerBase.states;

public class WFPredicates {

    public static TraceabilityPredicate compressedBlocks(Material... frameMaterials) {
        return states(Arrays.stream(frameMaterials).map(m -> MetaBlocks.COMPRESSED.get(m).getBlock(m))
                .toArray(IBlockState[]::new))
                ;
    }
}
