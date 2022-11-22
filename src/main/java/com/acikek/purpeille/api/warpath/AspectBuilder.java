package com.acikek.purpeille.api.warpath;

import com.acikek.purpeille.warpath.ClampedColor;
import com.acikek.purpeille.warpath.Tone;
import com.acikek.purpeille.warpath.component.Aspect;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

public class AspectBuilder {

    Tone tone;
    int color;
    Ingredient catalyst;
    int index;
    double modifier = 1.0;
    boolean ignoreSlot = false;
    List<Identifier> whitelist;
    MutableText display;

    public AspectBuilder tone(Tone tone) {
        this.tone = tone;
        return this;
    }

    public AspectBuilder color(int color) {
        this.color = color;
        return this;
    }

    public AspectBuilder color(String name) {
        return color(ClampedColor.colorByName(name));
    }

    public AspectBuilder catalyst(Ingredient catalyst) {
        this.catalyst = catalyst;
        return this;
    }

    public AspectBuilder index(int index) {
        this.index = index;
        return this;
    }

    public AspectBuilder modifier(double modifier) {
        this.modifier = modifier;
        return this;
    }

    public AspectBuilder ignoreSlot(boolean ignoreSlot) {
        this.ignoreSlot = ignoreSlot;
        return this;
    }

    public AspectBuilder whitelist(List<Identifier> whitelist) {
        this.whitelist = whitelist;
        return this;
    }

    public AspectBuilder display(MutableText text) {
        this.display = text;
        return this;
    }

    boolean isValid(Identifier id) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(tone);
        Objects.requireNonNull(catalyst);
        Objects.requireNonNull(display);
        if (color < 0) {
            throw new IllegalStateException("'color' cannot be negative");
        }
        if (index != -1 && (index < 0 || index > 8)) {
            throw new IllegalStateException("'index' must be in the range [0-8]");
        }
        return true;
    }

    public Aspect buildAspect(Identifier id) {
        if (isValid(id)) {
            return new Aspect(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist, display);
        }
        return null;
    }
}
