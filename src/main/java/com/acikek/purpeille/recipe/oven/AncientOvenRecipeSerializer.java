package com.acikek.purpeille.recipe.oven;

import com.google.gson.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;

public class AncientOvenRecipeSerializer implements RecipeSerializer<AncientOvenRecipe> {

    public static class JsonFormat {
        JsonObject input;
        int damage;
        int cook_time;
        JsonArray result;
    }

    public static final AncientOvenRecipeSerializer INSTANCE = new AncientOvenRecipeSerializer();

    @Override
    public AncientOvenRecipe read(Identifier id, JsonObject json) {
        JsonFormat recipe = new Gson().fromJson(json, JsonFormat.class);
        if (recipe.input == null) {
            throw new JsonSyntaxException("Missing field 'input'");
        }
        if (recipe.result == null) {
            throw new JsonSyntaxException("Missing field 'result'");
        }
        Ingredient input = Ingredient.fromJson(recipe.input);
        ItemStack[] result = new ItemStack[recipe.result.size()];
        for (int i = 0; i < result.length; i++) {
            JsonElement stack = recipe.result.get(i);
            if (!stack.isJsonObject()) {
                throw new JsonSyntaxException("'result' entry must be an object");
            }
            result[i] = ShapedRecipe.outputFromJson(stack.getAsJsonObject());
        }
        return new AncientOvenRecipe(input, recipe.damage, recipe.cook_time, result, id);
    }

    @Override
    public AncientOvenRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient input = Ingredient.fromPacket(buf);
        int damage = buf.readInt();
        int cookTime = buf.readInt();
        int size = buf.readInt();
        ItemStack[] result = new ItemStack[size];
        for (int i = 0; i < result.length; i++) {
            result[i] = buf.readItemStack();
        }
        return new AncientOvenRecipe(input, damage, cookTime, result, id);
    }

    @Override
    public void write(PacketByteBuf buf, AncientOvenRecipe recipe) {
        recipe.input().write(buf);
        buf.writeInt(recipe.damage());
        buf.writeInt(recipe.cookTime());
        buf.writeInt(recipe.result().length);
        for (ItemStack stack : recipe.result()) {
            buf.writeItemStack(stack);
        }
    }
}
