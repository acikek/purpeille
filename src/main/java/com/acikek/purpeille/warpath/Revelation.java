package com.acikek.purpeille.warpath;

import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.item.ModItems;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum Revelation {

    SPIRIT("spirit", Tone.STRENGTH, 0, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2, true, ModItems.PURPEILLE_HELMET),
    VIGOR("vigor", Tone.STRENGTH, 1, EntityAttributes.GENERIC_ATTACK_SPEED, 0.15, true, ModItems.PURPEILLE_AXE),
    TOTALITY("totality", Tone.STRENGTH, 2, EntityAttributes.GENERIC_MAX_HEALTH, 2.0, false, ModItems.PURPEILLE_CHESTPLATE),
    AVARICE("avarice", Tone.TENSION, 0, EntityAttributes.GENERIC_LUCK, 1.5, false, ModItems.PURPEILLE_PICKAXE),
    MALAISE("malaise", Tone.TENSION, 1, ModAttributes.GENERIC_POISON_RESISTANCE, 8.0, false, ModItems.PURPEILLE_HOE),
    TERROR("terror", Tone.TENSION, 2, EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0, false, ModItems.PURPEILLE_SWORD),
    BOUND("bound", Tone.RELEASE, 0, ModAttributes.GENERIC_JUMP_BOOST, 0.25, true, ModItems.PURPEILLE_SHOVEL),
    PACE("pace", Tone.RELEASE, 1, EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20, true, ModItems.PURPEILLE_BOOTS),
    IMMERSION("immersion", Tone.RELEASE, 2, ModAttributes.GENERIC_WATER_SPEED, 0.4, true, ModItems.PURPEILLE_LEGGINGS);

    public String name;
    public Tone tone;
    public int index;
    public EntityAttribute attribute;
    public Item item;
    public double modifier;
    public EntityAttributeModifier.Operation operation;

    Revelation(String name, Tone tone, int index, EntityAttribute attribute, double modifier, boolean multiply, Item item) {
        this.name = name;
        this.tone = tone;
        this.index = index;
        this.attribute = attribute;
        this.modifier = modifier;
        operation = multiply ? EntityAttributeModifier.Operation.MULTIPLY_TOTAL : EntityAttributeModifier.Operation.ADDITION;
        this.item = item;
    }

    public Text getRite() {
        return new TranslatableText("rite.purpeille." + name).formatted(Formatting.GRAY);
    }

    public double getModifier(ItemStack stack, Aspect aspect) {
        double value = stack.isOf(item) ? modifier * 1.2 : modifier;
        if (aspect == null) {
            return value;
        }
        return value * aspect.modifier * Synergy.getSynergy(this, aspect).modifier;
    }

    public static Revelation getFromNbt(ItemStack stack) {
        return Type.REVELATION.getFromNbt(stack, values());
    }
}
