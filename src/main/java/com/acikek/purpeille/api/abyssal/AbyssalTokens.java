package com.acikek.purpeille.api.abyssal;

import com.acikek.purpeille.impl.AbyssalTokensImpl;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Set;

public class AbyssalTokens {

    public static int getMaxEnergy() {
        return AbyssalTokensImpl.MAX_ENERGY;
    }

    public static Set<AbyssalToken> getTokens() {
        return AbyssalTokensImpl.TOKENS;
    }

    public static void clearTokens() {
        AbyssalTokensImpl.clearTokens();
    }

    public static void imbue(ItemStack stack, int energy, Collection<Item> itemsUsed) {
        AbyssalTokensImpl.imbue(stack, energy, itemsUsed);
    }

    public static int[] apply(ItemStack stack, Revelation revelation, int energy, Item appliedToken) {
        return AbyssalTokensImpl.apply(stack, revelation, energy, appliedToken);
    }

    public static int[] apply(ItemStack base, ItemStack tokenStack) {
        return AbyssalTokensImpl.apply(base, tokenStack);
    }
}
