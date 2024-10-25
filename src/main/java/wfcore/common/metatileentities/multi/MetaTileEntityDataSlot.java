package wfcore.common.metatileentities.multi;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.impl.NotifiableItemStackHandler;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.resources.IGuiTexture;
import gregtech.api.gui.widgets.BlockableSlotWidget;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IDataItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockNotifiablePart;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityObjectHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import wfcore.api.capability.IDataSlot;

import java.util.Iterator;
import java.util.List;

public class MetaTileEntityDataSlot extends MetaTileEntityMultiblockNotifiablePart
        implements IMultiblockAbilityPart<IDataSlot>, IDataSlot {

    private final DataSlotHandler heldData = new DataSlotHandler(this);
    private boolean isLocked;

    public MetaTileEntityDataSlot(ResourceLocation metaTileEntityId, int tier, boolean isExportHatch) {
        super(metaTileEntityId, tier, isExportHatch);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityObjectHolder(this.metaTileEntityId);
    }

    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return ModularUI.defaultBuilder().label(5, 5, this.getMetaFullName()).image(46, 18, 84, 60, GuiTextures.PROGRESS_BAR_RESEARCH_STATION_BASE)
                .widget((new BlockableSlotWidget(this.heldItems, 0, 79, 39))
                        .setIsBlocked(this::isSlotBlocked).setBackgroundTexture(new IGuiTexture[]{GuiTextures.SLOT, GuiTextures.RESEARCH_STATION_OVERLAY}))
                .widget((new BlockableSlotWidget(this.heldItems, 0, 79, 39)).setIsBlocked(this::isSlotBlocked)
                        .setBackgroundTexture(new IGuiTexture[]{GuiTextures.SLOT, GuiTextures.DATA_ORB_OVERLAY}))
                .bindPlayerInventory(entityPlayer.inventory)
                .build(this.getHolder(), entityPlayer);
    }


    private boolean isSlotBlocked() {
        return this.isLocked;
    }

    public void clearMachineInventory(NonNullList<ItemStack> itemBuffer) {
        clearInventory(itemBuffer, this.heldItems);
    }

    @SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        SimpleOverlayRenderer renderer = Textures.OBJECT_HOLDER_OVERLAY;
        MultiblockControllerBase controller = this.getController();
        if (controller != null && controller.isActive()) {
            renderer = Textures.OBJECT_HOLDER_ACTIVE_OVERLAY;
        }

        renderer.renderSided(this.getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public MultiblockAbility<IDataSlot> getAbility() {
        return null;
    }

    @Override
    public void registerAbilities(List<IDataSlot> list) {

    }

    @Override
    public @NotNull ItemStack getDataItem(boolean var1) {
        return null;
    }

    @Override
    public void setDataItem(@NotNull ItemStack var1) {

    }

    @Override
    public void setLocked(boolean var1) {

    }

    @Override
    public @NotNull IItemHandler getAsHandler() {
        return null;
    }

    private class DataSlotHandler extends NotifiableItemStackHandler {
        public DataSlotHandler(MetaTileEntity metaTileEntity) {
            super(metaTileEntity, 1, (MetaTileEntity) null, false);
        }

        public int getSlotSimit(int slot) {
            return 1;
        }

        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return !MetaTileEntityDataSlot.this.isSlotBlocked() ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }

        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            if (stack.isEmpty()) {
                return true;
            } else {
                Item var5 = stack.getItem();
                boolean isDataItem = false;
                if (var5 instanceof MetaItem) {
                    MetaItem<?> metaItem = (MetaItem) var5;
                    Iterator var7 = metaItem.getBehaviours(stack).iterator();

                    while (var7.hasNext()) {
                        IItemBehaviour behaviour = (IItemBehaviour) var7.next();
                        if (behaviour instanceof IDataItem) {
                            isDataItem = true;
                            break;
                        }
                    }
                }
                return isDataItem;

            }

        }


    }
}
