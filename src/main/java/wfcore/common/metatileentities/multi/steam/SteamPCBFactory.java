package wfcore.common.metatileentities.multi.steam;

import gregtech.api.capability.impl.NotifiableItemStackHandler;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.ProgressWidget.MoveType;
import gregtech.api.metatileentity.MetaTileEntity;
import wfcore.api.capability.InsaneSteamMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandlerModifiable;
import wfcore.api.recipes.WFCoreRecipeMaps;

public class SteamPCBFactory extends InsaneSteamMetaTileEntity {

    public SteamPCBFactory(ResourceLocation metaTileEntityId, boolean isHighPressure) {
        super(metaTileEntityId, WFCoreRecipeMaps.Steam_PCB_Factory_Recipes, Textures.SIFTER_OVERLAY, isHighPressure);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new SteamPCBFactory(metaTileEntityId, isInsanePressure);
    }

    @Override
    protected boolean isBrickedCasing() {
        return true;
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        return new NotifiableItemStackHandler(this, 2, this, false);
    }

    @Override
    protected IItemHandlerModifiable createExportItemHandler() {
        return new NotifiableItemStackHandler(this, 1, this, true);
    }

    @Override
    public ModularUI createUI(EntityPlayer player) {
        return createUITemplate(player)
                .slot(this.importItems, 0, 53, 25, GuiTextures.SLOT_STEAM.get(isInsanePressure),
                        GuiTextures.SLOT_STEAM.get(isInsanePressure))
                .slot(this.importItems, 1, 35, 25, GuiTextures.SLOT_STEAM.get(isInsanePressure),
                        GuiTextures.SLOT_STEAM.get(isInsanePressure))
                .progressBar(workableHandler::getProgressPercent, 79, 26, 20, 16,
                        GuiTextures.PROGRESS_BAR_ARROW_STEAM.get(isInsanePressure), MoveType.HORIZONTAL,
                        workableHandler.getRecipeMap())
                .slot(this.exportItems, 0, 107, 25, true, false, GuiTextures.SLOT_STEAM.get(isInsanePressure))
                .build(getHolder(), player);
    }
}



