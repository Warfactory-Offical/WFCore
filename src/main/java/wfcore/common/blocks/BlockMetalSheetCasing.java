package wfcore.common.blocks;

import gregtech.api.GregTechAPI;
import gregtech.api.block.IStateHarvestLevel;
import gregtech.api.block.VariantBlock;
import gregtech.api.items.toolitem.ToolClasses;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;


public class BlockMetalSheetCasing extends VariantBlock<BlockMetalSheetCasing.MetalSheetCasingType> {

    public BlockMetalSheetCasing(String name) {
        super(Material.IRON);
        setTranslationKey(name);
        setRegistryName(name);
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        BlockRegistry.BLOCKS.add(this);
    }


    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    public enum MetalSheetCasingType implements IStringSerializable, IStateHarvestLevel {

        ALUMINIUM_SHEET_CASING("aluminum_sheet", 2);

        private final String name;
        private final int harvestLevel;

        MetalSheetCasingType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public int getHarvestLevel(IBlockState state) {
            return harvestLevel;
        }

        @Override
        public String getHarvestTool(IBlockState state) {
            return ToolClasses.WRENCH;
        }
    }
}

