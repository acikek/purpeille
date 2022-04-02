package com.acikek.purpeille.warpath;

import net.minecraft.item.ItemStack;

public enum Aspect {

    VIRTUOUS("virtuous", Tone.STRENGTH, 0, 1.4),
    EXCESS("excess", Tone.STRENGTH, 1, 1.35),
    HEROIC("heroic", Tone.STRENGTH, 2, 1.5),
    TERRAN("terran", Tone.TENSION, 0, 1.2),
    SHOCKING("shocking", Tone.TENSION, 1, 1.5),
    DEATHLY("deathly", Tone.TENSION, 2, 1.2),
    LIMITLESS("limitless", Tone.RELEASE, 0, 1.3),
    TRANQUIL("tranquil", Tone.RELEASE, 1, 1.2),
    UNRIVALED("unrivaled", Tone.RELEASE, 2, 1.4);

    public String name;
    public Tone tone;
    public int index;
    public double modifier;

    Aspect(String name, Tone tone, int index, double modifier) {
        this.name = name;
        this.tone = tone;
        this.index = index;
        this.modifier = modifier;
    }

    public static Aspect getFromNbt(ItemStack stack) {
        return Type.ASPECT.getFromNbt(stack, values());
    }
}
