package com.acikek.purpeille.warpath;

import com.acikek.purpeille.api.AbyssalToken;
import com.acikek.purpeille.warpath.component.Revelation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
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
    public Map<Ingredient, Pair<Float, Effect>> modifiers;
    public float max;

    public Abyssalite(Item token, Map<Ingredient, Float> modifiers, float max) {
        this.token = token;
        Map<Ingredient, Pair<Float, Effect>> withEffect = new HashMap<>();
        Collection<Float> values = modifiers.values();
        float minValue = Collections.min(values);
        float maxValue = Collections.max(values);
        for (Map.Entry<Ingredient, Float> pair : modifiers.entrySet()) {
            Effect effect = Effect.getEffect(pair.getValue(), minValue, maxValue);
            withEffect.put(pair.getKey(), new Pair<>(pair.getValue(), effect));
        }
        this.modifiers = withEffect;
        this.max = max;
    }

    public Pair<Float, Effect> getModifier(ItemStack stack) {
        for (Map.Entry<Ingredient, Pair<Float, Effect>> entry : modifiers.entrySet()) {
            if (entry.getKey().test(stack)) {
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

    public static Map<Ingredient, Float> modifiersFromJson(JsonArray array) {
        Map<Ingredient, Float> result = new HashMap<>();
        for (JsonElement element : array) {
            JsonObject obj = JsonHelper.asObject(element, "modifier entry");
            Ingredient ingredient = Ingredient.fromJson(obj.get("item"));
            float modifier = JsonHelper.getFloat(obj, "modifier");
            result.put(ingredient, modifier);
        }
        return result;
    }

    public static Abyssalite fromJson(JsonObject obj) {
        if (obj == null) {
            return null;
        }
        Item token = JsonHelper.getItem(obj, "token");
        Map<Ingredient, Float> modifiers = modifiersFromJson(JsonHelper.getArray(obj, "modifiers"));
        float max = JsonHelper.getFloat(obj, "max");
        return new Abyssalite(token, modifiers, max);
    }

    public static Abyssalite read(PacketByteBuf buf) {
        Item token = buf.readRegistryValue(Registry.ITEM);
        List<Pair<Ingredient, Float>> modifierList = buf.readList(byteBuf -> {
            Ingredient item = Ingredient.fromPacket(byteBuf);
            float modifier = byteBuf.readFloat();
            return new Pair<>(item, modifier);
        });
        Map<Ingredient, Float> modifiers = modifierList.stream()
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        float max = buf.readFloat();
        return new Abyssalite(token, modifiers, max);
    }

    public void write(PacketByteBuf buf) {
        buf.writeRegistryValue(Registry.ITEM, token);
        List<Pair<Ingredient, Float>> modifierList = modifiers.entrySet().stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue().getLeft()))
                .toList();
        buf.writeCollection(modifierList, (byteBuf, pair) -> {
            pair.getLeft().write(byteBuf);
            byteBuf.writeFloat(pair.getRight());
        });
        buf.writeFloat(max);
    }
}
