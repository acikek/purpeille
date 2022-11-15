package com.acikek.purpeille;

import com.acikek.purpeille.advancement.ModCriteria;
import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.block.ChorusInfestedBlocks;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.PurpurRemnants;
import com.acikek.purpeille.block.entity.ModBlockEntities;
import com.acikek.purpeille.command.AncientMessageCommand;
import com.acikek.purpeille.command.VacuousBlastCommand;
import com.acikek.purpeille.command.WarpathCommand;
import com.acikek.purpeille.effect.ModStatusEffects;
import com.acikek.purpeille.impl.AbyssalAllegianceImpl;
import com.acikek.purpeille.impl.AncientMessagesImpl;
import com.acikek.purpeille.item.ModItems;
import com.acikek.purpeille.loot.RandomRevelationLootFunction;
import com.acikek.purpeille.recipe.oven.AncientOvenRecipe;
import com.acikek.purpeille.recipe.warpath.WarpathCreateRecipe;
import com.acikek.purpeille.recipe.warpath.WarpathRemoveRecipe;
import com.acikek.purpeille.sound.ModSoundEvents;
import com.acikek.purpeille.world.gen.EndCityProximityPlacementModifier;
import com.acikek.purpeille.world.reload.ReloadHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
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
        if (FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
            LOGGER.warn("Purpeille support for REI is limited; use EMI for better integration! https://modrinth.com/mod/emi");
        }
        ModBlocks.register(ModBlocks.BLOCKS);
        ModBlockEntities.register();
        ModItems.register();
        ModBlocks.register(ModBlocks.DECORATIONS);
        ModAttributes.register();
        ModStatusEffects.register();
        ModCriteria.register();
        ModSoundEvents.register();
        WarpathCreateRecipe.register();
        WarpathRemoveRecipe.register();
        AncientOvenRecipe.register();
        EndCityProximityPlacementModifier.register();
        PurpurRemnants.build();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            VacuousBlastCommand.register(dispatcher);
            WarpathCommand.register(dispatcher);
            AncientMessageCommand.register(dispatcher);
        });
        UseBlockCallback.EVENT.register(new ChorusInfestedBlocks());
        RandomRevelationLootFunction.register();
        ReloadHandler.register();
        AbyssalAllegianceImpl.register();
        AncientMessagesImpl.register();
        FabricLoader.getInstance().getModContainer(ID).ifPresent(mod ->
                ResourceManagerHelper.registerBuiltinResourcePack(
                        id("default"), mod, "Default Warpaths",
                        ResourcePackActivationType.DEFAULT_ENABLED
                )
        );
    }
}
