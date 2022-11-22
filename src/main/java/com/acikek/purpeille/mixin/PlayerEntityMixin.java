package com.acikek.purpeille.mixin;

import com.acikek.purpeille.api.PurpeilleAPI;
import com.acikek.purpeille.block.entity.ancient.guardian.AncientGuardianBlockEntity;
import com.acikek.purpeille.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
    private static void purpeille$allowGuardianRespawn(
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

    @Inject(method = "isUsingSpyglass", cancellable = true, at = @At("HEAD"))
    private void purpeille$recognizeAmalgamatedSpyglass(CallbackInfoReturnable<Boolean> cir) {
        if (PurpeilleAPI.isUsingAmalgamatedSpyglass((LivingEntity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
