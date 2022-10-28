package com.acikek.purpeille.mixin;

import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.block.entity.ancient.guardian.AncientGuardianBlockEntity;
import com.acikek.purpeille.effect.ModStatusEffects;
import com.acikek.purpeille.tag.ModTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow @Nullable public abstract EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract @Nullable StatusEffectInstance getStatusEffect(StatusEffect effect);

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
            if (instance != null) {
                ((StatusEffectInstanceAccessor) effect).setDuration((int) (effect.getDuration() - instance.getValue() * 20.0));
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

    @ModifyVariable(
            method = "travel", ordinal = 1,
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getDepthStrider(Lnet/minecraft/entity/LivingEntity;)I")
    )
    private float purpeille$applyWaterSpeed(float g) {
        EntityAttributeInstance instance = getAttributeInstance(ModAttributes.GENERIC_WATER_SPEED);
        return instance != null ? g * (float) instance.getValue() : g;
    }

    public int getArmorPieces() {
        int result = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR && getEquippedStack(slot).isIn(ModTags.WARPATH_BASE)) {
                result++;
            }
        }
        return result;
    }

    @Inject(method = "tryUseTotem", cancellable = true,
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;"))
    private void purpeille$useVoidTether(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        int armorPieces = getArmorPieces();
        if (armorPieces > 0 && entity instanceof ServerPlayerEntity playerEntity) {
            AncientGuardianBlockEntity blockEntity = AncientGuardianBlockEntity.getTether(playerEntity);
            if (blockEntity != null && blockEntity.cooldown == 0 && blockEntity.isPlayerTethered(playerEntity)) {
                blockEntity.activate(playerEntity, armorPieces);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void purpeille$applyAttackerKnockback(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof LivingEntity livingEntity) {
            EntityAttributeInstance instance = getAttributeInstance(ModAttributes.GENERIC_ATTACKER_KNOCKBACK_CHANCE);
            if (instance == null) {
                return;
            }
            World world = ((Entity) (Object) this).world;
            if (world.random.nextDouble() < instance.getValue()) {
                Vec3d velocity = livingEntity.getVelocity();
                Vec3d pos = livingEntity.getPos().subtract(velocity.multiply(1.5));
                livingEntity.takeKnockback(velocity.length(), pos.x, pos.z);
            }
        }
    }

    @Inject(method = "damage", cancellable = true, at = @At("HEAD"))
    private void purpeille$applyVoidImmunity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source == DamageSource.OUT_OF_WORLD && getStatusEffect(ModStatusEffects.VOID_IMMUNITY) != null) {
            cir.setReturnValue(false);
        }
    }
}
