package com.acikek.purpeille.warpath;

import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Warpath {

    public static final String NBT_KEY = "WarpathData";
    public static final MutableText SEPARATOR = new TranslatableText("separator.purpeille.warpath");
    public static final ClampedColor SEPARATOR_COLOR = new ClampedColor(Formatting.GRAY);

    /**
     * Generates warpath text based on component instances.
     * @param aspect If {@code null}, only generates revelation text.
     * @param animated Whether to use a sine wave animation for the text color of both components.
     * @param rite Whether to include the 'rite' text if {@link Synergy#getSynergy(Revelation, Aspect)} returns {@link Synergy#IDENTICAL}.
     */
    public static List<Text> getWarpath(Revelation revelation, Aspect aspect, boolean animated, boolean rite) {
        boolean hasAspect = aspect != null;
        int wave = animated ? ClampedColor.getWave() : Integer.MIN_VALUE;
        Style style = hasAspect && animated && Synergy.getSynergy(revelation, aspect) == Synergy.IDENTICAL
                ? revelation.getStyle(wave)
                : null;
        MutableText revelationText = revelation.getText(wave, style);
        if (!hasAspect) {
            return Collections.singletonList(revelationText);
        }
        List<Text> text = new ArrayList<>();
        MutableText aspectText = aspect.getText(wave, style);
        MutableText separator = SEPARATOR.copy().styled(s -> s.withColor(SEPARATOR_COLOR.getModified(wave)));
        text.add(aspectText.append(separator).append(revelationText));
        if (rite && Synergy.getSynergy(revelation, aspect) == Synergy.IDENTICAL) {
            text.add(revelation.rite);
        }
        return text;
    }

    /**
     * Returns the result of {@link Warpath#getWarpath(Revelation, Aspect, boolean, boolean)} based on a stack's NBT.
     */
    public static List<Text> getWarpath(ItemStack stack, boolean animated, boolean rite) {
        NbtCompound data = getData(stack);
        if (data == null) {
            return null;
        }
        Revelation revelation = Revelation.fromNbt(data);
        if (revelation == null) {
            return null;
        }
        Aspect aspect = Aspect.fromNbt(data);
        return getWarpath(revelation, aspect, animated, rite);
    }

    public static NbtCompound getData(ItemStack stack) {
        if (!stack.hasNbt()) {
            return null;
        }
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains(NBT_KEY)) {
            return null;
        }
        return nbt.getCompound(NBT_KEY);
    }

    /**
     * Returns the slot in which the warpath should activate.
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
     * Use {@link Warpath#add(ItemStack, Revelation, Aspect)} to add a full warpath.
     */
    public static void addModifiers(ItemStack stack, Revelation revelation, Aspect aspect) {
        EntityAttributeModifier modifier = revelation.getModifier(stack, aspect);
        EquipmentSlot slot = getSlot(stack);
        if (slot != null) {
            stack.addAttributeModifier(revelation.attribute, modifier, slot);
        }
    }

    /**
     * Adds a warpath to a stack based on component instances.<br>
     * Encodes Warpath data under the {@link Warpath#NBT_KEY} key.
     * @param aspect If {@code null}, does not append Aspect data.
     */
    public static void add(ItemStack stack, Revelation revelation, Aspect aspect) {
        NbtCompound data = new NbtCompound();
        Type.REVELATION.addNbt(data, revelation.id);
        if (aspect != null) {
            Type.ASPECT.addNbt(data, aspect.id);
        }
        stack.getOrCreateNbt().put(NBT_KEY, data);
        addModifiers(stack, revelation, aspect);
    }

    /**
     * Removes components and all attribute modifiers from a stack.
     */
    public static void remove(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.remove(NBT_KEY);
        nbt.remove("AttributeModifiers");
    }
}
