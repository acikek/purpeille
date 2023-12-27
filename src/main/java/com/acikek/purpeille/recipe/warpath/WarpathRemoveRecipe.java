package com.acikek.purpeille.recipe.warpath;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.warpath.Warpath;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class WarpathRemoveRecipe extends SpecialCraftingRecipe {

    public static SpecialRecipeSerializer<WarpathRemoveRecipe> SERIALIZER;

    public WarpathRemoveRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        ItemStack base = WarpathCreateRecipe.getBase(inventory);
        return base != null && Warpath.getData(base) != null;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager manager) {
        ItemStack base = WarpathCreateRecipe.getBase(inventory);
        if (base == null) {
            return null;
        }
        ItemStack stack = base.copy();
        Warpath.remove(stack);
        return stack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static void register() {
        SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Purpeille.id("crafting_special_warpath_remove"), new SpecialRecipeSerializer<>(WarpathRemoveRecipe::new));
    }
}
