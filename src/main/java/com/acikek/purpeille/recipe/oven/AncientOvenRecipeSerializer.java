package com.acikek.purpeille.recipe.oven;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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
        JsonObject result;
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
        ItemStack result = ShapedRecipe.outputFromJson(recipe.result);
        return new AncientOvenRecipe(input, recipe.damage, recipe.cook_time, result, id);
    }

    @Override
    public AncientOvenRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient input = Ingredient.fromPacket(buf);
        int damage = buf.readInt();
        int cookTime = buf.readInt();
        ItemStack result = buf.readItemStack();
        return new AncientOvenRecipe(input, damage, cookTime, result, id);
    }

    @Override
    public void write(PacketByteBuf buf, AncientOvenRecipe recipe) {
        recipe.input().write(buf);
        buf.writeInt(recipe.damage());
        buf.writeInt(recipe.cookTime());
        buf.writeItemStack(recipe.result());
    }
}
