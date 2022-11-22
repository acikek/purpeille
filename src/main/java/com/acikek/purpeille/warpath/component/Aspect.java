package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.warpath.ClampedColor;
import com.acikek.purpeille.warpath.Tone;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.List;
import java.util.Objects;

public class Aspect extends Component {

    Aspect(Identifier id, Tone tone, int color, Ingredient catalyst, int index, double modifier, boolean ignoreSlot, List<Identifier> whitelist) {
        super(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist);
    }

    public Type getType() {
        return Type.ASPECT;
    }

    public static Aspect fromNbt(NbtCompound nbt) {
        return Type.ASPECT.getFromNbt(nbt, ASPECTS);
    }

    public static class Builder {

        Tone tone;
        int color;
        Ingredient catalyst;
        int index;
        double modifier = 1.0;
        boolean ignoreSlot = false;
        List<Identifier> whitelist;

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

        boolean isValid(Identifier id) {
            Objects.requireNonNull(id);
            Objects.requireNonNull(tone);
            Objects.requireNonNull(catalyst);
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
                return new Aspect(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist);
            }
            return null;
        }
    }

    public static Aspect fromJson(JsonObject obj, Identifier id) {
        return new Builder()
                .tone(enumFromJson(obj.get("tone"), Tone::valueOf, "tone"))
                .color(ClampedColor.colorFromJson(obj.get("color")))
                .catalyst(Ingredient.fromJson(obj.get("catalyst")))
                .index(JsonHelper.getInt(obj, "index", -1))
                .modifier(JsonHelper.getDouble(obj, "modifier", 1.0))
                .ignoreSlot(JsonHelper.getBoolean(obj, "ignore_slot", false))
                .whitelist(whitelistFromJson(obj))
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
        return new Aspect(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist);
    }
}
