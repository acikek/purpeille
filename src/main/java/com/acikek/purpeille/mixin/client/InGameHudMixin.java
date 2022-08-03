package com.acikek.purpeille.mixin.client;

import com.acikek.purpeille.client.render.AncientMessageHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderCrosshair", cancellable = true, at = @At("HEAD"))
    private void hideDuringAncientMessage(MatrixStack matrices, CallbackInfo ci) {
        if (AncientMessageHud.ticks > 0) {
            ci.cancel();
        }
    }
}