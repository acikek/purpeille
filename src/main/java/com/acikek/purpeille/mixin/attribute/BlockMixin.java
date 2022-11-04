package com.acikek.purpeille.mixin.attribute;

import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.tag.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Shadow protected abstract void dropExperience(ServerWorld world, BlockPos pos, int size);

    @Inject(method = "onBreak", locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;emitGameEvent(Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/event/GameEvent$Emitter;)V"))
    private void purpeille$applyMiningExperience(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (!world.isClient()
                && state.isIn(ModTags.MINING_EXPERIENCE)
                && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, player.getInventory().getMainHandStack()) == 0) {
            EntityAttributeInstance instance = player.getAttributeInstance(ModAttributes.GENERIC_MINING_EXPERIENCE);
            if (instance != null && Math.random() >= 0.8) {
                dropExperience((ServerWorld) world, pos, (int) instance.getValue());
            }
        }
    }
}
