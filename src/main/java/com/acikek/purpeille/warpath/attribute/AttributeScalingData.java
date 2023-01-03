package com.acikek.purpeille.warpath.attribute;

import com.acikek.purpeille.api.warpath.Components;
import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;

import java.util.*;

public class AttributeScalingData {

    public double scalingMultiplier;
    public double scalingDropoff;
    public Map<EquipmentSlot, UUID> uuidMap;
    public Collection<UUID> uuids;

    public AttributeScalingData(double scalingMultiplier, double scalingDropoff, Map<EquipmentSlot, UUID> uuidMap, Collection<UUID> uuids) {
        this.scalingMultiplier = scalingMultiplier;
        this.scalingDropoff = scalingDropoff;
        this.uuidMap = uuidMap;
        this.uuids = uuids;
    }

    public AttributeScalingData(double scalingMultiplier, double scalingDropoff, Map<EquipmentSlot, UUID> uuidMap) {
        this(scalingMultiplier, scalingDropoff, uuidMap, uuidMap.values());
    }

    public double getModifierValue(int i, double base) {
        return i == 0 ? base
                : (Math.pow(scalingDropoff, -i) * (scalingMultiplier)) * base;
    }

    public static AttributeScalingData getScalingData(EntityAttribute attribute) {
        if (attribute == ModAttributes.GENERIC_ABYSSAL_ALLEGIANCE) {
            return ModAttributes.ABYSSAL_ALLEGIANCE_SCALING_DATA;
        }
        for (Revelation revelation : Components.getRevelations().values()) {
            if (attribute == revelation.attribute.value) {
                return revelation.attribute;
            }
            if (revelation.abyssalite != null && attribute == revelation.abyssalite.attribute.value) {
                return revelation.abyssalite.attribute;
            }
        }
        return null;
    }

    public static AttributeScalingData readNbt(NbtCompound nbt) {
        double scalingMultiplier = nbt.getDouble("ScalingMultiplier");
        double scalingDropoff = nbt.getDouble("ScalingDropoff");
        List<UUID> uuids = new ArrayList<>();
        for (NbtElement element : nbt.getList("UUIDs", NbtElement.INT_ARRAY_TYPE)) {
            uuids.add(NbtHelper.toUuid(element));
        }
        return new AttributeScalingData(scalingMultiplier, scalingDropoff, null, uuids);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putDouble("ScalingMultiplier", scalingMultiplier);
        nbt.putDouble("ScalingDropoff", scalingDropoff);
        NbtList list = new NbtList();
        for (UUID uuid : uuids) {
            list.add(NbtHelper.fromUuid(uuid));
        }
        nbt.put("UUIDs", list);
    }
}
