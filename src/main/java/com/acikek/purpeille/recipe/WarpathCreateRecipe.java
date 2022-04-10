package com.acikek.purpeille.recipe;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.item.ModItems;
import com.acikek.purpeille.tag.ModTags;
import com.acikek.purpeille.warpath.Type;
import com.acikek.purpeille.warpath.Warpath;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class WarpathCreateRecipe extends SpecialCraftingRecipe {

    public static SpecialRecipeSerializer<WarpathCreateRecipe> SERIALIZER;

    public WarpathCreateRecipe(Identifier id) {
        super(id);
    }

    public static ItemStack getBase(CraftingInventory inventory, boolean strict) {
        ItemStack base = null;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (base == null && stack.isIn(ModTags.WARPATH_BASE)) {
                if (!strict) {
                    return stack;
                }
                base = stack;
            }
            else if (strict && !stack.isEmpty()) {
                return null;
            }
        }
        return base;
    }

    public static Pair<Integer, Integer> getIndices(CraftingInventory inventory) {
        Pair<Integer, Integer> components = new Pair<>(-1, -1);
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (components.getLeft() == -1 && stack.isOf(ModItems.SMOLDERED_PURPEILLE_INGOT)) {
                components.setLeft(i);
            }
            else if (components.getRight() == -1 && stack.isOf(ModItems.PRESERVED_DUST)) {
                components.setRight(i);
            }
            else if (!stack.isEmpty() && !stack.isIn(ModTags.WARPATH_BASE)) {
                return null;
            }
        }
        return components;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        ItemStack base = getBase(inventory, false);
        if (base == null || Type.REVELATION.hasNbt(base)) {
            return false;
        }
        Pair<Integer, Integer> indices = getIndices(inventory);
        return indices != null && indices.getLeft() != -1;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack base = getBase(inventory, false);
        Pair<Integer, Integer> indices = getIndices(inventory);
        if (base == null || indices == null) {
            return null;
        }
        ItemStack stack = base.copy();
        Warpath.apply(stack, indices.getLeft(), indices.getRight());
        return stack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 9;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static void register() {
        SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, Purpeille.id("crafting_special_warpath_create"), new SpecialRecipeSerializer<>(WarpathCreateRecipe::new));
    }
}
