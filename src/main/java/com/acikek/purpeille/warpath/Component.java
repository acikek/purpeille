package com.acikek.purpeille.warpath;

import com.acikek.purpeille.attribute.ModAttributes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.UUID;

public enum Component {

    // Aspects
    VIRTUOUS("virtuous", Type.ASPECT, Tone.STRENGTH, 0, null, 0.0),
    EXCESS("excess", Type.ASPECT, Tone.STRENGTH, 1, null, 0.0),
    HEROIC("heroic", Type.ASPECT, Tone.STRENGTH, 2, null, 0.0),
    TERRAN("terran", Type.ASPECT, Tone.TENSION, 0, null, 0.0),
    SHOCKING("shocking", Type.ASPECT, Tone.TENSION, 1, null, 0.0),
    DEATHLY("deathly", Type.ASPECT, Tone.TENSION, 2, null, 0.0),
    LIMITLESS("limitless", Type.ASPECT, Tone.RELEASE, 0, null, 0.0),
    TRANQUIL("tranquil", Type.ASPECT, Tone.RELEASE, 1, null, 0.0),
    UNRIVALED("unrivaled", Type.ASPECT, Tone.RELEASE, 2, null, 0.0),

    // Revelations
    SPIRIT("spirit", Type.REVELATION, Tone.STRENGTH, 0, null, 0.0),
    VIGOR("vigor", Type.REVELATION, Tone.STRENGTH, 1, null, 0.0),
    TOTALITY("totality", Type.REVELATION, Tone.STRENGTH, 2, null, 0.0),
    AVARICE("avarice", Type.REVELATION, Tone.TENSION, 0, null, 0.0),
    MALAISE("malaise", Type.REVELATION, Tone.TENSION, 1, null, 0.0),
    TERROR("terror", Type.REVELATION, Tone.TENSION, 2, null, 0.0),
    BOUND("bound", Type.REVELATION, Tone.RELEASE, 0, ModAttributes.GENERIC_JUMP_BOOST, 0.5),
    PACE("pace", Type.REVELATION, Tone.RELEASE, 1, EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25),
    IMMERSION("immersion", Type.REVELATION, Tone.RELEASE, 2, null, 0.0);

    public Type type;
    public Tone tone;
    public int index;
    public EntityAttribute attribute;
    public double baseModifier;
    public MutableText text;

    Component(String name, Type type, Tone tone, int index, EntityAttribute attribute, double baseModifier) {
        this.type = type;
        this.tone = tone;
        this.index = index;
        this.attribute = attribute;
        this.baseModifier = baseModifier;
        text = tone.getText(type.translationKey, name, index);
    }

    public static Component[] ASPECTS = {
            VIRTUOUS, EXCESS, HEROIC, TERRAN, SHOCKING, DEATHLY, LIMITLESS, TRANQUIL, UNRIVALED
    };

    public static Component[] REVELATIONS = {
            SPIRIT, VIGOR, TOTALITY, AVARICE, MALAISE, TERROR, BOUND, PACE, IMMERSION
    };

    public static Text getMessage(Component revelation, Component aspect) {
        if (aspect == null) {
            return revelation.text;
        }
        else {
            Text separator = new TranslatableText("separator.purpeille.warpath").formatted(Formatting.GRAY);
            MutableText aspectText = aspect.text.copy().formatted(aspect.tone.formatting[aspect.index]);
            return aspectText.append(separator).append(revelation.text);
        }
    }

    public enum Type {

        REVELATION("revelation", "Revelation"),
        ASPECT("aspect", "Aspect");

        public String translationKey;
        public String nbtKey;

        Type(String translationKey, String  nbtSuffix) {
            this.translationKey = translationKey;
            nbtKey = "Warpath" + nbtSuffix;
        }

        public Component[] getComponents() {
            return switch (this) {
                case ASPECT -> ASPECTS;
                case REVELATION -> REVELATIONS;
            };
        }

        public Component getFromNbt(ItemStack stack) {
            return stack.getOrCreateNbt().contains(nbtKey)
                    ? getComponents()[stack.getOrCreateNbt().getInt(nbtKey)]
                    : null;
        }

        public void addNbt(ItemStack stack, int index) {
            stack.getOrCreateNbt().putInt(nbtKey, index);
        }

        public void applyModifier(ItemStack stack, int index) {
            Component component = getComponents()[index];
            if (component.attribute != null) {
                stack.addAttributeModifier(
                        component.attribute,
                        new EntityAttributeModifier(
                                UUID.fromString("2c67c058-5d5e-4b39-98e3-b3eb9965f7eb"),
                                "Weapon modifier",
                                component.baseModifier,
                                EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                        ),
                        EquipmentSlot.MAINHAND
                );
            }
        }
    }
}
