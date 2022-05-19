package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.warpath.ClampedColor;
import com.acikek.purpeille.warpath.Tone;
import com.acikek.purpeille.warpath.Type;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Aspect extends Component {

    public Aspect(Identifier id, Tone tone, int color, Item catalyst, int index, double modifier, boolean ignoreSlot) {
        super(id, tone, color, catalyst, index, modifier, ignoreSlot);
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
        Item catalyst = ShapedRecipe.getItem(obj.getAsJsonObject("catalyst"));
        int index = obj.get("index").getAsInt();
        double modifier = obj.get("modifier").getAsDouble();
        boolean ignoreSlot = obj.has("ignore_slot") && obj.get("ignore_slot").getAsBoolean();
        return new Aspect(id, tone, color, catalyst, index, modifier, ignoreSlot);
    }

    public static Aspect read(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        Tone tone = buf.readEnumConstant(Tone.class);
        int color = buf.readInt();
        Item catalyst = Registry.ITEM.get(buf.readIdentifier());
        int index = buf.readInt();
        double modifier = buf.readDouble();
        boolean ignoreSlot = buf.readBoolean();
        return new Aspect(id, tone, color, catalyst, index, modifier, ignoreSlot);
    }
}
