package com.acikek.purpeille.mixin.client;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.api.abyssal.AmalgamatedSpyglass;
import com.acikek.purpeille.client.render.AncientMessageHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    private final Identifier AMALGAMATED_SPYGLASS_TEXTURE = Purpeille.id("textures/item/amalgamated_spyglass_scope.png");

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderCrosshair", cancellable = true, at = @At("HEAD"))
    private void purpeille$hideDuringAncientMessage(MatrixStack matrices, CallbackInfo ci) {
        if (!AncientMessageHud.ticks.isEmpty()) {
            ci.cancel();
        }
    }

    @ModifyArg(method = "renderSpyglassOverlay", index = 1,
               at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V"))
    private Identifier purpeille$renderAmalgamatedSpyglassOverlay(Identifier spyglassTexture) {
        return AmalgamatedSpyglass.isUsingAmalgamatedSpyglass(client.player)
            ? AMALGAMATED_SPYGLASS_TEXTURE
            : spyglassTexture;
    }
}
