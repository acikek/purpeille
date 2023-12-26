package com.acikek.purpeille.recipe.oven;

import com.acikek.purpeille.Purpeille;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public record AncientOvenRecipe(Ingredient input, int damage, int cookTime,
                                ItemStack[] result, Identifier id) implements Recipe<SimpleInventory> {

    public static final Identifier ID = Purpeille.id("ancient_oven");

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        return inventory.size() >= 1 && input.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager manager) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    public ItemStack getOutput(Random random) {
        return result[random.nextInt(result.length)];
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager manager) {
        return getOutput(Random.create());
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AncientOvenRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<AncientOvenRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static void register() {
        Registry.register(Registries.RECIPE_TYPE, ID, Type.INSTANCE);
        Registry.register(Registries.RECIPE_SERIALIZER, ID, AncientOvenRecipeSerializer.INSTANCE);
    }
}
