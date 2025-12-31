package wfcore.common.metatileentities.multi.electric;

import com.google.common.collect.Lists;
import com.hbm.blocks.ModBlocks;
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
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockTurbineCasing;
import gregtech.common.blocks.MetaBlocks;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import wfcore.api.capability.data.IDataStorage;
import wfcore.api.metatileentity.IAnimatedMTE;
import wfcore.api.radar.MultiblockRadarLogic;
import wfcore.api.util.math.ClusterData;
import wfcore.client.render.WFTextures;
import wfcore.common.blocks.BlockBoltableCasing;
import wfcore.common.blocks.BlockMetalSheetCasing;
import wfcore.common.blocks.BlockRegistry;
import wfcore.common.materials.WFCoreMaterials;
import wfcore.common.metatileentities.multi.WFPredicates;

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
    @Getter
    String animState = "idle";
    @Getter
    long animEpoch = 0l;
    private long tickCounter = 0;
    private boolean tryWrite = false;

    public MetaTileEntityRadar(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    // I think this is called when the multiblock is formed and valid, but allows extra checks such as muffler blocking
    @Override
    protected void updateFormedValid() {

    }

    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        // TODO: fix this only ever being called in the multi builder but not rendering in game
        return WFTextures.OVERLAY_RADAR;
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
        super.update();  // YOU MUST DO THIS FOR THE STRUCTURE TO FORM AND OTHER IMPORTANT STUFF TO OCCUR

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

        // no items to process or not allowed to process
        if (inputInventory == null || inputInventory.getSlots() == 0 || !tryWrite) {
            return;
        }

        // check all slots
        for (int slotId = 0; slotId < inputInventory.getSlots(); ++slotId) {
            // check if there is a data storage device/ stack
            var slotStack = inputInventory.getStackInSlot(slotId);  // DO NOT MODIFY STACK
            if (!(slotStack.getItem() instanceof IDataStorage storage)) {
                return;
            }

            // check if we have data to store
            if (logic.lastScan != null) {
                // write the last scan to the data storage item
                var stackToWrite = slotStack.copy();  // create copy that we are allowed to modify
                tryWrite = !storage.writeData(stackToWrite, new ArrayList<>(logic.lastScan));  // update tryWrite on successful writes only
                inputInventory.setStackInSlot(slotId, stackToWrite);
            }
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

    protected BlockPattern createStructurePattern() {
        return
        FactoryBlockPattern.start()
                .aisle("                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "      OOOO      ", "      OOOO      ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                .aisle(" JJJEJ    JEJJJ ", "    H      H    ", "    H      H    ", "    H      H    ", "    H      H    ", "    H      H    ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "      OOOO      ", "    OO    OO    ", "    OO    OO    ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                .aisle(" JHEJJ    JJEHJ ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "    HH    HH    ", "    H H  H H    ", "    H  HH  H    ", "    H  HH  H    ", "    H H  H H    ", "   PPPPPPPPPP   ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "    OOOOOOOO    ", "   O   GG   O   ", "   O   GG   O   ", "       GG       ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                .aisle(" JEJJ      JJEJ ", "  H          H  ", "  H          H  ", "  H          H  ", "  H          H  ", "  H          H  ", "                ", "                ", "                ", "                ", "                ", "  PPJJJJJJJJPP  ", "   JJ      JJ   ", "    H      H    ", "    HH    HH    ", "    H H  H H    ", "    H  HH  H    ", "    H  HH  H    ", "    H H  H H    ", "    HH    HH    ", "    HHHHHHHH    ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "   OOOOOOOOOO   ", "  O          O  ", "  O          O  ", "                ", "       GG       ", "       GG       ", "       GG       ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                .aisle(" EJJ        JJE ", " H            H ", " H            H ", " H            H ", " H            H ", " H            H ", "  H          H  ", "  H          H  ", "  H          H  ", "  H          H  ", "  H          H  ", "  PJJJJJJJJJJP  ", "   JJ      JJ   ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "    H      H    ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PDDP      ", "       PP       ", "                ", "    OOOOOOO     ", "  OOO      OOO  ", " O            O ", " O            O ", "                ", "                ", "                ", "                ", "       GG       ", "       GG       ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                .aisle(" JJ    III   JJ ", "       INI      ", "       INI      ", "       III      ", "                ", "                ", "  H          H  ", "                ", "                ", "                ", "                ", "  PJJJJJJJJJJP  ", "                ", "                ", "   H        H   ", "                ", "                ", "                ", "                ", "   H        H   ", "   H        H   ", "                ", "     HPPMPH     ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PDDP      ", "       PP       ", "                ", "    OOOOOOOO    ", "  OO        OO  ", " O            O ", " O            O ", "                ", "                ", "                ", "                ", "                ", "                ", "       GG       ", "       GG       ", "       GG       ", "                ", "                ", "                ", "                ")
                .aisle("       III      ", "       NAN      ", "       NAN      ", "       III      ", "                ", "                ", "                ", "  H          H  ", "                ", "                ", "  H          H  ", "  PJJJJJJJJJJP  ", "           I    ", "                ", "                ", "   H        H   ", "                ", "                ", "   H        H   ", "                ", "   H        H   ", "                ", "    PPPPMPPP    ", "                ", "                ", "      FFFF      ", "       FF       ", "       FF       ", "       DD       ", "       DD       ", "      OOOO      ", "    OO    OO    ", " OOO        OOO ", "O              O", "O              O", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "       GG       ", "                ", "                ", "                ")
                .aisle("       IMI      ", "       NMN      ", "       NMN      ", "       IMI      ", "        M       ", "        M       ", "        M       ", "        M       ", "  H     M    H  ", "  H     M    H  ", "        M       ", "  PJJJJJMMMMJP  ", "        M  C    ", "        M  L    ", "        M       ", "        M       ", "   H    M   H   ", "   H    M   H   ", "        M       ", "        M       ", "   H    M   H   ", "   H    M   H   ", "    PPPPMPPP    ", "                ", "                ", "      FFFF      ", "       FF       ", "       FF       ", "       DD       ", "       DD       ", "      OOOO      ", "    OO    OO    ", " OOO        OOO ", "O G          G O", "O G          G O", "  G          G  ", "   G        G   ", "   G        G   ", "   G        G   ", "    G      G    ", "    G      G    ", "     G    G     ", "     G    G     ", "     G    G     ", "      G  G      ", "       DD       ", "       DD       ", "                ")
                .aisle("       III      ", "       III      ", "       III      ", "       III      ", "        I       ", "        I       ", "        I       ", "        I       ", "  H     I    H  ", "  H     I    H  ", "        I       ", "  PJJJJJJJJJJP  ", "           I    ", "           K    ", "                ", "                ", "   H        H   ", "   H        H   ", "                ", "                ", "   H        H   ", "   H        H   ", "    PPPPPPPP    ", "                ", "                ", "      FFFF      ", "       FF       ", "       FF       ", "       DD       ", "       DD       ", "      OOOO      ", "    OO    OO    ", " OOO        OOO ", "O G          G O", "O G          G O", "  G          G  ", "   G        G   ", "   G        G   ", "   G        G   ", "    G      G    ", "    G      G    ", "     G    G     ", "     G    G     ", "     G    G     ", "      G  G      ", "       DD       ", "       DD       ", "                ")
                .aisle("                ", "                ", "                ", "                ", "                ", "                ", "                ", "  H          H  ", "                ", "                ", "  H          H  ", "  PJJJJJ JJJJP  ", "           I    ", "                ", "                ", "   H        H   ", "                ", "                ", "   H        H   ", "                ", "   H        H   ", "                ", "    PPPPPPPP    ", "                ", "                ", "      FFFF      ", "       FF       ", "       FF       ", "       DD       ", "       DD       ", "      OOOO      ", "    OO    OO    ", " OOO        OOO ", "O              O", "O              O", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "       GG       ", "                ", "                ", "                ")
                .aisle(" JJ          JJ ", "                ", "                ", "                ", "                ", "                ", "  H          H  ", "                ", "                ", "                ", "                ", "  PJJJJJJJJJJP  ", "                ", "                ", "   H        H   ", "                ", "                ", "                ", "                ", "   H        H   ", "   H        H   ", "                ", "     HPPPPH     ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PDDP      ", "       PP       ", "                ", "    OOOOOOOO    ", "  OO        OO  ", " O            O ", " O            O ", "                ", "                ", "                ", "                ", "                ", "                ", "       GG       ", "       GG       ", "       GG       ", "                ", "                ", "                ", "                ")
                .aisle(" EJJ        JJE ", " H            H ", " H            H ", " H            H ", " H            H ", " H            H ", "  H          H  ", "  H          H  ", "  H          H  ", "  H          H  ", "  H          H  ", "  PJJJJJJJJJJP  ", "   JJ      JJ   ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "    H      H    ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PPPP      ", "      PDDP      ", "       PP       ", "                ", "    OOOOOOOO    ", "  OOO      OOO  ", " O            O ", " O            O ", "                ", "                ", "                ", "                ", "       GG       ", "       GG       ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                .aisle(" JEJJ      JJEJ ", "  H          H  ", "  H          H  ", "  H          H  ", "  H          H  ", "  H          H  ", "                ", "                ", "                ", "                ", "                ", "  PBJJJJJJJJPP  ", "   JJ      JJ   ", "    H      H    ", "    HH    HH    ", "    H H  H H    ", "    H  HH  H    ", "    H  HH  H    ", "    H H  H H    ", "    HH    HH    ", "    HHHHHHHH    ", "       HH       ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "   OOOOOOOOOO   ", "  O          O  ", "  O          O  ", "                ", "       GG       ", "       GG       ", "       GG       ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                .aisle(" JHEJJ    JJEHJ ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "   H        H   ", "    HH    HH    ", "    H H  H H    ", "    H  HH  H    ", "    H  HH  H    ", "    H H  H H    ", "   PPPPPPPPPP   ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "    OOOOOOOO    ", "   O   GG   O   ", "   O   GG   O   ", "       GG       ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                .aisle(" JJJEJ    JEJJJ ", "    H      H    ", "    H      H    ", "    H      H    ", "    H      H    ", "    H      H    ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "      OOOO      ", "    OO    OO    ", "    OO    OO    ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                .aisle("                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "      OOOO      ", "      OOOO      ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                .where('G', frames(Materials.Aluminium))
                .where('O', states(BlockRegistry.BOLTABLE_CASING.getState(BlockBoltableCasing.BoltableCasingType.BORON_COATED_BOLTED)))
                .where('A', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE)))
                .where('E', WFPredicates.compressedBlocks(Materials.Steel))
                .where('F', WFPredicates.compressedBlocks(Materials.Lead))
                .where('M', blocks(ModBlocks.deco_red_copper))
                .where('P', states(BlockRegistry.SHEET_CASING.getState(BlockMetalSheetCasing.MetalSheetCasingType.ALUMINIUM_SHEET_CASING)))
                .where('H', frames(WFCoreMaterials.GalvanizedSteel))
                .where('J', blocks(ModBlocks.concrete_smooth))
                .where('L', blocks(ModBlocks.deco_crt))
                .where('N', autoAbilities())
                .where('B', states(BlockRegistry.SHEET_CASING.getState(BlockMetalSheetCasing.MetalSheetCasingType.ALUMINIUM_SHEET_CASING)))
                .where('I', states(MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_TURBINE_CASING)))
                .where('K', blocks(ModBlocks.deco_crt))
                .where('C', selfPredicate())
                .where('D', WFPredicates.compressedBlocks(Materials.Aluminium))
                .where('#', any())
                .where(' ', air())
                .build();

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

    private void onWriteToggleClick(Widget.ClickData data) {
        tryWrite = !tryWrite;
    }

    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList, this.isStructureFormed()).addCustom(tl -> {
            // get the cluster data to use and set a default
            List<ClusterData> data = logic.lastScan;
            String dataString = new TextComponentTranslation("info.data.no_data").getFormattedText();

            // if data is present, begin converting it, incrementing index every 5 seconds
            if (!data.isEmpty()) {
                int clusterIdx = ((int) tickCounter / 100) % data.size();
                ClusterData targetData = data.get(clusterIdx);
                dataString = targetData.toString();
            }

            // add text
            ITextComponent scanResults = TextComponentUtil.stringWithColor(TextFormatting.AQUA, dataString);
            tl.add(scanResults);
        });
    }

    private void addWriteInfoText(List<ITextComponent> textList) {
        if (tryWrite) {
            textList.add(TextComponentUtil.translationWithColor(TextFormatting.GREEN, "info.data.try_write"));
        } else {
            textList.add(TextComponentUtil.translationWithColor(TextFormatting.RED, "info.data.not_try_write"));
        }
    }

    @Override
    protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI
                .builder(GuiTextures.BORDERED_BACKGROUND, 256/*176*/, 208);
        builder.shouldColor(false);
        builder.image(4, 4, 248, 117, GuiTextures.DISPLAY);
        builder.label(9, 9, getMetaFullName(), 0xFFFFFF);
        builder.widget(new ClickButtonWidget(9, 96, 60, 20, "SCAN", this::onScanClick));
        builder.widget(new ClickButtonWidget(71, 96, 80, 20, "WRITE TOGGLE", this::onWriteToggleClick));
        builder.widget(new AdvancedTextWidget(155, 106, this::addWriteInfoText, 0xFFFFFF));
        builder.widget(new AdvancedTextWidget(9, 20, this::addDisplayText, 0xFFFFFF)
                .setMaxWidthLimit(162)
                .setClickHandler(this::handleDisplayClick));
        builder.widget(new IndicatorImageWidget(232, 101, 17, 17, getLogo())
                .setWarningStatus(getWarningLogo(), this::addWarningText)
                .setErrorStatus(getErrorLogo(), this::addErrorText));
        builder.bindPlayerInventory(entityPlayer.inventory,
                GuiTextures.SLOT, 47, 125);
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
        return new Vec3d(4, 10, 0);
    }

    public BlockPos getLightPos() {
        return thisObject().getPos().up(15);
    }

    @Override
    public boolean shouldRender() {
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

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        logic.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        logic.readFromNBT(data);
    }


}
