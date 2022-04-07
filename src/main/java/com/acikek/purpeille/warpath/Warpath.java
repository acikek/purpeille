package com.acikek.purpeille.warpath;

import com.acikek.purpeille.warpath.component.Revelation;
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

    public static Text getWarpath(Revelations revelation, Aspects aspect) {
        MutableText revelationText = revelation.value.getText();
        if (aspect == null) {
            return revelationText;
        }
        else {
            Text separator = new TranslatableText("separator.purpeille.warpath").formatted(Formatting.GRAY);
            MutableText aspectText = aspect.value.getText();
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
        Revelation revelation = Revelations.values()[revelationIndex].value;
        double modifier = revelation.getModifier(stack, aspectIndex != -1 ? Aspects.values()[8 - aspectIndex].value : null);
        EntityAttributeModifier attributeModifier = new EntityAttributeModifier(WARPATH_ID, "Warpath modifier", modifier, revelation.operation);
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
