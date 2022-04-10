package com.acikek.purpeille.warpath;

import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.item.ModItems;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum Revelations {

    SPIRIT("spirit", Tone.STRENGTH, 0, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2, true, ModItems.PURPEILLE_HELMET),
    VIGOR("vigor", Tone.STRENGTH, 1, EntityAttributes.GENERIC_ATTACK_SPEED, 0.15, true, ModItems.PURPEILLE_AXE),
    TOTALITY("totality", Tone.STRENGTH, 2, EntityAttributes.GENERIC_MAX_HEALTH, 2.0, false, ModItems.PURPEILLE_CHESTPLATE),
    AVARICE("avarice", Tone.TENSION, 0, EntityAttributes.GENERIC_LUCK, 1.5, false, ModItems.PURPEILLE_PICKAXE),
    MALAISE("malaise", Tone.TENSION, 1, ModAttributes.GENERIC_POISON_RESISTANCE, 12.0, false, ModItems.PURPEILLE_HOE),
    TERROR("terror", Tone.TENSION, 2, EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0, false, ModItems.PURPEILLE_SWORD),
    BOUND("bound", Tone.RELEASE, 0, ModAttributes.GENERIC_JUMP_BOOST, 0.25, true, ModItems.PURPEILLE_SHOVEL),
    PACE("pace", Tone.RELEASE, 1, EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20, true, ModItems.PURPEILLE_BOOTS),
    IMMERSION("immersion", Tone.RELEASE, 2, ModAttributes.GENERIC_WATER_SPEED, 0.4, true, ModItems.PURPEILLE_LEGGINGS);

    public Revelation value;

    Revelations(String name, Tone tone, int index, EntityAttribute attribute, double modifier, boolean multiply, Item item) {
        value = new Revelation(name, tone, index, modifier, attribute, item, multiply);
    }

    public static Revelations getFromNbt(ItemStack stack) {
        return Type.REVELATION.getFromNbt(stack, values());
    }
}
