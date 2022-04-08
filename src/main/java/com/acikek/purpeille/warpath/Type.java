package com.acikek.purpeille.warpath;

import net.minecraft.item.ItemStack;

public enum Type {

    REVELATION("revelation", "Revelation"),
    ASPECT("aspect", "Aspect");

    public String translationKey;
    public String nbtKey;

    Type(String translationKey, String nbtSuffix) {
        this.translationKey = translationKey;
        nbtKey = "Warpath" + nbtSuffix;
    }

    public boolean hasNbt(ItemStack stack) {
        return stack.hasNbt() && stack.getOrCreateNbt().contains(nbtKey);
    }

    public <T extends Enum<T>> T getFromNbt(ItemStack stack, T[] values) {
        return hasNbt(stack)
                ? values[stack.getOrCreateNbt().getInt(nbtKey)]
                : null;
    }

    public void addNbt(ItemStack stack, int index) {
        stack.getOrCreateNbt().putInt(nbtKey, index);
    }

    public void removeNbt(ItemStack stack) {
        stack.getOrCreateNbt().remove(nbtKey);
    }
}
