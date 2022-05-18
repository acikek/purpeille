package com.acikek.purpeille.warpath;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum Tone {

    STRENGTH(0),
    TENSION(1),
    RELEASE(2);

    public int index;

    Tone(int index) {
        this.index = index;
    }

    public Tone getOpposition() {
        return switch (this) {
            case STRENGTH -> TENSION;
            case TENSION -> RELEASE;
            case RELEASE -> STRENGTH;
        };
    }
}
