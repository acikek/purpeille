package com.acikek.purpeille.mixin;

import com.acikek.purpeille.attribute.ModAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(StatusEffect.class)
public class StatusEffectMixin {

    @Inject(method = "applyUpdateEffect", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "HEAD"))
    private void applyPoisonResistance(LivingEntity entity, int amplifier, CallbackInfo ci) {
        if ((Object) this == StatusEffects.POISON) {
            EntityAttributeInstance instance = entity.getAttributeInstance(ModAttributes.GENERIC_POISON_RESISTANCE);
            if (instance != null) {
                float max = 1.0f + (float) instance.getValue() / 3.0f;
                System.out.println(max);
                if (entity.getHealth() > max) {
                    entity.damage(DamageSource.MAGIC, 1.0f);
                }
                ci.cancel();
            }
        }
    }
}
