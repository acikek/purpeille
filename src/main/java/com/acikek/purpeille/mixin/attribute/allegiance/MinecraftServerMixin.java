package com.acikek.purpeille.mixin.attribute.allegiance;

import com.acikek.purpeille.api.allegiance.AbyssalAllegiance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Shadow private int ticks;

    @Shadow private PlayerManager playerManager;

    @Inject(method = "tick", at = @At("HEAD"))
    private void purpeille$demandAbyssalTribute(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (ticks % 168000 == 0) {
            for (ServerPlayerEntity player : playerManager.getPlayerList()) {
                AbyssalAllegiance.cycle(player, player.world.random);
            }
        }
    }
}
