package com.acikek.purpeille.warpath;

import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum Tone {

    STRENGTH(new Formatting[] { Formatting.GOLD, Formatting.RED, Formatting.LIGHT_PURPLE }),
    TENSION(new Formatting[] { Formatting.YELLOW, Formatting.GREEN, Formatting.DARK_GREEN }),
    RELEASE(new Formatting[] { Formatting.AQUA, Formatting.BLUE, Formatting.DARK_AQUA });

    public Formatting[] formatting;

    Tone(Formatting[] formatting) {
        this.formatting = formatting;
    }

    public MutableText getText(String key, String name, int index) {
        return new TranslatableText(key + ".purpeille." + name).formatted(formatting[index]);
    }
}
