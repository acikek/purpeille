package com.acikek.purpeille.api;

import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

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

    default boolean hasRevelation() {
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
            nbt.putInt("CustomModelData", 1);
            NbtCompound imbuements = new NbtCompound();
            new Imbuements(energy, 1, new HashSet<>(itemsUsed)).write(imbuements);
            nbt.put(Imbuements.KEY, imbuements);
        }
        NbtCompound imbuementsNbt = nbt.getCompound(Imbuements.KEY);
        Imbuements imbuements = Imbuements.read(imbuementsNbt);
        imbuements.energy += energy;
        imbuements.energy = Math.min(imbuements.energy, MAX_ENERGY);
        imbuements.count++;
        imbuements.itemsUsed.addAll(itemsUsed);
        imbuements.write(imbuementsNbt);
        nbt.put(Imbuements.KEY, imbuementsNbt);
    }
}
