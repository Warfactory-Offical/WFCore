package wfcore.mixins.minecraft;

import net.minecraft.util.CombatRules;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.spongepowered.asm.mixin.injection.callback.LocalCapture.CAPTURE_FAILSOFT;

@Mixin(CombatRules.class)
public abstract class CombatRulesMixin {

    /*
    public static float getDamageAfterAbsorb(float damage, float totalArmor, float toughnessAttribute)
    {
        float f = 2.0F + toughnessAttribute / 4.0F;
        float f1 = MathHelper.clamp(totalArmor - damage / f, totalArmor * 0.2F, 20.0F);
        return damage * (1.0F - f1 / 25.0F);
    }
    */
    // here I just copied over the entire method, hope it won't cause issues.
    //@ModifyReturnValue(method = "getDamageAfterAbsorb", at = @At(value = "RETURN"))
    @Inject(method = "getDamageAfterAbsorb(FFF)F", at = @At("RETURN"), cancellable = true, locals = CAPTURE_FAILSOFT)
    private static void getDamageAfterAbsorb(float damage, float totalArmor, float toughnessAttribute, CallbackInfoReturnable<Float> ci, float f) {
        //float f = 2.0F + toughnessAttribute / 4.0F;
        float f1 = MathHelper.clamp(totalArmor - damage / f, totalArmor * 0.2F, 25.0F);
        ci.setReturnValue(damage * (1.0F - f1 / 25.0F));
    }
}
