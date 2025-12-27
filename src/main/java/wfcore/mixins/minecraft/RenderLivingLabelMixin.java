package wfcore.mixins.minecraft;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Render.class)
public class RenderLivingLabelMixin<T extends Entity> {
    @Shadow
    protected RenderManager renderManager;

    @ModifyVariable(
            method = "renderLivingLabel(Lnet/minecraft/entity/Entity;Ljava/lang/String;DDDI)V",
            at = @At("STORE"),
            ordinal = 0

    )
    private boolean flag(boolean flagSrc, T entityIn, String str, double x, double y, double z, int maxDistance) {
        return renderManager.pointedEntity == entityIn;
    }
}
