package com.acikek.purpeille.mixin;

import com.acikek.purpeille.block.entity.ancient.guardian.AncientGuardianBlockEntity;
import com.acikek.purpeille.effect.ModStatusEffects;
import com.acikek.purpeille.tag.ModTags;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract @Nullable StatusEffectInstance getStatusEffect(StatusEffect effect);

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

    @Inject(method = "damage", cancellable = true, at = @At("HEAD"))
    private void purpeille$applyVoidImmunity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source == DamageSource.OUT_OF_WORLD && getStatusEffect(ModStatusEffects.VOID_IMMUNITY) != null) {
            cir.setReturnValue(false);
        }
    }
}
