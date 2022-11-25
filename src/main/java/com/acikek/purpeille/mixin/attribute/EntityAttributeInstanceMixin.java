package com.acikek.purpeille.mixin.attribute;

import com.acikek.purpeille.attribute.ModAttributes;
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

    private Collection<UUID> purpeille$uuids;
    private boolean purpeille$isWarpath;
    private boolean purpeille$isDirty = true;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void purpeille$checkIsRevelationAttribute(EntityAttribute type, Consumer<?> updateCallback, CallbackInfo ci) {
        Map<EquipmentSlot, UUID> uuidMap = ModAttributes.getUUIDMap(type);
        if (uuidMap != null) {
            purpeille$uuids = uuidMap.values();
        }
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void purpeille$checkIsWarpath(CallbackInfo ci) {
        if (purpeille$uuids == null) {
            return;
        }
        for (Map.Entry<UUID, EntityAttributeModifier> entry : idToModifiers.entrySet()) {
            if (purpeille$uuids.contains(entry.getKey())) {
                purpeille$isWarpath = true;
                return;
            }
        }
    }

    private double getValue(int i, double base) {
        boolean aa = type == ModAttributes.GENERIC_ABYSSAL_ALLEGIANCE;
        return i == 0 ? base : (Math.pow(aa ? 1.4 : 2.0, -i) * (aa ? 1.4 : 1.2)) * base;
    }

    private List<Double> getScaledModifiersByOperation(EntityAttributeModifier.Operation operation) {
        List<EntityAttributeModifier> modifiers = getModifiersByOperation(operation).stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .toList();
        List<Double> scaled = new ArrayList<>();
        List<Double> others = new ArrayList<>();
        for (int i = 0; i < modifiers.size(); i++) {
            double base = modifiers.get(i).getValue();
            if (purpeille$uuids.contains(modifiers.get(i).getId())) {
                scaled.add(getValue(i, base));
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
        if (purpeille$uuids != null) {
            NbtList uuids = new NbtList();
            for (UUID uuid : purpeille$uuids) {
                uuids.add(NbtHelper.fromUuid(uuid));
            }
            nbt.put("RevelationUUIDs", uuids);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    private void purpeille$readNbt(NbtCompound nbt, CallbackInfo ci) {
        purpeille$isWarpath = nbt.getBoolean("IsWarpath");
        if (nbt.contains("RevelationUUIDs")) {
            purpeille$uuids = new ArrayList<>();
            for (NbtElement element : nbt.getList("RevelationUUIDs", NbtElement.INT_ARRAY_TYPE)) {
                purpeille$uuids.add(NbtHelper.toUuid(element));
            }
        }
    }
}
