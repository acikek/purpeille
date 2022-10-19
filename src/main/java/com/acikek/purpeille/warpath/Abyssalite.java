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
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Abyssalite {

    public enum Effect {

        WEAK,
        STANDARD,
        GREAT;

        public static Effect getEffect(float modifier, float min, float max) {
            float range = max - min;
            float offset = modifier - min;
            if (offset < range / 3.0f) {
                return WEAK;
            }
            else if (offset > range / 3.0f * 2.0f) {
                return GREAT;
            }
            return STANDARD;
        }
    }

    public Item token;
    public Map<TagKey<Item>, Pair<Float, Effect>> modifiers;

    public Abyssalite(Item token, Map<TagKey<Item>, Float> modifiers) {
        this.token = token;
        Map<TagKey<Item>, Pair<Float, Effect>> withEffect = new HashMap<>();
        Collection<Float> values = modifiers.values();
        float min = Collections.min(values);
        float max = Collections.max(values);
        for (Map.Entry<TagKey<Item>, Float> pair : modifiers.entrySet()) {
            Effect effect = Effect.getEffect(pair.getValue(), min, max);
            withEffect.put(pair.getKey(), new Pair<>(pair.getValue(), effect));
        }
        this.modifiers = withEffect;
    }

    public Pair<Float, Effect> getModifier(ItemStack stack) {
        for (Map.Entry<TagKey<Item>, Pair<Float, Effect>> entry : modifiers.entrySet()) {
            if (stack.isIn(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
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
        buf.writeMap(modifiers, (byteBuf, tag) -> byteBuf.writeIdentifier(tag.id()), (byteBuf, modifier) -> byteBuf.writeFloat(modifier.getLeft()));
    }
}
