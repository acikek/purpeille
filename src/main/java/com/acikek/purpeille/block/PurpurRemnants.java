package com.acikek.purpeille.block;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.world.gen.EndCityProximityPlacementModifier;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.Arrays;

public class PurpurRemnants {

    public static final AbstractBlock.Settings SETTINGS = FabricBlockSettings.create()
            .requiresTool()
            .sounds(BlockSoundGroup.NETHER_ORE)
            .strength(7.0f, 50.0f);

    public static void build(int size, int count, int distance, String name) {
        ConfiguredFeature<?, ?> configuredFeature = new ConfiguredFeature<>(
                Feature.ORE,
                new OreFeatureConfig(
                        new BlockMatchRuleTest(Blocks.END_STONE),
                        ModBlocks.PURPUR_REMNANTS.getDefaultState(),
                        size, 1.0f
                )
        );
        PlacedFeature placedFeature = new PlacedFeature(
                RegistryEntry.of(configuredFeature),
                Arrays.asList(
                        CountPlacementModifier.of(count),
                        SquarePlacementModifier.of(),
                        HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(64)),
                        new EndCityProximityPlacementModifier(distance)
                )
        );
        Identifier id = Purpeille.id(name);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, configuredFeature);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, id, placedFeature);
        BiomeModifications.addFeature(
                BiomeSelectors.foundInTheEnd(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(RegistryKeys.PLACED_FEATURE, id)
        );
    }

    public static void build() {
        build(4, 4, 3, "purpur_remnants_small");
        build(16, 1, 1, "purpur_remnants_large");
    }
}
