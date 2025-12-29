package wfcore.mixins.minecraft;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wfcore.WFCore;

@Mixin(RenderManager.class)
public class DebugRemover {
    /**
     * @author Chudcel Norwood
     * @reason Bye bye debug render!
     */
    @Inject(
            method = "renderDebugBoundingBox",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderDebugBoundingBox(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (!WFCore.DEBUG)
            ci.cancel();

    }



}
