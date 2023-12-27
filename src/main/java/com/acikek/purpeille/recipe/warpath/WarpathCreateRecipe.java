package com.acikek.purpeille.recipe.warpath;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.tag.ModTags;
import com.acikek.purpeille.warpath.Warpath;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Component;
import com.acikek.purpeille.warpath.component.Revelation;
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

import java.util.Map;

public class WarpathCreateRecipe extends SpecialCraftingRecipe {

    public static SpecialRecipeSerializer<WarpathCreateRecipe> SERIALIZER;

    public WarpathCreateRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
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
                if ((component.ignoreSlot || component.getFixedIndex() == index) && component.catalyst.test(stack)) {
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

        public boolean isValid() {
            Revelation revelationComponent = revelation.getComponent(Component.REVELATIONS);
            if (revelationComponent == null) {
                return false;
            }
            Aspect aspectComponent = aspect.getComponent(Component.ASPECTS);
            if (!aspect.isEmpty() && aspectComponent == null) {
                return false;
            }
            return Component.areCompatible(revelationComponent, aspectComponent);
        }

        public static ComponentPair getEmpty() {
            return new ComponentPair(ComponentData.getEmpty(), ComponentData.getEmpty());
        }
    }

    public static ItemStack getBase(RecipeInputInventory inventory) {
        ItemStack base = null;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (base == null && stack.isIn(ModTags.WARPATH_BASE)) {
                base = stack;
            }
            else if (!stack.isEmpty() && !stack.isIn(ModTags.REVELATION_CATALYST) && !stack.isIn(ModTags.ASPECT_CATALYST)) {
                return null;
            }
        }
        return base;
    }

    public static ComponentPair getRecipeData(RecipeInputInventory inventory) {
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
    public boolean matches(RecipeInputInventory inventory, World world) {
        ItemStack base = getBase(inventory);
        if (base == null || Warpath.getData(base) != null) {
            return false;
        }
        ComponentPair data = getRecipeData(inventory);
        if (data == null || data.revelation.isEmpty()) {
            return false;
        }
        return data.isValid();
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager manager) {
        ItemStack base = getBase(inventory);
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
        SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Purpeille.id("crafting_special_warpath_create"), new SpecialRecipeSerializer<>(WarpathCreateRecipe::new));
    }
}
