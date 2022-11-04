package com.acikek.purpeille.mixin.attribute;

import com.acikek.purpeille.attribute.ModAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PotionEntity.class)
public class PotionEntityMixin {

    @Inject(method = "applySplashPotion", at = @At("HEAD"))
    private void purpeille$applyThrownPotency(List<StatusEffectInstance> statusEffects, Entity entity, CallbackInfo ci) {
        if (((ProjectileEntity) (Object) this).getOwner() instanceof LivingEntity livingEntity) {
            ModAttributes.applyPotency(statusEffects, livingEntity);
        }
    }
}
