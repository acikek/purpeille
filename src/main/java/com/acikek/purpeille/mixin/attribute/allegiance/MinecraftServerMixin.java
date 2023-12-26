package com.acikek.purpeille.mixin.attribute.allegiance;

import com.acikek.purpeille.api.allegiance.AbyssalAllegiance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow private PlayerManager playerManager;

    @Shadow public abstract SaveProperties getSaveProperties();

    @Inject(method = "tick", at = @At("HEAD"))
    private void purpeille$demandAbyssalTribute(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (getSaveProperties() instanceof LevelProperties levelProperties) {
            if (levelProperties.getTime() % 168000 == 0) {
                for (ServerPlayerEntity player : playerManager.getPlayerList()) {
                    AbyssalAllegiance.cycle(player, player.getWorld().random);
                }
            }
        }
    }
}
