package com.acikek.purpeille.mixin.attribute;

import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.warpath.attribute.AttributeScalingData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;
import java.util.function.Consumer;

@Mixin(EntityAttributeInstance.class)
public abstract class EntityAttributeInstanceMixin {

    @Shadow @Final private Map<UUID, EntityAttributeModifier> idToModifiers;

    @Shadow @Final private EntityAttribute type;

    @Shadow public abstract double getBaseValue();

    @Shadow protected abstract Collection<EntityAttributeModifier> getModifiersByOperation(EntityAttributeModifier.Operation operation);

    @Shadow protected abstract double computeValue();

    private AttributeScalingData purpeille$scalingData;
    private boolean purpeille$isWarpath;
    private boolean purpeille$isDirty = true;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void purpeille$checkIsRevelationAttribute(EntityAttribute type, Consumer<?> updateCallback, CallbackInfo ci) {
        purpeille$scalingData = AttributeScalingData.getScalingData(type);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void purpeille$checkIsWarpath(CallbackInfo ci) {
        if (purpeille$scalingData == null) {
            return;
        }
        for (Map.Entry<UUID, EntityAttributeModifier> entry : idToModifiers.entrySet()) {
            if (purpeille$scalingData.uuids.contains(entry.getKey())) {
                purpeille$isWarpath = true;
                return;
            }
        }
    }

    private List<Double> getScaledModifiersByOperation(EntityAttributeModifier.Operation operation) {
        List<EntityAttributeModifier> modifiers = getModifiersByOperation(operation).stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .toList();
        List<Double> scaled = new ArrayList<>();
        List<Double> others = new ArrayList<>();
        for (int i = 0; i < modifiers.size(); i++) {
            double base = modifiers.get(i).getValue();
            if (purpeille$scalingData.uuids.contains(modifiers.get(i).getId())) {
                scaled.add(purpeille$scalingData.getModifierValue(i, base));
                continue;
            }
            others.add(base);
        }
        scaled.addAll(others);
        return scaled;
    }

    @Inject(method = "getValue", cancellable = true, at = @At("HEAD"))
    private void purpeille$forceScaling(CallbackInfoReturnable<Double> cir) {
        if (purpeille$isWarpath && purpeille$isDirty) {
            cir.setReturnValue(computeValue());
        }
    }

    @Inject(method = "computeValue", cancellable = true, at = @At("HEAD"))
    private void purpeille$scaledWarpathStacking(CallbackInfoReturnable<Double> cir) {
        if (!purpeille$isWarpath) {
            return;
        }
        purpeille$isDirty = false;
        double additionBase = getBaseValue();
        for (double m : getScaledModifiersByOperation(EntityAttributeModifier.Operation.ADDITION)) {
            additionBase += m;
        }
        double base = additionBase;
        for (double m : getScaledModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_BASE)) {
            base += (additionBase * m);
        }
        for (double m : getScaledModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_TOTAL)) {
            base *= (1.0 + m);
        }
        cir.setReturnValue(type.clamp(base));
    }

    @Inject(method = "toNbt", locals = LocalCapture.CAPTURE_FAILHARD, at = @At("RETURN"))
    private void purpeille$toNbt(CallbackInfoReturnable<NbtCompound> cir, NbtCompound nbt) {
        nbt.putBoolean("IsWarpath", purpeille$isWarpath);
        if (purpeille$scalingData != null) {
            NbtCompound scalingNbt = new NbtCompound();
            purpeille$scalingData.writeNbt(scalingNbt);
            nbt.put("RevelationScaling", scalingNbt);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    private void purpeille$readNbt(NbtCompound nbt, CallbackInfo ci) {
        purpeille$isWarpath = nbt.getBoolean("IsWarpath");
        if (nbt.contains("RevelationScaling")) {
            purpeille$scalingData = AttributeScalingData.readNbt(nbt.getCompound("RevelationScaling"));
        }
    }
}
