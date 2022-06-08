package com.acikek.purpeille.compat.emi;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.compat.emi.warpath.CatalystInstance;
import com.acikek.purpeille.compat.emi.warpath.EmiWarpathCreateRecipe;
import com.acikek.purpeille.compat.emi.warpath.EmiWarpathRemoveRecipe;
import com.acikek.purpeille.recipe.oven.AncientOvenRecipe;
import com.acikek.purpeille.tag.ModTags;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Component;
import com.acikek.purpeille.warpath.component.Revelation;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

import java.util.*;
import java.util.stream.Collectors;

public class PurpeillePlugin implements EmiPlugin {

    public static Identifier WIDGETS = Purpeille.id("textures/gui/emi/widgets.png");

    public static final Block[] ANCIENT_OVENS = {
        ModBlocks.ANCIENT_OVEN,
        ModBlocks.ANCIENT_OVEN_DIM,
        ModBlocks.ANCIENT_OVEN_VERY_DIM
    };

    public static Optional<List<Item>> getTagItems(TagKey<Item> tag) {
        return Registry.ITEM.getEntryList(tag)
            .map(holders -> holders.stream().map(RegistryEntry::value).collect(Collectors.toList()));
    }

    public static Map<CatalystInstance<Revelation>, List<CatalystInstance<Aspect>>> getCompatibleCatalysts(List<CatalystInstance<Revelation>> revelationInstances, List<CatalystInstance<Aspect>> aspectInstances) {
        Map<CatalystInstance<Revelation>, List<CatalystInstance<Aspect>>> result = new HashMap<>();
        for (CatalystInstance<Revelation> revelationInstance : revelationInstances) {
            List<CatalystInstance<Aspect>> compatibleAspects = aspectInstances.stream()
                .filter(instance -> Component.areCompatible(revelationInstance.component(), instance.component()))
                .filter(instance -> revelationInstance.component().getFixedIndex() != instance.component().getFixedIndex())
                .collect(Collectors.toList());
            result.put(revelationInstance, compatibleAspects);
        }
        return result;
    }

    public static List<Item> getAllItems(List<CatalystInstance<Revelation>> revelationInstances, List<CatalystInstance<Aspect>> aspectInstances) {
        List<Item> result = new ArrayList<>();
        CatalystInstance.addItems(revelationInstances, result);
        CatalystInstance.addItems(aspectInstances, result);
        return result;
    }

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(EmiAncientOvenRecipe.CATEGORY);
        for (Block oven : ANCIENT_OVENS) {
            registry.addWorkstation(EmiAncientOvenRecipe.CATEGORY, EmiStack.of(oven.asItem().getDefaultStack()));
        }
        List<Item> aspectCatalysts = getTagItems(ModTags.ASPECT_CATALYST).orElse(Collections.emptyList());
        getTagItems(ModTags.WARPATH_BASE).ifPresent(warpathBases -> getTagItems(ModTags.REVELATION_CATALYST).ifPresent(revelationCatalysts -> {
            List<CatalystInstance<Aspect>> aspectInstances = CatalystInstance.getInstances(aspectCatalysts, Component.ASPECTS);
            List<CatalystInstance<Revelation>> revelationInstances = CatalystInstance.getInstances(revelationCatalysts, Component.REVELATIONS);
            Map<CatalystInstance<Revelation>, List<CatalystInstance<Aspect>>> compatibleCatalysts = getCompatibleCatalysts(revelationInstances, aspectInstances);
            List<Item> allItems = getAllItems(revelationInstances, aspectInstances);
            for (Item base : warpathBases) {
                registry.addRecipe(new EmiWarpathCreateRecipe(base, compatibleCatalysts, allItems, null));
                registry.addRecipe(new EmiWarpathRemoveRecipe(base, compatibleCatalysts, null));
            }
        }));
        for (AncientOvenRecipe recipe : registry.getRecipeManager().listAllOfType(AncientOvenRecipe.Type.INSTANCE)) {
            registry.addRecipe(new EmiAncientOvenRecipe(recipe));
        }
    }
}
