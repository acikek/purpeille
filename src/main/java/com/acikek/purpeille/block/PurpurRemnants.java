package com.acikek.purpeille.block;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.world.gen.EndCityProximityPlacementModifier;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.Holder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.CountPlacementModifier;
import net.minecraft.world.gen.decorator.HeightRangePlacementModifier;
import net.minecraft.world.gen.decorator.InSquarePlacementModifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.ConfiguredFeatureUtil;
import net.minecraft.world.gen.feature.util.PlacedFeatureUtil;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

import java.util.Arrays;

public class PurpurRemnants {

    public static final AbstractBlock.Settings SETTINGS = QuiltBlockSettings.of(Material.STONE)
            .requiresTool()
            .sounds(BlockSoundGroup.NETHER_ORE)
            .strength(7.0f, 50.0f);

    public static void build(int size, int count, int distance, String name) {
		Identifier id = Purpeille.id(name);
        Holder<ConfiguredFeature<OreFeatureConfig, ?>> configuredFeature = ConfiguredFeatureUtil.register(
				id.toString(),
                Feature.ORE,
                new OreFeatureConfig(
                        new BlockMatchRuleTest(Blocks.END_STONE),
                        ModBlocks.PURPUR_REMNANTS.getDefaultState(),
                        size, 1.0f
                )
        );
       	PlacedFeatureUtil.register(
				id.toString(),
                configuredFeature,
                Arrays.asList(
                        CountPlacementModifier.create(count),
                        InSquarePlacementModifier.getInstance(),
                        HeightRangePlacementModifier.createUniform(YOffset.getBottom(), YOffset.fixed(64)),
                        new EndCityProximityPlacementModifier(distance)
                )
        );
        BiomeModifications.addFeature(
                BiomeSelectors.foundInTheEnd(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, id)
        );
    }

    public static void build() {
        build(4, 4, 3, "purpur_remnants_small");
        build(16, 1, 1, "purpur_remnants_large");
    }
}
