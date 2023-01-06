package com.acikek.purpeille.attribute;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.warpath.attribute.AttributeScalingData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.EnumUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ModAttributes {

    public static final EntityAttribute GENERIC_MINING_EXPERIENCE = new ClampedEntityAttribute("attribute.name.generic_mining_experience", 0.0, 0.0, 1024.0).setTracked(true);
    public static final EntityAttribute GENERIC_POISON_RESISTANCE = new ClampedEntityAttribute("attribute.name.generic_poison_resistance", 0.0, 0.0, 1024.0).setTracked(true);
    public static final EntityAttribute GENERIC_JUMP_BOOST = new ClampedEntityAttribute("attribute.name.generic_jump_boost", 1.0, 0.0, 1024.0).setTracked(true);
    public static final EntityAttribute GENERIC_WATER_SPEED = new ClampedEntityAttribute("attribute.name.generic_water_speed", 1.0, 0.0, 1024.0).setTracked(true);
    public static final EntityAttribute GENERIC_ATTACKER_KNOCKBACK_CHANCE = new ClampedEntityAttribute("attribute.name.generic_attacker_knockback_chance", 0.0, 0.0, 100.0).setTracked(true);
    public static final EntityAttribute GENERIC_ATTACK_PULL = new ClampedEntityAttribute("attribute.name.generic_attack_pull", 0.0, 0.0, 1.0).setTracked(true);
    public static final EntityAttribute GENERIC_REGENERATION_CHANCE = new ClampedEntityAttribute("attribute.name.generic_regeneration_chance", 0.0, 0.0, 100.0).setTracked(true);
    public static final EntityAttribute GENERIC_MINING_CONTINUATION_EFFICIENCY = new ClampedEntityAttribute("attribute.name.generic_mining_continuation_efficiency", 0.0, 0.0, 1024.0).setTracked(true);
    public static final EntityAttribute GENERIC_POTION_USED_POTENCY = new ClampedEntityAttribute("attribute.name.generic_potion_used_potency", 1.0, 0.0, 1024.0).setTracked(true);
    public static final EntityAttribute GENERIC_CRITICAL_DAMAGE = new ClampedEntityAttribute("attribute.name.generic_critical_damage", 0.0, 0.0, 1024.0).setTracked(true);
    public static final EntityAttribute GENERIC_AIR_VELOCITY = new ClampedEntityAttribute("attribute.name.generic_air_velocity", 1.0, 0.0, 1024.0).setTracked(true);
    public static final EntityAttribute GENERIC_SWIMMING_RESPIRATION = new ClampedEntityAttribute("attribute.name.generic_swimming_respiration", 0.0, 0.0, 1024.0).setTracked(true);
    public static final EntityAttribute GENERIC_ABYSSAL_ALLEGIANCE = new ClampedEntityAttribute("attribute.name.generic_abyssal_allegiance", 0.0, 0.0, 1024.0).setTracked(true);

    public static Map<String, EntityAttribute> ATTRIBUTES = new LinkedHashMap<>();

    public static final AttributeScalingData ABYSSAL_ALLEGIANCE_SCALING_DATA = new AttributeScalingData(1.4, 1.4, getEquipmentSlotUUIDMap("generic.abyssal_allegiance"));

    static {
        ATTRIBUTES.put("generic.mining_experience", GENERIC_MINING_EXPERIENCE);
        ATTRIBUTES.put("generic.poison_resistance", GENERIC_POISON_RESISTANCE);
        ATTRIBUTES.put("generic.water_speed", GENERIC_WATER_SPEED);
        ATTRIBUTES.put("generic.jump_boost", GENERIC_JUMP_BOOST);
        ATTRIBUTES.put("generic.attacker_knockback_chance", GENERIC_ATTACKER_KNOCKBACK_CHANCE);
        ATTRIBUTES.put("generic.attack_pull", GENERIC_ATTACK_PULL);
        ATTRIBUTES.put("generic.regeneration_chance", GENERIC_REGENERATION_CHANCE);
        ATTRIBUTES.put("generic.mining_continuation_efficiency", GENERIC_MINING_CONTINUATION_EFFICIENCY);
        ATTRIBUTES.put("generic.potion_used_potency", GENERIC_POTION_USED_POTENCY);
        ATTRIBUTES.put("generic.critical_damage", GENERIC_CRITICAL_DAMAGE);
        ATTRIBUTES.put("generic.air_velocity", GENERIC_AIR_VELOCITY);
        ATTRIBUTES.put("generic.swimming_respiration", GENERIC_SWIMMING_RESPIRATION);
        ATTRIBUTES.put("generic.abyssal_allegiance", GENERIC_ABYSSAL_ALLEGIANCE);
    }

    public static void register() {
        for (Map.Entry<String, EntityAttribute> pair : ATTRIBUTES.entrySet()) {
            Registry.register(Registry.ATTRIBUTE, Purpeille.id(pair.getKey()), pair.getValue());
        }
    }

    public static Map<EquipmentSlot, UUID> getEquipmentSlotUUIDMap(String id) {
        return EnumUtils.getEnumList(EquipmentSlot.class).stream()
                .map(slot -> {
                    String newId = id + "_" + slot.getName();
                    return new Pair<>(slot, UUID.nameUUIDFromBytes(newId.getBytes()));
                })
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    public static void applyPotency(List<StatusEffectInstance> effects, LivingEntity livingEntity) {
        EntityAttributeInstance instance = livingEntity.getAttributeInstance(GENERIC_POTION_USED_POTENCY);
        if (instance == null || instance.getValue() == 1.0) {
            return;
        }
        for (StatusEffectInstance effect : effects) {
            effect.amplifier += (int) instance.getValue();
            effect.duration *= instance.getValue();
        }
    }
}
