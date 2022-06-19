package com.acikek.purpeille.compat.rei;

import com.acikek.purpeille.recipe.oven.AncientOvenRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;

public class PurpeilleREIPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new AncientOvenCategory());
        registry.addWorkstations(AncientOvenDisplay.IDENTIFIER, AncientOvenCategory.ICON);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerFiller(AncientOvenRecipe.class, AncientOvenDisplay::new);
    }
}
