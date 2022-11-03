package com.acikek.purpeille.mixin;

import com.acikek.purpeille.attribute.ModAttributes;
import com.google.common.collect.Multimap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    private EntityAttribute purpeille$attribute;

    @Inject(method = "getTooltip", locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;"))
    private void purpeille$captureAttribute(
            PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir,
            List<?> list, MutableText mutableText, int i, EquipmentSlot[] var6, int var7, int var8,
            EquipmentSlot equipmentSlot, Multimap<?, ?> multimap, Iterator<?> var11, Map.Entry<EntityAttribute, ?> entry) {
        purpeille$attribute = entry.getKey();
    }

    @ModifyArg(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;", ordinal = 6))
    private Formatting purpeille$abyssalAllegianceTooltipColor(Formatting formatting) {
        return purpeille$attribute == ModAttributes.GENERIC_ABYSSAL_ALLEGIANCE
                ? Formatting.RED
                : formatting;
    }
}
