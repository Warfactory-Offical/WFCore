package wfcore.common.damage;

import net.minecraft.util.DamageSource;

public class DamageAsbestos extends DamageSource {

    public static final DamageAsbestos ASBESTOS =
            new DamageAsbestos("asbestos");

    protected DamageAsbestos(String damageTypeIn) {
        super(damageTypeIn);
        setDamageBypassesArmor();
        setDamageIsAbsolute();
    }

}
