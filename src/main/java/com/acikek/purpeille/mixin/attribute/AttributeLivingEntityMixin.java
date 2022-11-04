package com.acikek.purpeille.mixin.attribute;

import com.acikek.purpeille.attribute.ModAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class AttributeLivingEntityMixin {

    @Shadow @Nullable public abstract EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    @Shadow public abstract float getHealth();

    @Shadow public abstract float getMaxHealth();

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    @Shadow public abstract boolean isInSwimmingPose();

    @Inject(method = "createLivingAttributes", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void purpeille$addCustomAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        for (EntityAttribute attribute : ModAttributes.ATTRIBUTES.values()) {
            cir.getReturnValue().add(attribute);
        }
    }

    @Inject(
            method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void purpeille$applyPoisonResistance(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (effect.getEffectType() == StatusEffects.POISON) {
            EntityAttributeInstance instance = getAttributeInstance(ModAttributes.GENERIC_POISON_RESISTANCE);
            if (instance != null && instance.getValue() != 0.0) {
                effect.duration = (int) (effect.getDuration() - instance.getValue() * 20.0);
            }
        }
    }

    @Inject(method = "getJumpVelocity", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void purpeille$applyJumpBoost(CallbackInfoReturnable<Float> cir) {
        EntityAttributeInstance instance = getAttributeInstance(ModAttributes.GENERIC_JUMP_BOOST);
        if (instance != null) {
            cir.setReturnValue(cir.getReturnValue() * (float) (instance.getValue()));
        }
    }

    @ModifyVariable(method = "travel", ordinal = 1,
                    at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getDepthStrider(Lnet/minecraft/entity/LivingEntity;)I"))
    private float purpeille$applyWaterSpeed(float g) {
        EntityAttributeInstance instance = getAttributeInstance(ModAttributes.GENERIC_WATER_SPEED);
        return instance != null ? g * (float) instance.getValue() : g;
    }

    @Redirect(method = "getMovementSpeed(F)F",
              at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;airStrafingSpeed:F"))
    private float purpeille$applyAirVelocity(LivingEntity instance) {
        EntityAttributeInstance attributeInstance = getAttributeInstance(ModAttributes.GENERIC_AIR_VELOCITY);
        return instance.airStrafingSpeed * (attributeInstance != null ? (float) attributeInstance.getValue() : 1.0f);
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void purpeille$applyAttackerKnockback(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            EntityAttributeInstance instance = getAttributeInstance(ModAttributes.GENERIC_ATTACKER_KNOCKBACK_CHANCE);
            if (instance == null || instance.getValue() == 0.0) {
                return;
            }
            World world = ((Entity) (Object) this).world;
            if (world.random.nextDouble() < instance.getValue()) {
                Vec3d velocity = attacker.getVelocity();
                Vec3d pos = attacker.getPos().subtract(velocity.multiply(1.5));
                attacker.takeKnockback(velocity.length(), pos.x, pos.z);
            }
        }
    }

    @Inject(method = "damage", at = @At("TAIL"))
    private void purpeille$applyRegenerationChance(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        EntityAttributeInstance instance = getAttributeInstance(ModAttributes.GENERIC_REGENERATION_CHANCE);
        if (instance == null || instance.getValue() == 0.0) {
            return;
        }
        if (getHealth() / getMaxHealth() <= 0.5f && ((Entity) (Object) this).world.random.nextDouble() < instance.getValue()) {
            addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, (int) (instance.getValue() / 0.3)));
        }
    }

    @ModifyVariable(method = "getNextAirUnderwater", index = 1, argsOnly = true,
                    at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getRespiration(Lnet/minecraft/entity/LivingEntity;)I", shift = At.Shift.AFTER))
    private int purpeille$applySwimmingRespiration(int i) {
        EntityAttributeInstance instance = getAttributeInstance(ModAttributes.GENERIC_SWIMMING_RESPIRATION);
        return instance != null && isInSwimmingPose() ? i + (int) instance.getValue() : i;
    }
}
