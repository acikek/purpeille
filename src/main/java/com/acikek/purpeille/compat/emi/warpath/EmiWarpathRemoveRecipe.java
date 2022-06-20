package com.acikek.purpeille.compat.emi.warpath;

import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;
import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class EmiWarpathRemoveRecipe extends EmiPatternCraftingRecipe {

    public Item base;
    public Map<CatalystInstance<Revelation>, List<CatalystInstance<Aspect>>> compatibleCatalysts;

    public EmiWarpathRemoveRecipe(Item base, Map<CatalystInstance<Revelation>, List<CatalystInstance<Aspect>>> compatibleCatalysts, Identifier id) {
        super(List.of(EmiStack.of(base)), EmiStack.of(base), id);
        this.base = base;
        this.compatibleCatalysts = compatibleCatalysts;
    }

    @Override
    public SlotWidget getInputWidget(int slot, int x, int y) {
        return new GeneratedSlotWidget(random -> {
            if (slot == 4) {
                return EmiWarpathCreateRecipe.getOutput(compatibleCatalysts, base, random);
            }
            return EmiStack.EMPTY;
        }, unique, x, y);
    }

    @Override
    public SlotWidget getOutputWidget(int x, int y) {
        return new SlotWidget(EmiStack.of(base), x, y);
    }
}
