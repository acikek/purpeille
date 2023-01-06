package com.acikek.purpeille.mixin.attribute.mining;

import com.acikek.purpeille.attribute.MiningContinuationEfficiency;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onPlayerAction", locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket;getAction()Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;"))
    private void purpeille$handleMiningState(PlayerActionC2SPacket packet, CallbackInfo ci, BlockPos blockPos) {
        if (packet.getAction() == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK
                && player.getDataTracker().get(MiningContinuationEfficiency.CONTINUOUS_BLOCKS_MINED) > 0) {
            player.getDataTracker().set(MiningContinuationEfficiency.CONTINUOUS_BLOCKS_MINED, 0);
        }
        else if (packet.getAction() == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK
                && player.getDataTracker().get(MiningContinuationEfficiency.IS_NOT_MINING)) {
            player.getDataTracker().set(MiningContinuationEfficiency.IS_NOT_MINING, false);
        }
    }
}
