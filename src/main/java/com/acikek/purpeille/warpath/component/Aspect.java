package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.warpath.ClampedColor;
import com.acikek.purpeille.warpath.Tone;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;
import java.util.Objects;

public class Aspect extends Component {

    Aspect(Identifier id, Tone tone, int color, Ingredient catalyst, int index, double modifier, boolean ignoreSlot, List<Identifier> whitelist, MutableText display) {
        super(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist, display);
    }

    public static class Builder {

        Tone tone;
        int color;
        Ingredient catalyst;
        int index;
        double modifier = 1.0;
        boolean ignoreSlot = false;
        List<Identifier> whitelist;
        MutableText display;

        public Builder tone(Tone tone) {
            this.tone = tone;
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder color(String name) {
            return color(ClampedColor.colorByName(name));
        }

        public Builder catalyst(Ingredient catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        public Builder index(int index) {
            this.index = index;
            return this;
        }

        public Builder modifier(double modifier) {
            this.modifier = modifier;
            return this;
        }

        public Builder ignoreSlot(boolean ignoreSlot) {
            this.ignoreSlot = ignoreSlot;
            return this;
        }

        public Builder whitelist(List<Identifier> whitelist) {
            this.whitelist = whitelist;
            return this;
        }

        public Builder display(MutableText text) {
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

    public static Aspect fromJson(JsonObject obj, Identifier id, Type type) {
        Builder builder = new Builder()
                .tone(EnumUtils.getEnumIgnoreCase(Tone.class, JsonHelper.getString(obj, "tone")))
                .color(ClampedColor.colorFromJson(obj.get("color")))
                .catalyst(Ingredient.fromJson(obj.get("catalyst")))
                .index(JsonHelper.getInt(obj, "index", -1))
                .modifier(JsonHelper.getDouble(obj, "modifier", 1.0))
                .ignoreSlot(JsonHelper.getBoolean(obj, "ignore_slot", false))
                .whitelist(whitelistFromJson(obj));
        MutableText display = obj.has("display")
                ? Text.Serializer.fromJson(obj.get("display"))
                : Text.translatable(getIdKey(type.translationKey, id));
        return builder.display(display)
                .buildAspect(id);
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
}
