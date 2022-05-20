package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.warpath.ClampedColor;
import com.acikek.purpeille.warpath.Tone;
import com.acikek.purpeille.warpath.Type;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class Component {

    public static Map<Identifier, Aspect> ASPECTS = new HashMap<>();
    public static Map<Identifier, Revelation> REVELATIONS = new HashMap<>();

    public Identifier id;
    public Tone tone;
    public int color;
    public Ingredient catalyst;
    public int index;
    public double modifier;
    public boolean ignoreSlot;
    public List<Identifier> whitelist;
    public int relativeIndex;
    public ClampedColor waveColor;
    public MutableText baseText;
    public MutableText defaultText;

    public Component(Identifier id, Tone tone, int color, Ingredient catalyst, int index, double modifier, boolean ignoreSlot, List<Identifier> whitelist) {
        this.id = id;
        this.tone = tone;
        this.color = color;
        this.catalyst = catalyst;
        this.index = index;
        this.modifier = modifier;
        this.ignoreSlot = ignoreSlot;
        this.whitelist = whitelist;
        relativeIndex = tone.index * 3 + index;
        waveColor = new ClampedColor(color);
        baseText = new TranslatableText(getType().translationKey + "." + id.getNamespace() + "." + id.getPath());
        defaultText = baseText.styled(style -> style.withColor(color));
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

    public boolean isCompatible(Component other) {
        return other == null || whitelist == null || whitelist.contains(other.id);
    }

    public static boolean areCompatible(Component first, Component second) {
        return first.isCompatible(second) && (second == null || second.isCompatible(first));
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

    public static List<Identifier> whitelistFromJson(JsonObject obj) {
        JsonArray whitelistElements = JsonHelper.getArray(obj, "whitelist", null);
        if (whitelistElements == null) {
            return null;
        }
        List<Identifier> whitelist = new ArrayList<>();
        for (JsonElement element : whitelistElements) {
            whitelist.add(Identifier.tryParse(JsonHelper.asString(element, "whitelist id")));
        }
        return whitelist;
    }

    public void writeWhitelist(PacketByteBuf buf) {
        if (whitelist != null) {
            buf.writeInt(whitelist.size());
            for (Identifier id : whitelist) {
                buf.writeIdentifier(id);
            }
        }
        else {
            buf.writeInt(-1);
        }
    }

    public static List<Identifier> readWhitelist(PacketByteBuf buf) {
        int size = buf.readInt();
        if (size == -1) {
            return null;
        }
        List<Identifier> whitelist = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            whitelist.add(buf.readIdentifier());
        }
        return whitelist;
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(id);
        buf.writeEnumConstant(tone);
        buf.writeInt(color);
        catalyst.write(buf);
        buf.writeInt(index);
        buf.writeDouble(modifier);
        buf.writeBoolean(ignoreSlot);
        writeWhitelist(buf);
    }
}
