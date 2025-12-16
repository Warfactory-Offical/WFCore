package wfcore.common.registry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import wfcore.common.damage.DamageAsbestosCustom;


import static wfcore.WFCore.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class ItemReplacementHandler {

    // makes asbestos edible but instakills you
    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {

        ResourceLocation asbestosRL =
                new ResourceLocation("hbm", "ingot_asbestos");

        Item original = ForgeRegistries.ITEMS.getValue(asbestosRL);
        if (original == null) return;

        ItemFood deadlyAsbestos = new ItemFood(0, 0.0F, false) {

            @Override
            protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
                if (!world.isRemote) {
                    player.attackEntityFrom(DamageAsbestosCustom.ASBESTOS, Float.MAX_VALUE);
                }
            }

            @Override
            public EnumAction getItemUseAction(ItemStack stack) {
                return EnumAction.EAT;
            }

            @Override
            public int getMaxItemUseDuration(ItemStack stack) {
                return 16;
            }

        };

        deadlyAsbestos.setAlwaysEdible();
        deadlyAsbestos.setRegistryName(asbestosRL);
        deadlyAsbestos.setCreativeTab(original.getCreativeTab());
deadlyAsbestos.setTranslationKey("hbm.ingot_asbestos");
        event.getRegistry().register(deadlyAsbestos);

        }
    }

