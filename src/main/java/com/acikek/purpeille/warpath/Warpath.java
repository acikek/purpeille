package com.acikek.purpeille.warpath;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class Warpath {

    public static UUID WARPATH_ID = UUID.fromString("2c67c058-5d5e-4b39-98e3-b3eb9965f7eb");

    public static Text getWarpath(Revelation revelation, Aspect aspect) {
        MutableText revelationText = revelation.tone.getText(Type.REVELATION.translationKey, revelation.name, revelation.index);
        if (aspect == null) {
            return revelationText;
        }
        else {
            Text separator = new TranslatableText("separator.purpeille.warpath").formatted(Formatting.GRAY);
            MutableText aspectText = aspect.tone.getText(Type.ASPECT.translationKey, aspect.name, aspect.index)
                    .formatted(aspect.tone.formatting[aspect.index]);
            return aspectText.append(separator).append(revelationText);
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
        Revelation revelation = Revelation.values()[revelationIndex];
        double modifier = aspectIndex != -1 ? revelation.getModifier(Aspect.values()[8 - aspectIndex]) : revelation.modifier;
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
}
