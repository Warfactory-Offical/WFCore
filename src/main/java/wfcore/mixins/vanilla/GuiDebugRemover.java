package wfcore.mixins.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOverlayDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wfcore.WFCore;

@Mixin(Minecraft.class)
public class GuiDebugRemover {
    /**
     * @author Chudcel Norwood
     * @reason No more
     */

    @Inject(
            method = "displayDebugInfo",
            at = @At("HEAD"),
            cancellable = true
    )
    private void displayDebugInfo(long elapsedTicksTime, CallbackInfo ci){
        if(!WFCore.DEBUG)
            ci.cancel();
    }

}
