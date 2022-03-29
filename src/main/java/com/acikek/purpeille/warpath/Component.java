package com.acikek.purpeille.warpath;

import com.acikek.purpeille.attribute.ModAttributes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.UUID;

public enum Component {

    // Aspects
    VIRTUOUS("virtuous", Type.ASPECT, Tone.STRENGTH, 0, null, 1.0),
    EXCESS("excess", Type.ASPECT, Tone.STRENGTH, 1, null, 1.0),
    HEROIC("heroic", Type.ASPECT, Tone.STRENGTH, 2, null, 1.0),
    TERRAN("terran", Type.ASPECT, Tone.TENSION, 0, null, 1.0),
    SHOCKING("shocking", Type.ASPECT, Tone.TENSION, 1, null, 1.0),
    DEATHLY("deathly", Type.ASPECT, Tone.TENSION, 2, null, 1.0),
    LIMITLESS("limitless", Type.ASPECT, Tone.RELEASE, 0, null, 1.3),
    TRANQUIL("tranquil", Type.ASPECT, Tone.RELEASE, 1, null, 1.0),
    UNRIVALED("unrivaled", Type.ASPECT, Tone.RELEASE, 2, null, 1.4),

    // Revelations
    SPIRIT("spirit", Type.REVELATION, Tone.STRENGTH, 0, EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0),
    VIGOR("vigor", Type.REVELATION, Tone.STRENGTH, 1, EntityAttributes.GENERIC_ATTACK_SPEED, 1.0),
    TOTALITY("totality", Type.REVELATION, Tone.STRENGTH, 2, EntityAttributes.GENERIC_MAX_HEALTH, 1.0),
    AVARICE("avarice", Type.REVELATION, Tone.TENSION, 0, EntityAttributes.GENERIC_LUCK, 2.0),
    // TODO poison themed stuff
    MALAISE("malaise", Type.REVELATION, Tone.TENSION, 1, null, 0.0),
    TERROR("terror", Type.REVELATION, Tone.TENSION, 2, EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0),
    BOUND("bound", Type.REVELATION, Tone.RELEASE, 0, ModAttributes.GENERIC_JUMP_BOOST, 0.25),
    PACE("pace", Type.REVELATION, Tone.RELEASE, 1, EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25),
    IMMERSION("immersion", Type.REVELATION, Tone.RELEASE, 2, ModAttributes.GENERIC_WATER_SPEED, 1.3);

    public String name;
    public Type type;
    public Tone tone;
    public int index;
    public EntityAttribute attribute;
    public double modifier;

    Component(String name, Type type, Tone tone, int index, EntityAttribute attribute, double modifier) {
        this.name = name;
        this.type = type;
        this.tone = tone;
        this.index = index;
        this.attribute = attribute;
        this.modifier = modifier;
    }

    public MutableText getText() {
        return new TranslatableText(type.translationKey + ".purpeille." + name).formatted(tone.formatting[index]);
    }

    public Text getRite() {
        return new TranslatableText("rite.purpeille." + name).formatted(Formatting.GRAY);
    }

    public double getModifier(Component aspect) {
        if (tone.getOpposition() == aspect.tone) {
            return -modifier;
        }
        double value = modifier * aspect.modifier;
        return tone == aspect.tone ? value * 1.5 : value;
    }

    public boolean getSynergized(Component aspect) {
        return tone == aspect.tone && index == aspect.index;
    }

    public static UUID WARPATH_ID = UUID.fromString("2c67c058-5d5e-4b39-98e3-b3eb9965f7eb");

    public static Component[] ASPECTS = {
            VIRTUOUS, EXCESS, HEROIC, TERRAN, SHOCKING, DEATHLY, LIMITLESS, TRANQUIL, UNRIVALED
    };

    public static Component[] REVELATIONS = {
            SPIRIT, VIGOR, TOTALITY, AVARICE, MALAISE, TERROR, BOUND, PACE, IMMERSION
    };

    public static Text getWarpath(Component revelation, Component aspect) {
        if (aspect == null) {
            return revelation.getText();
        }
        else {
            Text separator = new TranslatableText("separator.purpeille.warpath").formatted(Formatting.GRAY);
            MutableText aspectText = aspect.getText().formatted(aspect.tone.formatting[aspect.index]);
            return aspectText.append(separator).append(revelation.getText());
        }
    }

    public static EquipmentSlot getSlot(ItemStack stack) {
        if (stack.getItem() instanceof ToolItem) {
            return EquipmentSlot.MAINHAND;
        }
        if (stack.getItem() instanceof ArmorItem armorItem) {
            return armorItem.getSlotType();
        }
        return null;
    }

    public static void addModifiers(ItemStack stack, int revelationIndex, int aspectIndex) {
        Component revelation = Type.REVELATION.getComponents()[revelationIndex];
        double modifier = aspectIndex != -1 ? revelation.getModifier(Type.ASPECT.getComponents()[8 - aspectIndex]) : revelation.modifier;
        EntityAttributeModifier attributeModifier = new EntityAttributeModifier(WARPATH_ID, "Warpath modifier", modifier, EntityAttributeModifier.Operation.ADDITION);
        if (revelation.attribute != null) {
            EquipmentSlot slot = getSlot(stack);
            if (slot != null) {
                stack.addAttributeModifier(revelation.attribute, attributeModifier, slot);
            }
        }
    }

    public static void apply(ItemStack stack, int revelationIndex, int aspectIndex) {
        Type.REVELATION.addNbt(stack, revelationIndex);
        if (aspectIndex != -1) {
            Type.ASPECT.addNbt(stack, 8 - aspectIndex);
        }
        addModifiers(stack, revelationIndex, aspectIndex);
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
    }
}
