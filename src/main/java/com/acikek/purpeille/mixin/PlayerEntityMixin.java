package com.acikek.purpeille.mixin;

import com.acikek.purpeille.block.entity.ancient.guardian.AncientGuardianBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "findRespawnPosition", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true,
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 0))
    private static void allowGuardianRespawn(
            ServerWorld world,
            BlockPos pos,
            float angle,
            boolean forced,
            boolean alive,
            CallbackInfoReturnable<Optional<Vec3d>> cir,
            BlockState blockState
    ) {
        if (world.getBlockEntity(pos) instanceof AncientGuardianBlockEntity blockEntity && blockEntity.tetheredPlayer != null) {
            cir.setReturnValue(RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, world, pos));
        }
    }
}
