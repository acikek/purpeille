package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.api.warpath.AspectBuilder;
import com.acikek.purpeille.warpath.ClampedColor;
import com.acikek.purpeille.warpath.Tone;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.EnumUtils;

import java.util.ArrayList;
import java.util.List;

public class Aspect implements Writer {

    public static final String KEY = "aspect";

    public Identifier id;
    public Tone tone;
    public int color;
    public Ingredient catalyst;
    public int index;
    public double modifier;
    public boolean ignoreSlot;
    public List<Identifier> whitelist;
    public MutableText display;
    public int relativeIndex;
    public ClampedColor waveColor;
    public MutableText defaultText;

    public Aspect(Identifier id, Tone tone, int color, Ingredient catalyst, int index, double modifier, boolean ignoreSlot, List<Identifier> whitelist, MutableText display) {
        this.id = id;
        this.tone = tone;
        this.color = color;
        this.catalyst = catalyst;
        this.index = index;
        this.modifier = modifier;
        this.ignoreSlot = ignoreSlot;
        this.whitelist = whitelist;
        this.display = display;
        relativeIndex = tone.index * 3 + index;
        waveColor = new ClampedColor(color);
        defaultText = display.styled(style -> style.withColor(color));
    }

    public Style getStyle(int wave) {
        return Style.EMPTY.withColor(waveColor.getModified(wave));
    }

    public MutableText getText(Style style) {
        return display.copy().setStyle(style);
    }

    public MutableText getText(int wave, Style style) {
        if (style == null) {
            if (wave == Integer.MIN_VALUE) {
                return defaultText.copy();
            }
            return getText(getStyle(wave));
        }
        return getText(style);
    }

    public int getIndex() {
        return 8 - relativeIndex;
    }

    public static String getIdKey(String value, Identifier id) {
        return value + "." + id.getNamespace() + "." + id.getPath();
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

    public static Aspect fromJson(JsonObject obj, Identifier id, String key) {
        AspectBuilder builder = new AspectBuilder()
                .tone(EnumUtils.getEnumIgnoreCase(Tone.class, JsonHelper.getString(obj, "tone")))
                .color(ClampedColor.colorFromJson(obj.get("color")))
                .catalyst(Ingredient.fromJson(obj.get("catalyst")))
                .index(JsonHelper.getInt(obj, "index", -1))
                .modifier(JsonHelper.getDouble(obj, "modifier", 1.0))
                .ignoreSlot(JsonHelper.getBoolean(obj, "ignore_slot", false))
                .whitelist(whitelistFromJson(obj));
        MutableText display = obj.has("display")
                ? Text.Serializer.fromJson(obj.get("display"))
                : Text.translatable(getIdKey(key, id));
        return builder.display(display)
                .buildAspect(id);
    }

    public static Aspect fromJson(JsonObject obj, Identifier id) {
        return fromJson(obj, id, KEY);
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

    public static Aspect read(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        Tone tone = buf.readEnumConstant(Tone.class);
        int color = buf.readInt();
        Ingredient catalyst = Ingredient.fromPacket(buf);
        int index = buf.readInt();
        double modifier = buf.readDouble();
        boolean ignoreSlot = buf.readBoolean();
        List<Identifier> whitelist = readWhitelist(buf);
        MutableText display = (MutableText) buf.readText();
        return new Aspect(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist, display);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(id);
        buf.writeEnumConstant(tone);
        buf.writeInt(color);
        catalyst.write(buf);
        buf.writeInt(index);
        buf.writeDouble(modifier);
        buf.writeBoolean(ignoreSlot);
        writeWhitelist(buf);
        buf.writeText(display);
    }
}
