package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.warpath.ClampedColor;
import com.acikek.purpeille.warpath.Tone;
import com.acikek.purpeille.warpath.Type;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.List;

public class Aspect extends Component {

    public Aspect(Identifier id, Tone tone, int color, Ingredient catalyst, int index, double modifier, boolean ignoreSlot, List<Identifier> whitelist) {
        super(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist);
    }

    public Type getType() {
        return Type.ASPECT;
    }

    public static Aspect fromNbt(NbtCompound nbt) {
        return Type.ASPECT.getFromNbt(nbt, ASPECTS);
    }

    public static Aspect fromJson(JsonObject obj, Identifier id) {
        Tone tone = enumFromJson(obj.get("tone"), Tone::valueOf, "tone");
        int color = ClampedColor.colorFromJson(obj.get("color"));
        Ingredient catalyst = Ingredient.fromJson(obj.get("catalyst"));
        int index = JsonHelper.getInt(obj, "index", -1);
        double modifier = JsonHelper.getDouble(obj, "modifier", 1.0);
        boolean ignoreSlot = JsonHelper.getBoolean(obj, "ignore_slot", false);
        List<Identifier> whitelist = whitelistFromJson(obj);
        return new Aspect(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist);
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
