package com.acikek.purpeille.warpath;

import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum Tone {

    STRENGTH(new Formatting[] { Formatting.GOLD, Formatting.RED, Formatting.LIGHT_PURPLE }, 0),
    TENSION(new Formatting[] { Formatting.YELLOW, Formatting.GREEN, Formatting.DARK_GREEN }, 1),
    RELEASE(new Formatting[] { Formatting.AQUA, Formatting.BLUE, Formatting.DARK_AQUA }, 2);

    public Formatting[] formatting;
    public int index;

    Tone(Formatting[] formatting, int index) {
        this.formatting = formatting;
        this.index = index;
    }

    public MutableText getText(String key, String name, int componentIndex) {
        return new TranslatableText(key + ".purpeille." + name).formatted(formatting[componentIndex]);
    }

    public Tone getOpposition() {
        return switch (this) {
            case STRENGTH -> TENSION;
            case TENSION -> RELEASE;
            case RELEASE -> STRENGTH;
        };
    }
}
