package com.acikek.purpeille.warpath;

import com.acikek.purpeille.warpath.component.Aspect;
import net.minecraft.item.ItemStack;

public enum Aspects {

    VIRTUOUS("virtuous", Tone.STRENGTH, 0, 1.4),
    EXCESS("excess", Tone.STRENGTH, 1, 1.35),
    HEROIC("heroic", Tone.STRENGTH, 2, 1.5),
    TERRAN("terran", Tone.TENSION, 0, 1.25),
    SHOCKING("shocking", Tone.TENSION, 1, 1.5),
    DEATHLY("deathly", Tone.TENSION, 2, 1.2),
    LIMITLESS("limitless", Tone.RELEASE, 0, 1.3),
    TRANQUIL("tranquil", Tone.RELEASE, 1, 1.15),
    UNRIVALED("unrivaled", Tone.RELEASE, 2, 1.4);

    public Aspect value;

    Aspects(String name, Tone tone, int index, double modifier) {
        value = new Aspect(name, tone, index, modifier);
    }

    public static Aspects getFromNbt(ItemStack stack) {
        return Type.ASPECT.getFromNbt(stack, values());
    }
}
