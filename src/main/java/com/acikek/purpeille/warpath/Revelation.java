package com.acikek.purpeille.warpath;

import com.acikek.purpeille.attribute.ModAttributes;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum Revelation {

    SPIRIT("spirit", Tone.STRENGTH, 0, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2),
    VIGOR("vigor", Tone.STRENGTH, 1, EntityAttributes.GENERIC_ATTACK_SPEED, 0.3),
    TOTALITY("totality", Tone.STRENGTH, 2, EntityAttributes.GENERIC_MAX_HEALTH, 2.0),
    AVARICE("avarice", Tone.TENSION, 0, EntityAttributes.GENERIC_LUCK, 128.0),
    MALAISE("malaise", Tone.TENSION, 1, ModAttributes.GENERIC_POISON_RESISTANCE, 4.0),
    TERROR("terror", Tone.TENSION, 2, EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0),
    BOUND("bound", Tone.RELEASE, 0, ModAttributes.GENERIC_JUMP_BOOST, 0.25),
    PACE("pace", Tone.RELEASE, 1, EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25),
    IMMERSION("immersion", Tone.RELEASE, 2, ModAttributes.GENERIC_WATER_SPEED, 0.4);

    public String name;
    public Tone tone;
    public int index;
    public EntityAttribute attribute;
    public double modifier;

    Revelation(String name, Tone tone, int index, EntityAttribute attribute, double modifier) {
        this.name = name;
        this.tone = tone;
        this.index = index;
        this.attribute = attribute;
        this.modifier = modifier;
    }

    public Text getRite() {
        return new TranslatableText("rite.purpeille." + name).formatted(Formatting.GRAY);
    }

    public double getModifier(Aspect aspect) {
        return modifier * aspect.modifier * Synergy.getSynergy(this, aspect).modifier;
    }

    public static Revelation getFromNbt(ItemStack stack) {
        return Type.REVELATION.getFromNbt(stack, values());
    }
}
