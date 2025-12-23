package wfcore.common.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import wfcore.api.radar.RadarTargetIdentifier;

import javax.annotation.Nullable;
import java.util.List;

public class RadarProbe extends BaseItem {
    public RadarProbe(String s, String texturePath) {
        super(s, texturePath);
    }

    // called on any right click
    /*
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack heldStack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) { return new ActionResult<>(EnumActionResult.PASS, heldStack); }
        RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, false);

        // intellij doesn't know what it's talking about; this can definitely occur
        if (raytraceresult == null) {
            playerIn.sendMessage(new TextComponentString("Got a null raytrace; returning").setStyle(new Style().setColor(TextFormatting.RED)));
            return new ActionResult<>(EnumActionResult.FAIL, heldStack);
        }

        // this method does not handle anything other than entity hits
        if (raytraceresult.typeOfHit != RayTraceResult.Type.ENTITY) {
            playerIn.sendMessage(new TextComponentString("Got a type of hit which was not an entity: " + raytraceresult.typeOfHit).setStyle(new Style().setColor(TextFormatting.RED)));
            return new ActionResult<>(EnumActionResult.PASS, heldStack);
        }

        // get the entity key
        ResourceLocation entityKey = EntityList.getKey(raytraceresult.entityHit);
        if (entityKey == null) { entityKey = new ResourceLocation("NULL"); }

        // get the entity string
        String entityString = EntityList.getEntityString(raytraceresult.entityHit);
        if (entityString == null) { entityString = "NULL"; }

        String entityTranslationKey = EntityList.getTranslationName(entityKey);

        ITextComponent translatedName = new TextComponentString("NULL");
        if (entityTranslationKey != null) { translatedName = new TextComponentTranslation(entityTranslationKey); }
        else { entityTranslationKey = "NULL"; }

        // send the display name
        playerIn.sendMessage(new TextComponentString("\nTarget entity's display name is:"));
        playerIn.sendMessage(translatedName.setStyle(new Style().setColor(TextFormatting.AQUA)));
        playerIn.sendMessage(new TextComponentString("\nTarget entity's string is:"));
        playerIn.sendMessage(new TextComponentString(entityString).setStyle(new Style().setColor(TextFormatting.AQUA)));
        playerIn.sendMessage(new TextComponentString("\nTarget entity's translation key is:"));
        playerIn.sendMessage(new TextComponentString(entityTranslationKey).setStyle(new Style().setColor(TextFormatting.AQUA)));

        // new line
        playerIn.sendMessage(new TextComponentString(""));

        return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
    } */

    // called when an entity is clicked (presumably something extending entitylivingbase only?)
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        // get the entity key
        ResourceLocation entityKey = EntityList.getKey(target);
        if (entityKey == null) { entityKey = new ResourceLocation("NULL"); }

        // get the entity string
        String entityString = EntityList.getEntityString(target);
        if (entityString == null) { entityString = "NULL"; }

        String entityTranslationKey = EntityList.getTranslationName(entityKey);

        ITextComponent translatedName = new TextComponentString("NULL");
        if (entityTranslationKey != null) { translatedName = new TextComponentTranslation(entityTranslationKey); }
        else { entityTranslationKey = "NULL"; }

        // send the display name
        player.sendMessage(new TextComponentString("\nTarget entity's display name is:"));
        player.sendMessage(translatedName.setStyle(new Style().setColor(TextFormatting.AQUA)));
        player.sendMessage(new TextComponentString("\nTarget entity's resource location is:"));
        player.sendMessage(new TextComponentString(entityKey.toString()).setStyle(new Style().setColor(TextFormatting.AQUA)));
        player.sendMessage(new TextComponentString("\nTarget entity's string is:"));
        player.sendMessage(new TextComponentString(entityString).setStyle(new Style().setColor(TextFormatting.AQUA)));
        player.sendMessage(new TextComponentString("\nTarget entity's translation key is:"));
        player.sendMessage(new TextComponentString(entityTranslationKey).setStyle(new Style().setColor(TextFormatting.AQUA)));

        // new line
        player.sendMessage(new TextComponentString(""));
        player.sendMessage(new TextComponentString("Radar Target Identifier result: "));
        player.sendMessage(new TextComponentString(RadarTargetIdentifier.getBestIdentifier(target).toString()).setStyle(new Style().setColor(TextFormatting.BLUE)));
        player.sendMessage(new TextComponentString(""));

        return true;
    }

    // called when clicking on a block
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote) { return EnumActionResult.PASS; }

        // get the targeted blockstate and check if a tile entity could be associated; if not, indicate and return
        IBlockState targState = world.getBlockState(pos);
        TileEntity targTE = world.getTileEntity(pos);
        if (targTE == null) {
            player.sendMessage(new TextComponentString("Target <" + targState + "> does not have an associated TE").setStyle(new Style().setColor(TextFormatting.RED)));
            return EnumActionResult.PASS;
        }

        // show the display name, which is guaranteed to be present by TileEntity
        ITextComponent targTEDisplayName = targTE.getDisplayName();
        String displayNameKey = "NULL";
        String formattedDisplayName = "NULL";

        // get the te resource for later display
        ResourceLocation teResource = TileEntity.getKey(targTE.getClass());
        if (teResource == null) {
            teResource = new ResourceLocation("NULL");
        } else if (teResource.toString().length() < 1) {
            teResource = new ResourceLocation("EMPTY");
        }

        // try to get a more interesting display name
        if (targTEDisplayName != null) {
            // get the default values for the raw display name
            formattedDisplayName = targTEDisplayName.getFormattedText();
            displayNameKey = targTEDisplayName.getUnformattedComponentText();

            // we want to use the key in most cases, not the translated name
            if (targTEDisplayName instanceof TextComponentTranslation translatable) {
                displayNameKey = translatable.getKey();

                // display the transformation if there is formatting to be done
                if (translatable.getFormatArgs().length > 0) {
                    displayNameKey += "\n->\n" + String.format(translatable.getKey(), translatable.getFormatArgs());
                }
            }
        }

        // send the display name
        player.sendMessage(new TextComponentString("\nTarget's display name is:"));
        player.sendMessage(new TextComponentString(formattedDisplayName).setStyle(new Style().setColor(TextFormatting.AQUA)));
        player.sendMessage(new TextComponentString("\nTarget's resource location is:"));
        player.sendMessage(new TextComponentString(teResource.toString()).setStyle(new Style().setColor(TextFormatting.AQUA)));
        player.sendMessage(new TextComponentString("\nTarget's unformatted name is:"));
        player.sendMessage(new TextComponentString(displayNameKey).setStyle(new Style().setColor(TextFormatting.AQUA)));

        // see if there is a raw name which might be more appropriate
        if (targTE instanceof IWorldNameable nameableTE) {
            // get the actual nameable text with a default value
            TextComponentString nameableText = new TextComponentString("NULL");
            if (nameableTE.getName().length() > 0) {
                nameableText = new TextComponentString(nameableTE.getName());
            }

            // send to the player
            player.sendMessage(new TextComponentString("\nTarget is nameable with raw name:"));
            player.sendMessage(nameableText.setStyle(new Style().setColor(TextFormatting.AQUA)));
        } else {
            player.sendMessage(new TextComponentString("\nTarget does not implement nameable interface."));
        }

        // new line
        player.sendMessage(new TextComponentString(""));
        player.sendMessage(new TextComponentString("Radar Target Identifier result: "));
        player.sendMessage(new TextComponentString(RadarTargetIdentifier.getBestIdentifier(targTE).toString()).setStyle(new Style().setColor(TextFormatting.BLUE)));
        player.sendMessage(new TextComponentString(""));

        return EnumActionResult.SUCCESS;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TextComponentTranslation("info.radar_probe.default").setStyle(
                new Style().setColor(TextFormatting.GRAY)).getFormattedText());

        if (flagIn.isAdvanced()) {
            tooltip.add(new TextComponentTranslation("info.radar_probe.advanced").setStyle(
                    new Style().setColor(TextFormatting.AQUA).setBold(true)).getFormattedText());
        }
    }

}
