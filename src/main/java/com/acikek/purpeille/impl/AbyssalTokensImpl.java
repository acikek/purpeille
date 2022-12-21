package com.acikek.purpeille.impl;

import com.acikek.purpeille.api.abyssal.AbyssalToken;
import com.acikek.purpeille.api.abyssal.ImbuementData;
import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.warpath.Warpath;
import com.acikek.purpeille.warpath.WarpathData;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AbyssalTokensImpl {

    public static final int MAX_ENERGY = 100;

    public static Set<AbyssalToken> TOKENS = new HashSet<>();

    public static void clearTokens() {
        for (AbyssalToken token : TOKENS) {
            token.setRevelation(null);
        }
        TOKENS.clear();
    }

    public static int getPositiveValue(int energy) {
        int s = energy - 65;
        int denom = energy < 65 ? 64 : 30;
        return MathHelper.ceil((float) - s * s / denom + 66.0f);
    }

    public static int getNegativeValue(int energy) {
        float result;
        if (energy < 60) {
            result = 1.0f / 7.35f * energy;
        }
        else if (energy <= 81) {
            float s = MathHelper.sqrt(energy - 50);
            result = - MathHelper.sqrt(1000.0f - (s * s * s * s)) + 38.164f;
        }
        else {
            float s = energy - 105.0f;
            result = - s * s / 13.17f + 75.5f;
        }
        return MathHelper.ceil(result);
    }

    public static void imbue(ItemStack stack, int energy, Collection<Item> itemsUsed) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains(ImbuementData.KEY)) {
            NbtCompound imbuements = new NbtCompound();
            new ImbuementData(energy, 1, new HashSet<>(itemsUsed)).writeNbt(imbuements);
            nbt.put(ImbuementData.KEY, imbuements);
        }
        NbtCompound imbuementsNbt = nbt.getCompound(ImbuementData.KEY);
        ImbuementData imbuements = ImbuementData.readNbt(imbuementsNbt);
        imbuements.energy += energy;
        imbuements.energy = Math.min(imbuements.energy, MAX_ENERGY);
        imbuements.count++;
        imbuements.itemsUsed.addAll(itemsUsed);
        imbuements.writeNbt(imbuementsNbt);
        nbt.put(ImbuementData.KEY, imbuementsNbt);
    }

    public static void modifyExistingModifier(ItemStack stack, Revelation revelation, double value) {
        NbtList list = stack.getOrCreateNbt().getList("AttributeModifiers", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound compound = list.getCompound(i);
            EntityAttributeModifier modifier = EntityAttributeModifier.fromNbt(compound);
            if (modifier != null && revelation.attribute.uuids.contains(modifier.getId())) {
                compound.putDouble("Amount", modifier.getValue() + value);
            }
        }
    }

    public static void apply(ItemStack stack, Revelation revelation, int positive, int negative, Item appliedToken) {
        // Calculate proportion from max positive energy
        double proportion = positive / 70.0f;
        // Add a small bonus to the existing Warpath modifier
        modifyExistingModifier(stack, revelation, proportion * revelation.abyssalite.baseBonus);
        EquipmentSlot slot = Warpath.getSlot(stack, revelation.abyssalite.attribute);
        // Add the abyssal token modifier
        EntityAttributeModifier modifier = revelation.abyssalite.attribute.getModifier(slot, "Abyssal token modifier", proportion * revelation.abyssalite.max);
        stack.addAttributeModifier(revelation.abyssalite.attribute.value, modifier, slot);
        // Write new information to the Warpath NBT data
        NbtCompound nbt = stack.getOrCreateNbt();
        WarpathData data = Warpath.getData(stack);
        if (data != null) {
            data.appliedToken = Registry.ITEM.getId(appliedToken);
            data.writeNbt(nbt.getCompound(WarpathData.KEY));
        }
        // Apply negative abyssal allegiance value
        EntityAttributeModifier negativeModifier = new EntityAttributeModifier(
                ModAttributes.ABYSSAL_ALLEGIANCE_SCALING_DATA.uuidMap.get(slot), "Abyssal token modifier",
                negative, EntityAttributeModifier.Operation.ADDITION
        );
        stack.addAttributeModifier(ModAttributes.GENERIC_ABYSSAL_ALLEGIANCE, negativeModifier, slot);
    }

    public static int[] apply(ItemStack stack, Revelation revelation, int energy, Item appliedToken) {
        int positive = getPositiveValue(energy);
        int negative = getNegativeValue(energy);
        apply(stack, revelation, positive, negative, appliedToken);
        return new int[] { positive, negative };
    }

    public static int[] apply(ItemStack base, ItemStack tokenStack) {
        if (tokenStack.getItem() instanceof AbyssalToken token) {
            Revelation revelation = token.getRevelation();
            int energy = ImbuementData.readEnergy(tokenStack.getOrCreateNbt().getCompound(ImbuementData.KEY));
            return apply(base, revelation, energy, tokenStack.getItem());
        }
        return null;
    }
}
