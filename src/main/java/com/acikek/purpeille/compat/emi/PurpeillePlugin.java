package com.acikek.purpeille.compat.emi;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.api.warpath.Components;
import com.acikek.purpeille.block.ChorusInfestedBlocks;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.compat.emi.warpath.CatalystInstance;
import com.acikek.purpeille.compat.emi.warpath.EmiWarpathCreateRecipe;
import com.acikek.purpeille.compat.emi.warpath.EmiWarpathRemoveRecipe;
import com.acikek.purpeille.recipe.oven.AncientOvenRecipe;
import com.acikek.purpeille.tag.ModTags;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.block.Block;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
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
                    .filter(instance -> Components.areCompatible(revelationInstance.component(), instance.component()))
                    .filter(instance -> revelationInstance.component().getIndex() != instance.component().getIndex())
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

    public static Identifier getWarpathRecipeId(String method, Item base) {
        return new Identifier("emi", "purpeille/warpath_" + method + "/" + Registry.ITEM.getId(base).getPath());
    }

    public static Identifier getInfestedInteractionId(String method, Block block) {
        return new Identifier("emi", "purpeille/" + method + "/" + Registry.BLOCK.getId(block).getPath());
    }

    public static TooltipComponent getChanceTooltip(double chance) {
        return TooltipComponent.of(Text.translatable("emi.purpeille.chance", chance).asOrderedText());
    }

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(EmiAncientOvenRecipe.CATEGORY);
        for (Block oven : ANCIENT_OVENS) {
            registry.addWorkstation(EmiAncientOvenRecipe.CATEGORY, EmiStack.of(oven.asItem().getDefaultStack()));
        }
        Revelation.finishAttributeReload(false);
        List<Item> aspectCatalysts = getTagItems(ModTags.ASPECT_CATALYST).orElse(Collections.emptyList());
        getTagItems(ModTags.WARPATH_BASE).ifPresent(warpathBases -> getTagItems(ModTags.REVELATION_CATALYST).ifPresent(revelationCatalysts -> {
            List<CatalystInstance<Aspect>> aspectInstances = CatalystInstance.getInstances(aspectCatalysts, Components.getAspects());
            List<CatalystInstance<Revelation>> revelationInstances = CatalystInstance.getInstances(revelationCatalysts, Components.getRevelations());
            Map<CatalystInstance<Revelation>, List<CatalystInstance<Aspect>>> compatibleCatalysts = getCompatibleCatalysts(revelationInstances, aspectInstances);
            List<Item> allItems = getAllItems(revelationInstances, aspectInstances);
            for (Item base : warpathBases) {
                registry.addRecipe(new EmiWarpathCreateRecipe(base, compatibleCatalysts, allItems, getWarpathRecipeId("create", base)));
                registry.addRecipe(new EmiWarpathRemoveRecipe(base, compatibleCatalysts, getWarpathRecipeId("remove", base)));
            }
        }));
        for (AncientOvenRecipe recipe : registry.getRecipeManager().listAllOfType(AncientOvenRecipe.Type.INSTANCE)) {
            registry.addRecipe(new EmiAncientOvenRecipe(recipe));
        }
        List<EmiRecipe> growingRecipes = new ArrayList<>();
        List<EmiRecipe> shearingRecipes = new ArrayList<>();
        EmiStack boneMeal = EmiStack.of(Items.BONE_MEAL);
        EmiStack shears = EmiStack.of(Items.SHEARS);
        EmiStack chorusFruit = EmiStack.of(Items.CHORUS_FRUIT);
        for (Map.Entry<Block, Block> pair : ChorusInfestedBlocks.STAGES.entrySet()) {
            EmiStack normal = EmiStack.of(pair.getKey());
            EmiStack grown = EmiStack.of(pair.getValue());
            EmiRecipe growingRecipe = EmiWorldInteractionRecipe.builder()
                    .id(getInfestedInteractionId("growing", pair.getKey()))
                    .leftInput(normal)
                    .rightInput(boneMeal, false)
                    .output(grown)
                    .build();
            EmiWorldInteractionRecipe.Builder shearingRecipeBuilder = EmiWorldInteractionRecipe.builder()
                    .id(getInfestedInteractionId("shearing", pair.getValue()))
                    .leftInput(grown)
                    .rightInput(shears, true);
            if (ChorusInfestedBlocks.CHORAL_BLOOM.contains(pair.getValue())) {
                shearingRecipeBuilder = shearingRecipeBuilder
                        .output(chorusFruit, slot -> new SlotWidget(chorusFruit, slot.getBounds().x(), slot.getBounds().y())
                                .appendTooltip(() -> getChanceTooltip(50.0)));
            }
            EmiRecipe shearingRecipe = shearingRecipeBuilder.output(normal).build();
            growingRecipes.add(growingRecipe);
            shearingRecipes.add(shearingRecipe);
        }
        for (EmiRecipe growingRecipe : growingRecipes) {
            registry.addRecipe(growingRecipe);
        }
        for (EmiRecipe shearingRecipe : shearingRecipes) {
            registry.addRecipe(shearingRecipe);
        }
    }
}
