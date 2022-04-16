package com.acikek.purpeille.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class BlockSettings {

    public static AbstractBlock.Settings MONOLITHIC_PURPUR = FabricBlockSettings.of(Material.STONE)
            .strength(5.0f)
            .sounds(BlockSoundGroup.BONE)
            .requiresTool();

    public static AbstractBlock.Settings REMNANT_BRICKS = FabricBlockSettings.of(Material.STONE)
            .strength(5.0f)
            .sounds(BlockSoundGroup.DEEPSLATE_BRICKS)
            .requiresTool();

    public static AbstractBlock.Settings PURPEILLE_BLOCK = FabricBlockSettings.of(Material.METAL)
            .strength(8.0f)
            .sounds(BlockSoundGroup.METAL)
            .requiresTool();

    public static AbstractBlock.Settings ANCIENT_MECHANICAL_BRICKS = FabricBlockSettings.of(Material.STONE)
            .strength(6.0f)
            .requiresTool()
            .sounds(BlockSoundGroup.NETHER_BRICKS);
}
