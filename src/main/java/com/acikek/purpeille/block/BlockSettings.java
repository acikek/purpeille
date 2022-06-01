package com.acikek.purpeille.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class BlockSettings {

    public static final AbstractBlock.Settings MONOLITHIC_PURPUR = QuiltBlockSettings.of(Material.STONE)
            .strength(5.0f)
            .sounds(BlockSoundGroup.BONE)
            .requiresTool();

    public static final AbstractBlock.Settings REMNANT_BRICKS = QuiltBlockSettings.of(Material.STONE)
            .strength(5.0f)
            .sounds(BlockSoundGroup.DEEPSLATE_BRICKS)
            .requiresTool();
}
