package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.warpath.ClampedColor;
import com.acikek.purpeille.warpath.Tone;
import com.acikek.purpeille.warpath.Type;
import com.google.gson.JsonElement;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class Component {

    public static Map<Identifier, Aspect> ASPECTS = new HashMap<>();
    public static Map<Identifier, Revelation> REVELATIONS = new HashMap<>();

    public Identifier id;
    public Tone tone;
    public Formatting color;
    public Item catalyst;
    public int index;
    public double modifier;
    public boolean ignoreSlot;
    public int relativeIndex;
    public ClampedColor waveColor;
    public MutableText baseText;
    public MutableText defaultText;

    public Component(Identifier id, Tone tone, Formatting color, Item catalyst, int index, double modifier, boolean ignoreSlot) {
        this.id = id;
        this.tone = tone;
        this.color = color;
        this.catalyst = catalyst;
        this.index = index;
        this.modifier = modifier;
        this.ignoreSlot = ignoreSlot;
        relativeIndex = tone.index * 3 + index;
        waveColor = new ClampedColor(color);
        baseText = new TranslatableText(getType().translationKey + ".purpeille." + id.getPath());
        defaultText = baseText.formatted(color);
    }

    public abstract Type getType();

    public Style getStyle(int wave) {
        return Style.EMPTY.withColor(waveColor.getModified(wave));
    }

    public MutableText getText(Style style) {
        return baseText.copy().setStyle(style);
    }

    public MutableText getText(int wave, Style style) {
        if (style == null) {
            if (wave == Integer.MIN_VALUE) {
                return defaultText.shallowCopy();
            }
            return getText(getStyle(wave));
        }
        return getText(style);
    }

    public static <T extends Enum<T>> T enumFromJson(JsonElement element, Function<String, T> valueOf, String name) {
        String key = element.getAsString();
        try {
            return valueOf.apply(key.toUpperCase());
        }
        catch (Exception e) {
            throw new IllegalStateException("'" + key + "' is not a valid " + name);
        }
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(id);
        buf.writeEnumConstant(tone);
        buf.writeEnumConstant(color);
        buf.writeIdentifier(Registry.ITEM.getId(catalyst));
        buf.writeInt(index);
        buf.writeDouble(modifier);
        buf.writeBoolean(ignoreSlot);
    }
}
