package wfcore.common.metatileentities.multi.electric;
;
import gregtech.api.capability.IObjectHolder;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class MetaTileEntityRadar extends MultiblockWithDisplayBase {
    public MetaTileEntityRadar(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    // I think this is called when the multiblock is formed and valid, but allows extra checks such as muffler blocking
    @Override
    protected void updateFormedValid() {

    }

    // L = bottom (lower) concrete slab, S = Steel Casing, - = Air, N = Stainless Steel Casing,
    // B = ULV Casing (bearing), s = steel frame, n = stainless frame, Z = Controller,
    // R = right facing (looking at controller) top stair
    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        // direction subsequent chars, strings, and aisles travel in relative to controller faces, respectively
        return FactoryBlockPattern.start(RelativeDirection.BACK, RelativeDirection.UP, RelativeDirection.RIGHT)
                .aisle("LSL", "---", "-N-")
                .aisle("SBP", "ZBP", "-n-")
                .aisle("LSL", "-R-", "-s-")
                .where('Z', this.selfPredicate())
                .where('L', states(Blocks.STONE_SLAB.getDefaultState()))
                .where('-', any())
                .where('R', states(topRightCStair()))
                .where('S', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID)))
                .where('N', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STAINLESS_CLEAN)))
                .where('B', states(MetaBlocks.MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.ULV)))
                .where('s', states(MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel)))
                .where('n', states(MetaBlocks.FRAMES.get(Materials.StainlessSteel).getBlock(Materials.StainlessSteel)))
                .where('P', states(MetaBlocks.MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.ULV))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY)
                                .setMinGlobalLimited(1))
                        .or(this.maintenancePredicate())
                                .setExactLimit(1))
                .build();
    }

    private IBlockState topRightCStair() {
        // hopefully this lookup isn't too expensive - would prefer to ref directly, but I don't know where hbm keeps this
        // could also store a static instance, but rather not if this is fine
        var stair = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("hbm", "concrete_smooth_stairs"));
        if (stair == null) { return Blocks.COBBLESTONE.getDefaultState(); }

        return stair.getBlockState().getBaseState();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return sourcePart != null && !(sourcePart instanceof IObjectHolder) ? Textures.COMPUTER_CASING : Textures.ADVANCED_COMPUTER_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityRadar(this.metaTileEntityId);
    }

    // do radar stuff every tick on the server
    @Override
    public void update() {
        // ignore clients
        if (this.getWorld().isRemote) { return; }
    }
}
