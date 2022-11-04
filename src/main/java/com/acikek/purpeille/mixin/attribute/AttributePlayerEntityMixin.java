package com.acikek.purpeille.mixin.attribute;

import com.acikek.purpeille.attribute.ModAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerEntity.class)
public class AttributePlayerEntityMixin {

    private boolean purpeille$isCriticalHit;

    @Inject(method = "attack", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V", ordinal = 0))
    private void purpeille$applyAttackPull(Entity target, CallbackInfo ci, float f, float g, float h, boolean bl, boolean bl2, int i, boolean bl3, boolean bl4, double d, float j, boolean bl5, int k, Vec3d vec3d, boolean bl6) {
        LivingEntity livingEntity = ((LivingEntity) (Object) this);
        EntityAttributeInstance instance = livingEntity.getAttributeInstance(ModAttributes.GENERIC_ATTACK_PULL);
        if (instance == null || instance.getValue() == 0.0) {
            return;
        }
        Vec3d between = target.getPos().subtract(livingEntity.getPos());
        livingEntity.move(MovementType.SELF, between.multiply(instance.getValue()));
    }

    @Inject(method = "attack", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.BEFORE))
    private void purpeille$captureCriticalHit(Entity target, CallbackInfo ci, float f, float g, float h, boolean bl, boolean bl2, int i, boolean bl3, boolean bl4, double d, float j, boolean bl5, int k, Vec3d vec3d) {
        purpeille$isCriticalHit = bl3;
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
    private float purpeille$applyCriticalDamage(float value) {
        EntityAttributeInstance instance = ((LivingEntity) (Object) this).getAttributeInstance(ModAttributes.GENERIC_CRITICAL_DAMAGE);
        return instance != null && purpeille$isCriticalHit ? value * (float) instance.getValue() : value;
    }
}
