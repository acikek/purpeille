package com.acikek.purpeille.compat.emi.warpath;

import com.acikek.purpeille.warpath.Warpath;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;
import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EmiWarpathCreateRecipe extends EmiPatternCraftingRecipe {

    public static EmiIngredient getCatalystIngredient(List<Item> allItems) {
        return EmiIngredient.of(allItems.stream().map(EmiStack::of).collect(Collectors.toList()));
    }

    public Item base;
    public Map<CatalystInstance<Revelation>, List<CatalystInstance<Aspect>>> compatibleCatalysts;

    public EmiWarpathCreateRecipe(Item base, Map<CatalystInstance<Revelation>, List<CatalystInstance<Aspect>>> compatibleCatalysts, List<Item> allItems, Identifier id) {
        super(List.of(EmiStack.of(base), getCatalystIngredient(allItems)), EmiStack.of(base), id);
        this.base = base;
        this.compatibleCatalysts = compatibleCatalysts;
    }

    public static Pair<CatalystInstance<Revelation>, CatalystInstance<Aspect>> getCatalysts(Map<CatalystInstance<Revelation>, List<CatalystInstance<Aspect>>> compatibleCatalysts, Random random) {
        var list = compatibleCatalysts.entrySet().stream().toList();
        var entry = list.get(random.nextInt(list.size()));
        CatalystInstance<Aspect> aspectInstance = !entry.getValue().isEmpty()
                ? entry.getValue().get(random.nextInt(entry.getValue().size()))
                : null;
        return new Pair<>(entry.getKey(), aspectInstance);
    }

    public static EmiIngredient getOutput(Map<CatalystInstance<Revelation>, List<CatalystInstance<Aspect>>> compatibleCatalysts, Item base, Random random) {
        var catalysts = getCatalysts(compatibleCatalysts, random);
        ItemStack stack = base.getDefaultStack();
        Warpath.add(stack, catalysts.getLeft().component(), catalysts.getRight() != null
                ? catalysts.getRight().component()
                : null);
        return EmiStack.of(stack);
    }

    public static <T extends Aspect> void appendComponent(List<Integer> slots, List<EmiIngredient> result, CatalystInstance<T> instance, Random random) {
        int index = instance.component().ignoreSlot
                ? slots.get(random.nextInt(slots.size()))
                : instance.component().getIndex();
        if (!slots.contains(index)) {
            return;
        }
        slots.remove(Integer.valueOf(index));
        result.set(index, EmiStack.of(instance.catalyst().getDefaultStack()));
    }

    public List<EmiIngredient> getInputs(Random random) {
        List<Integer> slots = new ArrayList<>(IntStream.range(0, 9).boxed().toList());
        List<EmiIngredient> result = new ArrayList<>(Collections.nCopies(9, EmiStack.EMPTY));
        var catalysts = getCatalysts(compatibleCatalysts, random);
        appendComponent(slots, result, catalysts.getLeft(), random);
        if (catalysts.getRight() != null) {
            appendComponent(slots, result, catalysts.getRight(), random);
        }
        int baseIndex = slots.contains(4) ? 4 : slots.get(random.nextInt(slots.size()));
        result.set(baseIndex, EmiStack.of(base.getDefaultStack()));
        return result;
    }

    @Override
    public SlotWidget getInputWidget(int slot, int x, int y) {
        return new GeneratedSlotWidget(random -> getInputs(random).get(slot), unique, x, y);
    }

    @Override
    public SlotWidget getOutputWidget(int x, int y) {
        return new GeneratedSlotWidget(random -> getOutput(compatibleCatalysts, base, random), unique, x, y);
    }
}
