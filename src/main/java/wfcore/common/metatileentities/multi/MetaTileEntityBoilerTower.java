package wfcore.common.metatileentities.multi;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.IMultiblockController;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.*;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.Widget;
import gregtech.api.gui.Widget.ClickData;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.category.ICategoryOverride;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTLog;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.multi.BoilerType;
import gregtech.core.sound.GTSoundEvents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static gregtech.api.capability.GregtechDataCodes.BOILER_HEAT;
import static gregtech.api.capability.GregtechDataCodes.BOILER_LAST_TICK_STEAM;
import static gregtech.api.util.RelativeDirection.*;

public class MetaTileEntityBoilerTower extends MultiblockWithDisplayBase implements IProgressBarMultiblock {

    public final BoilerType boilerType;
    protected BoilerTowerRecipeLogic recipeLogic;
    private FluidTankList fluidImportInventory;
    private ItemHandlerList itemImportInventory;
    private FluidTankList steamOutputTank;

    private int throttlePercentage = 100;

    public int size;

    public MetaTileEntityBoilerTower(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.boilerType = BoilerType.STEEL;
        this.recipeLogic = new BoilerTowerRecipeLogic(this);
        resetTileAbilities();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityBoilerTower(metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.size = context.getOrDefault("BoilerTowerHeight", 1) - 1;
        initializeAbilities();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        resetTileAbilities();
        this.throttlePercentage = 100;
        this.recipeLogic.invalidate();
    }

    private void initializeAbilities() {
        this.fluidImportInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.itemImportInventory = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.steamOutputTank = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
    }

    private void resetTileAbilities() {
        this.fluidImportInventory = new FluidTankList(true);
        this.itemImportInventory = new ItemHandlerList(Collections.emptyList());
        this.steamOutputTank = new FluidTankList(true);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive())
                .addCustom(tl -> {
                    if (isStructureFormed()) {
                        // Steam Output line
                        ITextComponent steamOutput = TextComponentUtil.stringWithColor(
                                TextFormatting.AQUA,
                                TextFormattingUtil.formatNumbers(recipeLogic.getLastTickSteam()) + " L/t");

                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gregtech.multiblock.large_boiler.steam_output",
                                steamOutput));

                        // Efficiency line
                        ITextComponent efficiency = TextComponentUtil.stringWithColor(
                                getNumberColor(recipeLogic.getHeatScaled()),
                                recipeLogic.getHeatScaled() + "%");

                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gregtech.multiblock.large_boiler.efficiency",
                                efficiency));

                        // Throttle line
                        ITextComponent throttle = TextComponentUtil.stringWithColor(
                                getNumberColor(getThrottle()),
                                getThrottle() + "%");

                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gregtech.multiblock.large_boiler.throttle",
                                throttle));

                        ITextComponent componentHeight = TextComponentUtil.stringWithColor(TextFormatting.BLUE,
                                String.valueOf((this.size + 1)));

                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gregtech.machine.boiler_tower.height",
                                componentHeight));
                    }
                })
                .addWorkingStatusLine();
    }

    private TextFormatting getNumberColor(int number) {
        if (number == 0) {
            return TextFormatting.DARK_RED;
        } else if (number <= 40) {
            return TextFormatting.RED;
        } else if (number < 100) {
            return TextFormatting.YELLOW;
        } else {
            return TextFormatting.GREEN;
        }
    }

    @Override
    protected void addWarningText(List<ITextComponent> textList) {
        super.addWarningText(textList);
        if (isStructureFormed()) {
            int[] waterAmount = getWaterAmount();
            if (waterAmount[0] == 0) {
                textList.add(TextComponentUtil.translationWithColor(TextFormatting.YELLOW,
                        "gregtech.multiblock.large_boiler.no_water"));
                textList.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                        "gregtech.multiblock.large_boiler.explosion_tooltip"));
            }
        }
    }

    @Override
    protected @NotNull Widget getFlexButton(int x, int y, int width, int height) {
        WidgetGroup group = new WidgetGroup(x, y, width, height);
        group.addWidget(new ClickButtonWidget(0, 0, 9, 18, "", this::decrementThrottle)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_MINUS)
                .setTooltipText("gregtech.multiblock.large_boiler.throttle_decrement"));
        group.addWidget(new ClickButtonWidget(9, 0, 9, 18, "", this::incrementThrottle)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_PLUS)
                .setTooltipText("gregtech.multiblock.large_boiler.throttle_increment"));
        return group;
    }

    private void incrementThrottle(ClickData clickData) {
        this.throttlePercentage = MathHelper.clamp(throttlePercentage + 5, 25, 100);
    }

    private void decrementThrottle(ClickData clickData) {
        this.throttlePercentage = MathHelper.clamp(throttlePercentage - 5, 25, 100);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && recipeLogic.isActive() && recipeLogic.isWorkingEnabled();
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RIGHT, FRONT, UP)
                .aisle("YSY", "YYY", "YYY")
                .aisle("XXX", "XIX", "XXX").setRepeatable(1, 11)
                .aisle("XXX", "XXX", "XXX")
                .where('S', selfPredicate())
                .where('Y', states(MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setExactLimit(1))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setExactLimit(1))
                        .or(abilities(MultiblockAbility.MUFFLER_HATCH))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH)))
                .where('X', states(getCasingState())
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxLayerLimited(1, 1)))
                .where('I', isIndicatorPredicate())
                .build();
    }

    public static TraceabilityPredicate isIndicatorPredicate() {
        return new TraceabilityPredicate((blockWorldState) -> {
            if (air().test(blockWorldState)) {
                blockWorldState.getMatchContext().increment("BoilerTowerHeight", 1);
                return true;
            } else
                return false;
        });
    }



    protected IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }


    @Override
    public String[] getDescription() {
        return new String[] { I18n.format("gregtech.multiblock.large_boiler.description") };
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.multiblock.large_boiler.rate_tooltip",
                (int) (boilerType.steamPerTick() * 20 * boilerType.runtimeBoost(20) / 20.0)));
        tooltip.add(
                I18n.format("gregtech.multiblock.large_boiler.heat_time_tooltip", boilerType.getTicksToBoiling() / 20));
        tooltip.add(I18n.format("gregtech.universal.tooltip.base_production_fluid", boilerType.steamPerTick()));
        tooltip.add(TooltipHelper.BLINKING_RED + I18n.format("gregtech.multiblock.large_boiler.explosion_tooltip"));
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), isActive(),
                recipeLogic.isWorkingEnabled());
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return boilerType.frontOverlay;
    }

    private boolean isFireboxPart(IMultiblockPart sourcePart) {
        return isStructureFormed() && (((MetaTileEntity) sourcePart).getPos().getY() < getPos().getY());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart != null && isFireboxPart(sourcePart)) {
            return isActive() ? boilerType.fireboxActiveRenderer : boilerType.fireboxIdleRenderer;
        }
        return boilerType.casingRenderer;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return true;
    }

    @Override
    public SoundEvent getSound() {
        return GTSoundEvents.BOILER;
    }

    @Override
    protected void updateFormedValid() {
        this.recipeLogic.update();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("ThrottlePercentage", throttlePercentage);
        data.setInteger("size", this.size);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        throttlePercentage = data.getInteger("ThrottlePercentage");
        this.size = data.getInteger("size");
        super.readFromNBT(data);
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeVarInt(throttlePercentage);
        buf.writeInt(this.size);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        throttlePercentage = buf.readVarInt();
        this.size = buf.readInt();
    }

    public int getThrottle() {
        return throttlePercentage;
    }

    @Override
    public IItemHandlerModifiable getImportItems() {
        return itemImportInventory;
    }

    @Override
    public FluidTankList getImportFluids() {
        return fluidImportInventory;
    }

    @Override
    public FluidTankList getExportFluids() {
        return steamOutputTank;
    }

    @Override
    protected boolean shouldUpdate(MTETrait trait) {
        return !(trait instanceof BoilerRecipeLogic);
    }

    @Override
    protected boolean shouldShowVoidingModeButton() {
        return false;
    }

    @Override
    public double getFillPercentage(int index) {
        if (!isStructureFormed()) return 0;
        int[] waterAmount = getWaterAmount();
        if (waterAmount[1] == 0) return 0; // no water capacity
        return (1.0 * waterAmount[0]) / waterAmount[1];
    }

    @Override
    public TextureArea getProgressBarTexture(int index) {
        return GuiTextures.PROGRESS_BAR_FLUID_RIG_DEPLETION;
    }

    @Override
    public void addBarHoverText(List<ITextComponent> hoverList, int index) {
        if (!isStructureFormed()) {
            hoverList.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                    "gregtech.multiblock.invalid_structure"));
        } else {
            int[] waterAmount = getWaterAmount();
            if (waterAmount[0] == 0) {
                hoverList.add(TextComponentUtil.translationWithColor(TextFormatting.YELLOW,
                        "gregtech.multiblock.large_boiler.no_water"));
            } else {
                ITextComponent waterInfo = TextComponentUtil.translationWithColor(
                        TextFormatting.BLUE,
                        "%s / %s L",
                        waterAmount[0], waterAmount[1]);
                hoverList.add(TextComponentUtil.translationWithColor(
                        TextFormatting.GRAY,
                        "gregtech.multiblock.large_boiler.water_bar_hover",
                        waterInfo));
            }
        }
    }

    /**
     * Returns an int[] of {AmountFilled, Capacity} where capacity is the sum of hatches with some water in them.
     * If there is no water in the boiler (or the structure isn't formed, both of these values will be zero.
     */
    private int[] getWaterAmount() {
        if (!isStructureFormed()) return new int[] { 0, 0 };
        List<IFluidTank> tanks = getAbilities(MultiblockAbility.IMPORT_FLUIDS);
        int filled = 0, capacity = 0;
        for (IFluidTank tank : tanks) {
            if (tank == null || tank.getFluid() == null) continue;
            if (CommonFluidFilters.BOILER_FLUID.test(tank.getFluid())) {
                filled += tank.getFluidAmount();
                capacity += tank.getCapacity();
            }
        }
        return new int[] { filled, capacity };
    }

    public class BoilerTowerRecipeLogic extends AbstractRecipeLogic implements ICategoryOverride {

        private static final long STEAM_PER_WATER = 160;

        private static final int FLUID_DRAIN_MULTIPLIER = 100;
        private static final int FLUID_BURNTIME_TO_EU = 800 / FLUID_DRAIN_MULTIPLIER;

        private int currentHeat;
        private int lastTickSteamOutput;
        private int excessWater, excessFuel, excessProjectedEU;

        public BoilerTowerRecipeLogic(MetaTileEntityBoilerTower tileEntity) {
            super(tileEntity, null);
            this.fluidOutputs = Collections.emptyList();
            this.itemOutputs = NonNullList.create();
        }

        @Override
        public int getParallelLimit() {
            return ((MetaTileEntityBoilerTower) this.getMetaTileEntity()).size;
        }

        @Override
        public void update() {
            if ((!isActive() || !canProgressRecipe() || !isWorkingEnabled()) && currentHeat > 0) {
                setHeat(currentHeat - 1);
                setLastTickSteam(0);
            }
            super.update();
        }


        @Override
        protected boolean canProgressRecipe() {
            return super.canProgressRecipe() && !(metaTileEntity instanceof IMultiblockController &&
                    ((IMultiblockController) metaTileEntity).isStructureObstructed());
        }

        @Override
        protected void trySearchNewRecipe() {
            MetaTileEntityBoilerTower boiler = (MetaTileEntityBoilerTower) metaTileEntity;
            if (ConfigHolder.machines.enableMaintenance && boiler.hasMaintenanceMechanics() &&
                    boiler.getNumMaintenanceProblems() > 5) {
                return;
            }

            // can optimize with an override of checkPreviousRecipe() and a check here

            IMultipleTankHandler importFluids = boiler.getImportFluids();
            List<ItemStack> dummyList = NonNullList.create();
            boolean didStartRecipe = false;

            for (IFluidTank fluidTank : importFluids.getFluidTanks()) {
                FluidStack fuelStack = fluidTank.drain(Integer.MAX_VALUE, false);
                if (fuelStack == null || CommonFluidFilters.BOILER_FLUID.test(fuelStack)) continue;

                Recipe dieselRecipe = RecipeMaps.COMBUSTION_GENERATOR_FUELS.findRecipe(
                        GTValues.V[GTValues.MAX], dummyList, Collections.singletonList(fuelStack));
                // run only if it can apply a certain amount of "parallel", this is to mitigate int division
                if (dieselRecipe != null &&
                        fuelStack.amount >= dieselRecipe.getFluidInputs().get(0).getAmount() * FLUID_DRAIN_MULTIPLIER) {
                    fluidTank.drain(dieselRecipe.getFluidInputs().get(0).getAmount() * FLUID_DRAIN_MULTIPLIER, true);
                    // divide by 2, as it is half burntime for combustion
                    setMaxProgress(adjustBurnTimeForThrottle(Math.max(1, boiler.boilerType.runtimeBoost(
                            (Math.abs(dieselRecipe.getEUt()) * dieselRecipe.getDuration()) / FLUID_BURNTIME_TO_EU / 2))));
                    didStartRecipe = true;
                    break;
                }

                Recipe denseFuelRecipe = RecipeMaps.SEMI_FLUID_GENERATOR_FUELS.findRecipe(
                        GTValues.V[GTValues.MAX], dummyList, Collections.singletonList(fuelStack));
                // run only if it can apply a certain amount of "parallel", this is to mitigate int division
                if (denseFuelRecipe != null &&
                        fuelStack.amount >= denseFuelRecipe.getFluidInputs().get(0).getAmount() * FLUID_DRAIN_MULTIPLIER) {
                    fluidTank.drain(denseFuelRecipe.getFluidInputs().get(0).getAmount() * FLUID_DRAIN_MULTIPLIER, true);
                    // multiply by 2, as it is 2x burntime for semi-fluid
                    setMaxProgress(adjustBurnTimeForThrottle(
                            Math.max(1, boiler.boilerType.runtimeBoost((Math.abs(denseFuelRecipe.getEUt()) *
                                    denseFuelRecipe.getDuration() / FLUID_BURNTIME_TO_EU * 2)))));
                    didStartRecipe = true;
                    break;
                }
            }

            if (!didStartRecipe) {
                IItemHandlerModifiable importItems = boiler.getImportItems();
                for (int i = 0; i < importItems.getSlots(); i++) {
                    ItemStack stack = importItems.getStackInSlot(i);
                    int fuelBurnTime = (int) Math.ceil(TileEntityFurnace.getItemBurnTime(stack));
                    if (fuelBurnTime / 10 > 0) { // try to ensure this fuel can burn for at least 1 tick
                        if (FluidUtil.getFluidHandler(stack) != null) continue;
                        this.excessFuel += fuelBurnTime % 10;
                        int excessProgress = this.excessFuel / 10;
                        this.excessFuel %= 10;
                        setMaxProgress(excessProgress +
                                adjustBurnTimeForThrottle(6 * fuelBurnTime / (10 * (size + 1))));
                        stack.shrink(1);
                        didStartRecipe = true;
                        break;
                    }
                }
            }
            if (didStartRecipe) {
                this.progressTime = 1;
                this.recipeEUt = adjustEUtForThrottle(150) * (size + 1);
                if (wasActiveAndNeedsUpdate) {
                    wasActiveAndNeedsUpdate = false;
                } else {
                    setActive(true);
                }
            }
            metaTileEntity.getNotifiedItemInputList().clear();
            metaTileEntity.getNotifiedFluidInputList().clear();
        }

        @Override
        protected void updateRecipeProgress() {
            if (canRecipeProgress) {
                int generatedSteam = this.recipeEUt * getMaximumHeatFromMaintenance() / getMaximumHeat();
                if (generatedSteam > 0) {
                    long amount = (generatedSteam + STEAM_PER_WATER) / STEAM_PER_WATER;
                    excessWater += amount * STEAM_PER_WATER - generatedSteam;
                    amount -= excessWater / STEAM_PER_WATER;
                    excessWater %= STEAM_PER_WATER;

                    FluidStack drainedWater = getBoilerFluidFromContainer(getInputTank(), (int) amount);
                    if (amount != 0 && (drainedWater == null || drainedWater.amount < amount)) {
                        //getMetaTileEntity().explodeMultiblock((1.0f * currentHeat / getMaximumHeat()) * 8.0f);
                    } else {
                        setLastTickSteam(generatedSteam);
                        getOutputTank().fill(Materials.Steam.getFluid(generatedSteam), true);
                    }
                }
                if (currentHeat < getMaximumHeat()) {
                    setHeat(currentHeat + 1);
                }

                if (++progressTime > maxProgressTime) {
                    completeRecipe();
                }
            }
        }

        private int getMaximumHeatFromMaintenance() {
            if (ConfigHolder.machines.enableMaintenance) {
                return (int) Math.min(currentHeat,
                        (1 - 0.1 * getMetaTileEntity().getNumMaintenanceProblems()) * getMaximumHeat());
            } else return currentHeat;
        }

        private int adjustEUtForThrottle(int rawEUt) {
            int throttle = ((MetaTileEntityBoilerTower) metaTileEntity).getThrottle();
            return Math.max(25, (int) (rawEUt * (throttle / 100.0)));
        }

        private int adjustBurnTimeForThrottle(int rawBurnTime) {
            MetaTileEntityBoilerTower boiler = (MetaTileEntityBoilerTower) metaTileEntity;
            int EUt = 180 * (size + 1);
            int adjustedEUt = adjustEUtForThrottle(EUt);
            int adjustedBurnTime = rawBurnTime * EUt / adjustedEUt;
            this.excessProjectedEU += EUt * rawBurnTime - adjustedEUt * adjustedBurnTime;
            adjustedBurnTime += this.excessProjectedEU / adjustedEUt;
            this.excessProjectedEU %= adjustedEUt;
            return adjustedBurnTime;
        }

        private int getMaximumHeat() {
            return ((MetaTileEntityBoilerTower) metaTileEntity).boilerType.getTicksToBoiling();
        }

        public int getHeatScaled() {
            return (int) Math.round(currentHeat / (1.0 * getMaximumHeat()) * 100);
        }

        public void setHeat(int heat) {
            if (heat != this.currentHeat && !metaTileEntity.getWorld().isRemote) {
                writeCustomData(BOILER_HEAT, b -> b.writeVarInt(heat));
            }
            this.currentHeat = heat;
        }

        public int getLastTickSteam() {
            return lastTickSteamOutput;
        }

        public void setLastTickSteam(int lastTickSteamOutput) {
            if (lastTickSteamOutput != this.lastTickSteamOutput && !metaTileEntity.getWorld().isRemote) {
                writeCustomData(BOILER_LAST_TICK_STEAM, b -> b.writeVarInt(lastTickSteamOutput));
            }
            this.lastTickSteamOutput = lastTickSteamOutput;
        }

        @Override
        public int getInfoProviderEUt() {
            return this.lastTickSteamOutput;
        }

        @Override
        public boolean consumesEnergy() {
            return false;
        }

        public void invalidate() {
            progressTime = 0;
            maxProgressTime = 0;
            recipeEUt = 0;
            setActive(false);
            setLastTickSteam(0);
        }

        @Override
        protected void completeRecipe() {
            progressTime = 0;
            setMaxProgress(0);
            recipeEUt = 0;
            wasActiveAndNeedsUpdate = true;
        }

        @NotNull
        @Override
        public MetaTileEntityBoilerTower getMetaTileEntity() {
            return (MetaTileEntityBoilerTower) super.getMetaTileEntity();
        }

        @NotNull
        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = super.serializeNBT();
            compound.setInteger("Heat", currentHeat);
            compound.setInteger("ExcessFuel", excessFuel);
            compound.setInteger("ExcessWater", excessWater);
            compound.setInteger("ExcessProjectedEU", excessProjectedEU);
            return compound;
        }

        @Override
        public void deserializeNBT(@NotNull NBTTagCompound compound) {
            super.deserializeNBT(compound);
            this.currentHeat = compound.getInteger("Heat");
            this.excessFuel = compound.getInteger("ExcessFuel");
            this.excessWater = compound.getInteger("ExcessWater");
            this.excessProjectedEU = compound.getInteger("ExcessProjectedEU");
        }

        @Override
        public void writeInitialSyncData(@NotNull PacketBuffer buf) {
            super.writeInitialSyncData(buf);
            buf.writeVarInt(currentHeat);
            buf.writeVarInt(lastTickSteamOutput);
        }

        @Override
        public void receiveInitialSyncData(@NotNull PacketBuffer buf) {
            super.receiveInitialSyncData(buf);
            this.currentHeat = buf.readVarInt();
            this.lastTickSteamOutput = buf.readVarInt();
        }

        @Override
        public void receiveCustomData(int dataId, @NotNull PacketBuffer buf) {
            super.receiveCustomData(dataId, buf);
            if (dataId == BOILER_HEAT) {
                this.currentHeat = buf.readVarInt();
            } else if (dataId == BOILER_LAST_TICK_STEAM) {
                this.lastTickSteamOutput = buf.readVarInt();
            }
        }

        // Required overrides to use RecipeLogic, but all of them are redirected by the above overrides.

        @Override
        protected long getEnergyInputPerSecond() {
            GTLog.logger.error("Large Boiler called getEnergyInputPerSecond(), this should not be possible!");
            return 0;
        }

        @Override
        protected long getEnergyStored() {
            GTLog.logger.error("Large Boiler called getEnergyStored(), this should not be possible!");
            return 0;
        }

        @Override
        protected long getEnergyCapacity() {
            GTLog.logger.error("Large Boiler called getEnergyCapacity(), this should not be possible!");
            return 0;
        }

        @Override
        protected boolean drawEnergy(int recipeEUt, boolean simulate) {
            GTLog.logger.error("Large Boiler called drawEnergy(), this should not be possible!");
            return false;
        }

        @Override
        public long getMaxVoltage() {
            GTLog.logger.error("Large Boiler called getMaxVoltage(), this should not be possible!");
            return 0;
        }

        /**
         * @param fluidHandler the handler to drain from
         * @param amount       the amount to drain
         * @return a valid boiler fluid from a container
         */
        @Nullable
        private static FluidStack getBoilerFluidFromContainer(@NotNull IFluidHandler fluidHandler, int amount) {
            if (amount == 0) return null;
            FluidStack drainedWater = fluidHandler.drain(Materials.Water.getFluid(amount), true);
            if (drainedWater == null || drainedWater.amount == 0) {
                drainedWater = fluidHandler.drain(Materials.DistilledWater.getFluid(amount), true);
            }
            if (drainedWater == null || drainedWater.amount == 0) {
                for (String fluidName : ConfigHolder.machines.boilerFluids) {
                    Fluid fluid = FluidRegistry.getFluid(fluidName);
                    if (fluid != null) {
                        drainedWater = fluidHandler.drain(new FluidStack(fluid, amount), true);
                        if (drainedWater != null && drainedWater.amount > 0) {
                            break;
                        }
                    }
                }
            }
            return drainedWater;
        }

        @Override
        public @NotNull RecipeMap<?> @NotNull [] getJEIRecipeMapCategoryOverrides() {
            return new RecipeMap<?>[] { RecipeMaps.COMBUSTION_GENERATOR_FUELS, RecipeMaps.SEMI_FLUID_GENERATOR_FUELS };
        }

        @Override
        public @NotNull String @NotNull [] getJEICategoryOverrides() {
            return new String[] { "minecraft.fuel" };
        }
    }
}
