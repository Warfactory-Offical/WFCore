package wfcore.common.metatileentities.multi.primitive;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.capability.impl.SteamMultiWorkable;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.gui.widgets.RecipeProgressWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapPrimitiveMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.GTUtility;
import gregtech.client.particle.VanillaParticleEffects;
import gregtech.client.renderer.CubeRendererState;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.cclop.ColourOperation;
import gregtech.client.renderer.cclop.LightMapOperation;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.BloomEffectUtil;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.include.com.google.common.collect.Lists;
import wfcore.api.recipes.WFCoreRecipeMaps;

import java.util.List;

public class MetaTileEntityWarfactoryBlastFurnace extends RecipeMapPrimitiveMultiblockController {

    private void resetTileAbilities() {
        this.inputInventory = new GTItemStackHandler(this, 0);
        this.inputFluidInventory = new FluidTankList(true);
        this.outputInventory = new GTItemStackHandler(this, 0);
        this.outputFluidInventory = new FluidTankList(true);
    }

    private static final TraceabilityPredicate SNOW_PREDICATE = new TraceabilityPredicate(
            bws -> GTUtility.isBlockSnow(bws.getBlockState()));

    public MetaTileEntityWarfactoryBlastFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, WFCoreRecipeMaps.Large_Blast_Furnace);
    }
    @Override
    protected void initializeAbilities() {
        this.inputInventory = new ItemHandlerList(getAbilities((MultiblockAbility.IMPORT_ITEMS)));
        this.outputInventory = new ItemHandlerList(getAbilities((MultiblockAbility.EXPORT_ITEMS)));
        this.outputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
    }
    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.initializeAbilities();
    }
    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityWarfactoryBlastFurnace(metaTileEntityId);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("#XXX#", "#XXX#", "#XXX#", "#####", "#####", "#####", "#####")
                .aisle("XXXXX", "X&&&X", "XX#XX", "#XXX#", "##X##", "##X##", "#XXX#")
                .aisle("XXXXX", "X&&&X", "X###X", "#X#X#", "#X#X#", "#X#X#", "#X#X#")
                .aisle("XXXXX", "X&&&X", "XX#XX", "#XXX#", "##X##", "##X##", "#XXX#")
                .aisle("#XXX#", "#XYX#", "#XXX#", "#####", "#####", "#####", "#####")
                .where('X', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS)))
                .where('#', air())
                .where('&', air().or(SNOW_PREDICATE)) // this won't stay in the structure, and will be broken while
                                                      // running
                .where('Y', selfPredicate())
                .build();
    }


    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.PRIMITIVE_BRICKS;
    }


    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                recipeMapWorkable.isActive(), recipeMapWorkable.isWorkingEnabled());
        if (recipeMapWorkable.isActive() && isStructureFormed()) {
            EnumFacing back = getFrontFacing().getOpposite();
            Matrix4 offset = translation.copy().translate(back.getXOffset(), -0.3, back.getZOffset());
            CubeRendererState op = Textures.RENDER_STATE.get();
            Textures.RENDER_STATE.set(new CubeRendererState(op.layer, CubeRendererState.PASS_MASK, op.world));
            Textures.renderFace(renderState, offset,
                    ArrayUtils.addAll(pipeline, new LightMapOperation(240, 240), new ColourOperation(0xFFFFFFFF)),
                    EnumFacing.UP, Cuboid6.full, TextureUtils.getBlockTexture("lava_still"),
                    BloomEffectUtil.getEffectiveBloomLayer());
            Textures.RENDER_STATE.set(op);
        }
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.PRIMITIVE_BLAST_FURNACE_OVERLAY;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add("You are going to need 2 Busses");
    }


    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public void update() {
        super.update();

        if (this.isActive()) {
            if (getWorld().isRemote) {
                VanillaParticleEffects.PBF_SMOKE.runEffect(this);
            } else {
                damageEntitiesAndBreakSnow();
            }
        }
    }

    private void damageEntitiesAndBreakSnow() {
        BlockPos middlePos = this.getPos();
        middlePos = middlePos.offset(getFrontFacing().getOpposite());
        this.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(middlePos))
                .forEach(entity -> entity.attackEntityFrom(DamageSource.LAVA, 3.0f));

        if (getOffsetTimer() % 10 == 0) {
            IBlockState state = getWorld().getBlockState(middlePos);
            GTUtility.tryBreakSnow(getWorld(), middlePos, state, true);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick() {
        if (this.isActive()) {
            VanillaParticleEffects.defaultFrontEffect(this, 0.3F, EnumParticleTypes.SMOKE_LARGE,
                    EnumParticleTypes.FLAME);
            if (ConfigHolder.machines.machineSounds && GTValues.RNG.nextDouble() < 0.1) {
                BlockPos pos = getPos();
                getWorld().playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                        SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
        }
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }

}
