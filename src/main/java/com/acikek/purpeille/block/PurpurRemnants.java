package com.acikek.purpeille.block;

import com.acikek.purpeille.Purpeille;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;

public class PurpurRemnants {

    public static final AbstractBlock.Settings SETTINGS = FabricBlockSettings.create()
            .requiresTool()
            .sounds(BlockSoundGroup.NETHER_ORE)
            .strength(7.0f, 50.0f);

    public static void register(String name) {
        Identifier id = Purpeille.id(name);
        BiomeModifications.addFeature(
                BiomeSelectors.foundInTheEnd(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(RegistryKeys.PLACED_FEATURE, id)
        );
    }

    public static void register() {
        register("purpur_remnants_small");
        register("purpur_remnants_large");
    }
}
