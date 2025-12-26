package wfcore.common.metatileentities.multi.electric;

import com.google.common.collect.Lists;
import com.modularmods.mcgltf.RenderedGltfScene;
import com.modularmods.mcgltf.animation.InterpolatedChannel;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.IObjectHolder;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.IndicatorImageWidget;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.GTUtility;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import wfcore.api.metatileentity.IAnimatedMTE;
import wfcore.api.radar.MultiblockRadarLogic;
import wfcore.api.util.math.ClusterData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

;

public class MetaTileEntityRadar extends MultiblockWithDisplayBase implements IAnimatedMTE {
    private final MultiblockRadarLogic logic = new MultiblockRadarLogic(this);  // this should be created/modified whenever structure is formed/modified

    protected IItemHandlerModifiable inputInventory;
    protected IItemHandlerModifiable outputInventory;
    protected IMultipleTankHandler inputFluidInventory;
    protected IMultipleTankHandler outputFluidInventory;
    protected IEnergyContainer energyContainer;


    private long tickCounter = 0;

    public MetaTileEntityRadar(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    // I think this is called when the multiblock is formed and valid, but allows extra checks such as muffler blocking
    @Override
    protected void updateFormedValid() {

    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        // TODO: fix this only ever being called in the multi builder but not rendering in game
        return Textures.BLAST_FURNACE_OVERLAY;
    }

    // L = bottom (lower) concrete slab, S = Steel Casing, - = Air, N = Stainless Steel Casing,
    // B = ULV Casing (bearing), s = steel frame, n = stainless frame, Z = Controller,
    // R = right facing (looking at controller) top stair
    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        // direction subsequent chars, strings, and aisles travel in relative to controller faces, respectively
        return FactoryBlockPattern.start(RelativeDirection.FRONT, RelativeDirection.UP, RelativeDirection.RIGHT)
                .aisle("ZPP")
                .where('Z', selfPredicate())
                .where('P', states(MetaBlocks.MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.ULV))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY)
                                .setMinGlobalLimited(1))
                        .or(this.maintenancePredicate())
                        .setExactLimit(1))
                .build();
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
        if (this.getWorld().isRemote) {
            return;
        }

        // update tick counter
        ++tickCounter;
        tickCounter &= 1L << 63;

        // only update once a second
        if (tickCounter % 20 != 0) {
            return;
        }

        if (!isStructureFormed()) {
            checkStructurePattern();
        }
    }

    @Override
    public void checkStructurePattern() {
        super.checkStructurePattern();
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
        logic.structureFormed();
    }


    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        resetTileAbilities();
        logic.invalidateStructure();
    }

    public int getTier() {
        if (energyContainer == null) {
            return -1;
        }

        return GTUtility.getTierByVoltage(energyContainer.getInputVoltage());
    }

    @Override
    public boolean isActive() {
        return logic != null && logic.isActive();
    }

    protected void initializeAbilities() {
        this.inputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.inputFluidInventory = new FluidTankList(true,
                getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
        this.outputFluidInventory = new FluidTankList(true,
                getAbilities(MultiblockAbility.EXPORT_FLUIDS));
        this.energyContainer = new EnergyContainerList(getAbilities(MultiblockAbility.INPUT_ENERGY));
    }

    private void resetTileAbilities() {
        this.inputInventory = new GTItemStackHandler(this, 0);
        this.inputFluidInventory = new FluidTankList(true);
        this.outputInventory = new GTItemStackHandler(this, 0);
        this.outputFluidInventory = new FluidTankList(true);
        this.energyContainer = new EnergyContainerList(Lists.newArrayList());
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    private void onScanClick(Widget.ClickData data) {
        logic.performScan();
    }

    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList, this.isStructureFormed()).addCustom(tl -> {
            // get the cluster data to use and set a default
            List<ClusterData> data = logic.accessScanResults(null);
            String dataString = "NO DATA";

            // if data is present, begin converting it, incrementing index every 5 seconds
            if (data.size() > 0) {
                int clusterIdx = ((int) tickCounter / 100) % data.size();
                ClusterData targetData = data.get(clusterIdx);
                dataString = targetData.toString();
            }

            // add text
            ITextComponent scanResults = TextComponentUtil.stringWithColor(TextFormatting.AQUA, dataString);
            tl.add(scanResults);
        });
    }

    @Override
    protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI
                .builder(GuiTextures.BORDERED_BACKGROUND, 176, 208);
        builder.shouldColor(false);
        builder.image(4, 4, 168, 117, GuiTextures.DISPLAY);
        builder.label(9, 9, getMetaFullName(), 0xFFFFFF);
        builder.widget(new ClickButtonWidget(9, 90, 60, 20, "SCAN", this::onScanClick));
        builder.widget(new AdvancedTextWidget(9, 20, this::addDisplayText, 0xFFFFFF)
                .setMaxWidthLimit(162)
                .setClickHandler(this::handleDisplayClick));
        builder.widget(new IndicatorImageWidget(152, 101, 17, 17, getLogo())
                .setWarningStatus(getWarningLogo(), this::addWarningText)
                .setErrorStatus(getErrorLogo(), this::addErrorText));
        builder.bindPlayerInventory(entityPlayer.inventory,
                GuiTextures.SLOT, 7, 125);
        return builder;
    }


    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        BlockPos p = getPos();
        AxisAlignedBB bb = new AxisAlignedBB(
                p.getX() - 10, p.getY(),
                p.getZ() - 10,
                p.getX() + 10, p.getY() + 1,
                p.getZ() + 10
        );
        return bb;
    }

    @Override
    public Vec3d getTransform() {
        return new Vec3d(3.5, 9.5, 0);
    }

    public String getAnimState(){
        return "Idle";
    }


    @Override
    public boolean shouldRender() {
//        return isStructureFormed();
        return true;
    }
    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }
    @Override
    public Collection<BlockPos> getHiddenBlocks() {
        return new ArrayList<>();
    }
}
