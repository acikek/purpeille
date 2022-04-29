package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.warpath.ClampedColor;
import com.acikek.purpeille.warpath.Tone;
import com.acikek.purpeille.warpath.Type;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.world.World;

public class Aspect {

    public String name;
    public Tone tone;
    public int index;
    public double modifier;
    public int relativeIndex;
    public ClampedColor color;
    public MutableText baseText;
    public MutableText defaultText;

    public Aspect(String name, Tone tone, int index, double modifier) {
        this.name = name;
        this.tone = tone;
        this.index = index;
        this.modifier = modifier;
        relativeIndex = tone.index * 3 + index;
        color = new ClampedColor(tone.getFormatting(index));
        baseText = tone.getText(getType().translationKey, name);
        defaultText = baseText.formatted(tone.getFormatting(index));
    }

    public Type getType() {
        return Type.ASPECT;
    }

    public Style getStyle(World world) {
        int wave = ClampedColor.getWave(world);
        return Style.EMPTY.withColor(color.getModified(wave));
    }

    public MutableText getText(Style style) {
        return baseText.copy().setStyle(style);
    }

    public MutableText getText(World world) {
        if (world == null) {
            return defaultText;
        }
        return getText(getStyle(world));
    }

    public MutableText getText(World world, Style style) {
        if (style == null) {
            return getText(world);
        }
        return getText(style);
    }

    public static int getRelativeIndex(Aspect aspect) {
        if (aspect == null) {
            return -1;
        }
        return aspect.relativeIndex;
    }
}
