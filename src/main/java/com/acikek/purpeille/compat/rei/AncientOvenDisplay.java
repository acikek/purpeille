package com.acikek.purpeille.compat.rei;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.recipe.oven.AncientOvenRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AncientOvenDisplay implements Display {

    public static final CategoryIdentifier<AncientOvenDisplay> IDENTIFIER = CategoryIdentifier.of(Purpeille.id("ancient_oven"));

    public List<EntryIngredient> input;
    public List<EntryIngredient> output;
    public int damage;
    public int cookTime;

    public AncientOvenDisplay(AncientOvenRecipe recipe) {
        input = Collections.singletonList(EntryIngredients.ofIngredient(recipe.input()));
        output = Collections.singletonList(EntryIngredients.ofItemStacks(Arrays.stream(recipe.result()).toList()));
        damage = recipe.damage();
        cookTime = recipe.cookTime();
    }

    public MutableText getDamageText() {
        return new TranslatableText("rei.purpeille.ancient_oven.damage", damage)
                .formatted(damage > 0 ? Formatting.RED : Formatting.GREEN);
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return output;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return IDENTIFIER;
    }
}
