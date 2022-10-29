package com.acikek.purpeille.api;

import com.acikek.purpeille.warpath.Warpath;
import com.acikek.purpeille.warpath.WarpathData;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public interface AbyssalToken {

    int MAX_ENERGY = 100;

    Set<AbyssalToken> TOKENS = new HashSet<>();

    static void clearTokens() {
        for (AbyssalToken token : TOKENS) {
            token.setRevelation(null);
        }
        TOKENS.clear();
    }

    Revelation getRevelation();

    void setRevelation(Revelation revelation);

    default boolean isAbyssalToken() {
        return getRevelation() != null;
    }

    static int getPositiveValue(int energy) {
        int s = energy - 65;
        int denom = energy < 65 ? 64 : 30;
        return - MathHelper.ceil((float) s * s / denom + 66.0f);
    }

    static int getNegativeValue(int energy) {
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

    static void imbue(ItemStack stack, int energy, Collection<Item> itemsUsed) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains(Imbuements.KEY)) {
            NbtCompound imbuements = new NbtCompound();
            new Imbuements(energy, 1, new HashSet<>(itemsUsed)).writeNbt(imbuements);
            nbt.put(Imbuements.KEY, imbuements);
        }
        NbtCompound imbuementsNbt = nbt.getCompound(Imbuements.KEY);
        Imbuements imbuements = Imbuements.readNbt(imbuementsNbt);
        imbuements.energy += energy;
        imbuements.energy = Math.min(imbuements.energy, MAX_ENERGY);
        imbuements.count++;
        imbuements.itemsUsed.addAll(itemsUsed);
        imbuements.writeNbt(imbuementsNbt);
        nbt.put(Imbuements.KEY, imbuementsNbt);
    }

    static void apply(ItemStack base, ItemStack tokenStack) {
        if (tokenStack.getItem() instanceof AbyssalToken token) {
            Revelation revelation = token.getRevelation();
            int energy = Imbuements.readEnergy(tokenStack.getOrCreateNbt().getCompound(Imbuements.KEY));
            int positive = getPositiveValue(energy);
            int negative = getNegativeValue(energy);
            double value = (positive / (float) MAX_ENERGY) * revelation.abyssalite.max;
            // TODO modify this when converting to logarithmic system
            UUID uuid = UUID.fromString("5bf752ea-56f2-11ed-9b6a-0242ac120002");
            EquipmentSlot slot = Warpath.getSlot(base);
            // TODO: consider modifying the existing modifier's value?
            EntityAttributeModifier modifier = new EntityAttributeModifier(
                    uuid, "Abyssal token modifier", value / 4.0,
                    revelation.attribute.operation
            );
            base.addAttributeModifier(revelation.attribute.value, modifier, slot);
            // TODO: make this better
            EntityAttributeModifier newModifier = new EntityAttributeModifier(
                    uuid, "Abyssal token bonus modifier", value,
                    revelation.abyssalite.attribute.operation
            );
            base.addAttributeModifier(revelation.abyssalite.attribute.value, newModifier, slot);
            NbtCompound nbt = base.getOrCreateNbt();
            WarpathData data = Warpath.getData(base);
            if (data != null) {
                data.appliedToken = Registry.ITEM.getId(tokenStack.getItem());
                data.writeNbt(nbt.getCompound(WarpathData.KEY));
            }
            // TODO: Apply negative attribute value
        }
    }
}
