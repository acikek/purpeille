package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.warpath.Tone;
import com.acikek.purpeille.warpath.Type;
import net.minecraft.text.MutableText;

public class Aspect {

    public String name;
    public Tone tone;
    public int index;
    public double modifier;

    public Aspect(String name, Tone tone, int index, double modifier) {
        this.name = name;
        this.tone = tone;
        this.index = index;
        this.modifier = modifier;
    }

    public Type getType() {
        return Type.ASPECT;
    }

    public MutableText getText() {
        return tone.getText(getType().translationKey, name, index);
    }
}
