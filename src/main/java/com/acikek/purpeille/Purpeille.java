package com.acikek.purpeille;

import com.acikek.purpeille.advancement.ModCriteria;
import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.PurpurRemnants;
import com.acikek.purpeille.block.ancient.ModBlockEntities;
import com.acikek.purpeille.command.VacuousBlastCommand;
import com.acikek.purpeille.command.WarpathCommand;
import com.acikek.purpeille.item.ModItems;
import com.acikek.purpeille.recipe.oven.AncientOvenRecipe;
import com.acikek.purpeille.recipe.warpath.WarpathCreateRecipe;
import com.acikek.purpeille.recipe.warpath.WarpathRemoveRecipe;
import com.acikek.purpeille.sound.ModSoundEvents;
import com.acikek.purpeille.world.gen.EndCityProximityPlacementModifier;
import com.acikek.purpeille.world.reload.ReloadHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Purpeille implements ModInitializer {

    public static final String ID = "purpeille";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(id("main"))
            .icon(() -> new ItemStack(ModItems.PURPEILLE_INGOT))
            .build();

    public static Identifier id(String key) {
        return new Identifier(ID, key);
    }

    public static final Logger LOGGER = LogManager.getLogger(ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Purpeille: Harness the Void!");
        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        ModAttributes.register();
        ModCriteria.register();
        ModSoundEvents.register();
        WarpathCreateRecipe.register();
        WarpathRemoveRecipe.register();
        AncientOvenRecipe.register();
        EndCityProximityPlacementModifier.register();
        PurpurRemnants.build();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            VacuousBlastCommand.register(dispatcher);
            WarpathCommand.register(dispatcher);
        });
        ReloadHandler.register();
    }
}
