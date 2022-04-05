package com.acikek.purpeille.warpath;

import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.item.ModItems;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum Revelation {

    SPIRIT("spirit", Tone.STRENGTH, 0, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, ModItems.PURPEILLE_HELMET, 0.2),
    VIGOR("vigor", Tone.STRENGTH, 1, EntityAttributes.GENERIC_ATTACK_SPEED, ModItems.PURPEILLE_AXE, 0.3),
    TOTALITY("totality", Tone.STRENGTH, 2, EntityAttributes.GENERIC_MAX_HEALTH, ModItems.PURPEILLE_CHESTPLATE, 2.0),
    AVARICE("avarice", Tone.TENSION, 0, EntityAttributes.GENERIC_LUCK, ModItems.PURPEILLE_PICKAXE, 1.5),
    MALAISE("malaise", Tone.TENSION, 1, ModAttributes.GENERIC_POISON_RESISTANCE, ModItems.PURPEILLE_HOE, 4.0),
    TERROR("terror", Tone.TENSION, 2, EntityAttributes.GENERIC_ATTACK_DAMAGE, ModItems.PURPEILLE_SWORD, 2.0),
    BOUND("bound", Tone.RELEASE, 0, ModAttributes.GENERIC_JUMP_BOOST, ModItems.PURPEILLE_SHOVEL, 0.25),
    PACE("pace", Tone.RELEASE, 1, EntityAttributes.GENERIC_MOVEMENT_SPEED, ModItems.PURPEILLE_BOOTS, 0.05),
    IMMERSION("immersion", Tone.RELEASE, 2, ModAttributes.GENERIC_WATER_SPEED, ModItems.PURPEILLE_LEGGINGS, 0.4);

    public String name;
    public Tone tone;
    public int index;
    public EntityAttribute attribute;
    public Item item;
    public double modifier;

    Revelation(String name, Tone tone, int index, EntityAttribute attribute, Item item, double modifier) {
        this.name = name;
        this.tone = tone;
        this.index = index;
        this.attribute = attribute;
        this.item = item;
        this.modifier = modifier;
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
