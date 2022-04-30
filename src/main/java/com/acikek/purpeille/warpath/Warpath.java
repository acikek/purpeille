package com.acikek.purpeille.warpath;

import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class Warpath {

    public static final MutableText SEPARATOR = new TranslatableText("separator.purpeille.warpath")
            .formatted(Formatting.GRAY);

    /**
     * Generates warpath text based on enumerated components.
     * @param aspect If {@code null}, only generates revelation text.
     * @param animated Whether or not to use a sine wave animation for the text color of both components.
     */
    public static Text getWarpath(Revelations revelation, Aspects aspect, boolean animated) {
        boolean hasAspect = aspect != null;
        int wave = animated ? ClampedColor.getWave() : Integer.MIN_VALUE;
        Style style = hasAspect && animated && Synergy.getSynergy(revelation, aspect) == Synergy.IDENTICAL
                ? revelation.value.getStyle(wave)
                : null;
        MutableText revelationText = revelation.value.getText(wave, style);
        if (!hasAspect) {
            return revelationText;
        }
        MutableText aspectText = aspect.value.getText(wave, style);
        return aspectText.append(SEPARATOR).append(revelationText);
    }

    /**
     * Returns the result of {@link Warpath#getWarpath(Revelations, Aspects, boolean)} based on a stack's NBT.
     */
    public static Text getWarpath(ItemStack stack, boolean animated) {
        return getWarpath(Revelations.getFromNbt(stack), Aspects.getFromNbt(stack), animated);
    }

    /**
     * Returns he slot in which the warpath should activate.
     * For tools, returns {@link EquipmentSlot#MAINHAND}.
     * For armor, returns its corresponding slot type.
     * Otherwise, returns {@code null}.
     */
    public static EquipmentSlot getSlot(ItemStack stack) {
        if (stack.getItem() instanceof ToolItem) {
            return EquipmentSlot.MAINHAND;
        }
        if (stack.getItem() instanceof ArmorItem armorItem) {
            return armorItem.getSlotType();
        }
        return null;
    }

    /**
     * Adds attribute modifiers based on component instances to a stack.
     * Use {@link Warpath#add(ItemStack, int, int)} to add a full warpath.
     */
    public static void addModifiers(ItemStack stack, Revelation revelation, Aspect aspect) {
        EntityAttributeModifier modifier = revelation.getModifier(stack, aspect);
        EquipmentSlot slot = getSlot(stack);
        if (slot != null) {
            stack.addAttributeModifier(revelation.attribute, modifier, slot);
        }
    }

    /**
     * Adds component indices to a stack's NBT.
     * Use {@link Warpath#add(ItemStack, int, int)} to add a full warpath.
     */
    public static void addNbt(ItemStack stack, int revelationIndex, int aspectIndex) {
        Type.REVELATION.addNbt(stack, revelationIndex);
        if (aspectIndex != -1) {
            Type.ASPECT.addNbt(stack, aspectIndex);
        }
    }

    /**
     * Adds a warpath to a stack based on component indices.
     * @param aspectIndex The aspect index relative to component order. May be {@code -1} to represent {@code null}.
     */
    public static void add(ItemStack stack, int revelationIndex, int aspectIndex) {
        addNbt(stack, revelationIndex, aspectIndex);
        Revelation revelation = Revelations.values()[revelationIndex].value;
        Aspect aspect = aspectIndex != -1 ? Aspects.values()[aspectIndex].value : null;
        addModifiers(stack, revelation, aspect);
    }

    /**
     * Adds a warpath to a stack based on component instances.
     * Has the same effect as {@link Warpath#add(ItemStack, int, int)}.
     */
    public static void add(ItemStack stack, Revelation revelation, Aspect aspect) {
        addNbt(stack, Aspect.getRelativeIndex(revelation), Aspect.getRelativeIndex(aspect));
        addModifiers(stack, revelation, aspect);
    }

    /**
     * Removes the revelation, the aspect, and all attribute modifers from a stack.
     */
    public static void remove(ItemStack stack) {
        stack.getOrCreateNbt().remove("AttributeModifiers");
        Type.REVELATION.removeNbt(stack);
        if (Type.ASPECT.hasNbt(stack)) {
            Type.ASPECT.removeNbt(stack);
        }
    }
}
