package wfcore.common.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class RadarProbe extends BaseItem {
    public RadarProbe(String s, String texturePath) {
        super(s, texturePath);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote) { return EnumActionResult.PASS; }  // don't run on server; client only util

        // get the targeted blockstate and check if a tile entity could be associated; if not, indicate and return
        IBlockState targState = worldIn.getBlockState(pos);
        TileEntity targTE = worldIn.getTileEntity(pos);
        if (targTE == null) {
            player.sendMessage(new TextComponentString("Target <" + targState + "> does not have an associated TE").setStyle(new Style().setColor(TextFormatting.RED)));
            return EnumActionResult.PASS;
        }

        // show the display name, which is guaranteed to be present by TileEntity
        ITextComponent targTEDisplayName = targTE.getDisplayName();
        String displayNameKey = "NULL";
        String formattedDisplayName = "NULL";

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
