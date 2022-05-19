package com.acikek.purpeille.recipe.warpath;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.tag.ModTags;
import com.acikek.purpeille.warpath.Warpath;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Component;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Map;

public class WarpathCreateRecipe extends SpecialCraftingRecipe {

    public static SpecialRecipeSerializer<WarpathCreateRecipe> SERIALIZER;

    public WarpathCreateRecipe(Identifier id) {
        super(id);
    }

    public record ComponentData<T extends Component>(ItemStack stack, int index) {

        public boolean isEmpty() {
            return stack == ItemStack.EMPTY || index == -1;
        }

        public T getComponent(Map<Identifier, T> registry) {
            if (isEmpty()) {
                return null;
            }
            for (T component : registry.values()) {
                int compIndex = component.relativeIndex;
                if (component instanceof Aspect) {
                    compIndex = 8 - compIndex;
                }
                if ((component.ignoreSlot || compIndex == index) && stack.isOf(component.catalyst)) {
                    return component;
                }
            }
            return null;
        }

        public static <T extends Component> ComponentData<T> getEmpty() {
            return new ComponentData<>(ItemStack.EMPTY, -1);
        }
    }

    public static class ComponentPair {

        public ComponentData<Revelation> revelation;
        public ComponentData<Aspect> aspect;

        public ComponentPair(ComponentData<Revelation> revelation, ComponentData<Aspect> aspect) {
            this.revelation = revelation;
            this.aspect = aspect;
        }

        public static ComponentPair getEmpty() {
            return new ComponentPair(ComponentData.getEmpty(), ComponentData.getEmpty());
        }
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

    public static ComponentPair getRecipeData(CraftingInventory inventory) {
        ComponentPair components = ComponentPair.getEmpty();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (components.revelation.isEmpty() && stack.isIn(ModTags.REVELATION_CATALYST)) {
                components.revelation = new ComponentData<>(stack, i);
            }
            else if (components.aspect.isEmpty() && stack.isIn(ModTags.ASPECT_CATALYST)) {
                components.aspect = new ComponentData<>(stack, i);
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
        if (base == null || Warpath.getData(base) != null) {
            return false;
        }
        ComponentPair data = getRecipeData(inventory);
        if (data == null || data.revelation.isEmpty()) {
            return false;
        }
        boolean validAspect = data.aspect.isEmpty() || data.aspect.getComponent(Component.ASPECTS) != null;
        return data.revelation.getComponent(Component.REVELATIONS) != null && validAspect;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack base = getBase(inventory, false);
        ComponentPair data = getRecipeData(inventory);
        if (base == null || data == null) {
            return null;
        }
        ItemStack stack = base.copy();
        Revelation revelation = data.revelation.getComponent(Component.REVELATIONS);
        if (revelation == null) {
            return null;
        }
        Aspect aspect = data.aspect.getComponent(Component.ASPECTS);
        Warpath.add(stack, revelation, aspect);
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
