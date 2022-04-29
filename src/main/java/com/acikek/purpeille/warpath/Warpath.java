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
import net.minecraft.world.World;

public class Warpath {

    public static final MutableText SEPARATOR = new TranslatableText("separator.purpeille.warpath")
            .formatted(Formatting.GRAY);

    public static Text getWarpath(Revelations revelation, Aspects aspect, World world) {
        boolean hasAspect = aspect != null;
        boolean animated = hasAspect && world != null;
        Style style = animated && Synergy.getSynergy(revelation, aspect) == Synergy.IDENTICAL
                ? revelation.value.getStyle(world)
                : null;
        MutableText revelationText = revelation.value.getText(world, style);
        if (!hasAspect) {
            return revelationText;
        }
        MutableText aspectText = aspect.value.getText(world, style);
        return aspectText.append(SEPARATOR).append(revelationText);
    }

    public static Text getWarpath(Revelations revelation, Aspects aspect) {
        return getWarpath(revelation, aspect, null);
    }

    public static Text getWarpath(ItemStack stack) {
        return getWarpath(Revelations.getFromNbt(stack), Aspects.getFromNbt(stack));
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

    public static void addModifiers(ItemStack stack, Revelation revelation, Aspect aspect) {
        EntityAttributeModifier modifier = revelation.getModifier(stack, aspect);
        EquipmentSlot slot = getSlot(stack);
        if (slot != null) {
            stack.addAttributeModifier(revelation.attribute, modifier, slot);
        }
    }

    public static void addNbt(ItemStack stack, int revelationIndex, int aspectIndex) {
        Type.REVELATION.addNbt(stack, revelationIndex);
        if (aspectIndex != -1) {
            Type.ASPECT.addNbt(stack, aspectIndex);
        }
    }

    public static void add(ItemStack stack, int revelationIndex, int aspectIndex) {
        addNbt(stack, revelationIndex, aspectIndex);
        Revelation revelation = Revelations.values()[revelationIndex].value;
        Aspect aspect = aspectIndex != -1 ? Aspects.values()[aspectIndex].value : null;
        addModifiers(stack, revelation, aspect);
    }

    public static void add(ItemStack stack, Revelation revelation, Aspect aspect) {
        addNbt(stack, Aspect.getRelativeIndex(revelation), Aspect.getRelativeIndex(aspect));
        addModifiers(stack, revelation, aspect);
    }

    public static void remove(ItemStack stack) {
        stack.getOrCreateNbt().remove("AttributeModifiers");
        Type.REVELATION.removeNbt(stack);
        if (Type.ASPECT.hasNbt(stack)) {
            Type.ASPECT.removeNbt(stack);
        }
    }
}
