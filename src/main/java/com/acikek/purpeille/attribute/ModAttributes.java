package com.acikek.purpeille.attribute;

import com.acikek.purpeille.Purpeille;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModAttributes {

    public static final EntityAttribute GENERIC_MINING_EXPERIENCE = new ClampedEntityAttribute("attribute.name.generic_mining_experience", 0.0, 0.0, 1024.0).setTracked(true);
    public static final EntityAttribute GENERIC_POISON_RESISTANCE = new ClampedEntityAttribute("attribute.name.generic_poison_resistance", 0.0, 0.0, 1024.0).setTracked(true);
    public static final EntityAttribute GENERIC_JUMP_BOOST = new ClampedEntityAttribute("attribute.name.generic_jump_boost", 1.0, 0.0, 2.0).setTracked(true);
    public static final EntityAttribute GENERIC_WATER_SPEED = new ClampedEntityAttribute("attribute.name.generic_water_speed", 1.0, 0.0, 2.0).setTracked(true);

    public static Map<String, EntityAttribute> ATTRIBUTES = new LinkedHashMap<>();

    static {
        ATTRIBUTES.put("generic.mining_experience", GENERIC_MINING_EXPERIENCE);
        ATTRIBUTES.put("generic.poison_resistance", GENERIC_POISON_RESISTANCE);
        ATTRIBUTES.put("generic.water_speed", GENERIC_WATER_SPEED);
        ATTRIBUTES.put("generic.jump_boost", GENERIC_JUMP_BOOST);
    }

    public static void register() {
        for (Map.Entry<String, EntityAttribute> pair : ATTRIBUTES.entrySet()) {
            Registry.register(Registries.ATTRIBUTE, Purpeille.id(pair.getKey()), pair.getValue());
        }
    }
}
