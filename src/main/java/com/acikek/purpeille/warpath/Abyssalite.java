package com.acikek.purpeille.warpath;

import com.acikek.purpeille.api.AbyssalToken;
import com.acikek.purpeille.warpath.component.Revelation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public record Abyssalite(Item token, Map<TagKey<Item>, Float> modifiers) {

    public int getPositiveValue(int energy) {
        int s = energy - 65;
        int denom = energy < 65 ? 64 : 30;
        return - MathHelper.ceil((float) s * s / denom + 66.0f);
    }

    public int getNegativeValue(int energy) {
        float result;
        if (energy < 60) {
            result = 1.0f / 7.35f * energy;
        }
        else if (energy <= 81) {
            float s = MathHelper.sqrt(energy - 50);
            result = - MathHelper.sqrt(1000.0f - (s * s * s * s)) + 38.164f;
        }
        else {
            float s = energy - 105.0f;
            result = - s * s / 13.17f + 75.5f;
        }
        return MathHelper.ceil(result);
    }

    public void energize(ItemStack stack, UnaryOperator<Integer> fn) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains(AbyssalToken.ENERGY_KEY)) {
            nbt.putInt("CustomModelData", 1);
        }
        int result = fn.apply(nbt.contains(AbyssalToken.ENERGY_KEY) ? nbt.getInt(AbyssalToken.ENERGY_KEY) : 0);
        nbt.putInt(AbyssalToken.ENERGY_KEY, result);
    }

    public void load(Revelation revelation) {
        AbyssalToken abyssalToken = (AbyssalToken) token;
        abyssalToken.setRevelation(revelation);
        AbyssalToken.TOKENS.add(abyssalToken);
    }

    public static Map<TagKey<Item>, Float> modifiersFromJson(JsonObject obj) {
        Map<TagKey<Item>, Float> result = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            TagKey<Item> itemTag = TagKey.of(Registry.ITEM_KEY, Identifier.tryParse(entry.getKey()));
            result.put(itemTag, entry.getValue().getAsFloat());
        }
        return result;
    }

    public static Abyssalite fromJson(JsonObject obj) {
        if (obj == null) {
            return null;
        }
        Item token = JsonHelper.getItem(obj, "token");
        Map<TagKey<Item>, Float> modifiers = modifiersFromJson(JsonHelper.getObject(obj, "modifiers"));
        return new Abyssalite(token, modifiers);
    }

    public static Abyssalite read(PacketByteBuf buf) {
        Item token = buf.readRegistryValue(Registry.ITEM);
        Map<TagKey<Item>, Float> modifiers = buf.readMap(byteBuf -> TagKey.of(Registry.ITEM_KEY, byteBuf.readIdentifier()), PacketByteBuf::readFloat);
        return new Abyssalite(token, modifiers);
    }

    public void write(PacketByteBuf buf) {
        buf.writeRegistryValue(Registry.ITEM, token);
        buf.writeMap(modifiers, (byteBuf, tag) -> byteBuf.writeIdentifier(tag.id()), PacketByteBuf::writeFloat);
    }
}
