package com.acikek.purpeille.mixin.attribute;

import com.acikek.purpeille.attribute.ModAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(PotionItem.class)
public class PotionItemMixin {

    @Inject(method = "finishUsing", locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void purpeille$applyPotency(ItemStack stack, World world, LivingEntity user,
                                        CallbackInfoReturnable<ItemStack> cir, PlayerEntity playerEntity,
                                        List<StatusEffectInstance> list) {
        ModAttributes.applyPotency(list, user);
    }
}
