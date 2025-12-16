package wfcore.common.damage;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class DamageAsbestosCustom extends DamageSource {

    public static final DamageAsbestosCustom ASBESTOS = new DamageAsbestosCustom("asbestos");

    protected DamageAsbestosCustom(String damageTypeIn) {
        super(damageTypeIn);
        setDamageBypassesArmor();
    }

    @Override
    public ITextComponent getDeathMessage(EntityLivingBase entityLivingBase) {
        String name = entityLivingBase.getName();
        // Custom death message
        return new TextComponentString(  name + " ascended to the great insulation warehouse in the sky");
    }
}