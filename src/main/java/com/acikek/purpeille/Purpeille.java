package com.acikek.purpeille;

import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.item.ModItems;
import com.acikek.purpeille.recipe.WarpathRecipe;
import com.acikek.purpeille.world.gen.EndCityProximityPlacementModifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Purpeille implements ModInitializer {

    public static final String ID = "purpeille";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(id("main"))
            .icon(() -> new ItemStack(ModItems.PURPEILLE_INGOT))
            .build();

    public static Identifier id(String key) {
        return new Identifier(ID, key);
    }

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModAttributes.register();
        WarpathRecipe.register();
        EndCityProximityPlacementModifier.register();
        PurpurRemnants.build();
    }
}
