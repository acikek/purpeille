package com.acikek.purpeille.warpath;

import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;
import com.acikek.purpeille.warpath.component.Type;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Warpath {

    public static final String ETR_NBT_KEY = "enchant_the_rainbow:GlintColor";
    public static final MutableText SEPARATOR = Text.translatable("separator.purpeille.warpath");
    public static final ClampedColor SEPARATOR_COLOR = new ClampedColor(Formatting.GRAY);

    /**
     * Generates warpath tooltip text based on component instances.
     * @param aspect If {@code null}, only generates revelation text.
     * @param animated Whether to use a sine wave animation for the text color of both components.
     * @param rite Whether to include the 'rite' text if {@link Synergy#getSynergy(Revelation, Aspect)} returns {@link Synergy#IDENTICAL}.
     */
    public static List<Text> getTooltip(Revelation revelation, Aspect aspect, boolean animated, boolean rite) {
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
     * Returns the result of {@link Warpath#getTooltip(Revelation, Aspect, boolean, boolean)} based on a stack's NBT.
     */
    public static List<Text> getTooltip(ItemStack stack, boolean animated, boolean rite) {
        WarpathData data = getData(stack);
        if (data == null) {
            return null;
        }
        Revelation revelation = data.getRevelation();
        if (revelation == null) {
            return null;
        }
        Aspect aspect = data.getAspect();
        return getTooltip(revelation, aspect, animated, rite);
    }

    /**
     * TODO: Migrate over to a class-based system
     */
    public static WarpathData getData(ItemStack stack) {
        if (!stack.hasNbt()) {
            return null;
        }
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains(WarpathData.KEY)) {
            return null;
        }
        return WarpathData.fromNbt(nbt.getCompound(WarpathData.KEY));
    }

    /**
     * Returns the slot in which the warpath should activate.
     * If the specified {@link AttributeData} has a {@link AttributeData#slot}, returns it.
     * For tools, returns {@link EquipmentSlot#MAINHAND}.
     * For armor, returns its corresponding slot type.
     * Otherwise, returns {@code null}.
     */
    public static EquipmentSlot getSlot(ItemStack stack, AttributeData attribute) {
        if (attribute.slot != null) {
            return attribute.slot;
        }
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
        EquipmentSlot slot = getSlot(stack, revelation.attribute);
        if (slot != null) {
            EntityAttributeModifier modifier = revelation.getModifier(stack, slot, aspect);
            stack.addAttributeModifier(revelation.attribute.value, modifier, slot);
        }
    }

    /**
     * Adds warpath data to an NBT compound based on component instances.
     * @param aspect If {@code null}, does not append Aspect data.
     */
    public static void addData(NbtCompound nbt, Revelation revelation, Aspect aspect) {
        Type.REVELATION.addNbt(nbt, revelation.id);
        if (aspect != null) {
            Type.ASPECT.addNbt(nbt, aspect.id);
        }
    }

    /**
     * Adds warpath data to an NBT compound based on component identifiers.<br>
     * The identifiers should be included in their respective registries.
     */
    public static void addData(NbtCompound nbt, Identifier revelationId, Identifier aspectId) {
        Revelation revelation = Revelation.REVELATIONS.get(revelationId);
        if (revelation == null) {
            return;
        }
        addData(nbt, revelation, Aspect.ASPECTS.get(aspectId));
    }

    /**
     * Adds a warpath to a stack.<br>
     * Encodes Warpath data using {@link Warpath#addData(NbtCompound, Revelation, Aspect)} under the {@link WarpathData#KEY} key.
     */
    public static void add(ItemStack stack, Revelation revelation, Aspect aspect) {
        NbtCompound data = new NbtCompound();
        addData(data, revelation, aspect);
        NbtCompound stackNbt = stack.getOrCreateNbt();
        stackNbt.put(WarpathData.KEY, data);
        stackNbt.putInt(ETR_NBT_KEY, revelation.dyeColor);
        addModifiers(stack, revelation, aspect);
    }

    /**
     * Removes components and all attribute modifiers from a stack.
     */
    public static void remove(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.remove(WarpathData.KEY);
        nbt.remove(ETR_NBT_KEY);
        nbt.remove("AttributeModifiers");
    }
}
