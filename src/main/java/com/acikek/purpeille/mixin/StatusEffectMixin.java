package com.acikek.purpeille.mixin;

import com.acikek.purpeille.attribute.ModAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatusEffect.class)
public class StatusEffectMixin {

    private static LivingEntity purpeille$target;

    @Inject(method = "applyUpdateEffect", at = @At(value = "HEAD"))
    private void purpeille$captureTarget(LivingEntity entity, int amplifier, CallbackInfo ci) {
        purpeille$target = entity;
    }

    @ModifyArg(method = "applyUpdateEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 0), index = 1)
    private float purpeille$applyPoisonResistance(float amount) {
        if ((Object) this == StatusEffects.POISON) {
            EntityAttributeInstance instance = purpeille$target.getAttributeInstance(ModAttributes.GENERIC_POISON_RESISTANCE);
            if (instance != null) {
                float max = 1.0f + (float) instance.getValue() / 3.0f;
                if (purpeille$target.getHealth() > max) {
                    return 0.0f;
                }
            }
        }
        return amount;
    }
}
