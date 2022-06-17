package com.acikek.purpeille.block;

import com.acikek.purpeille.sound.ModSoundGroups;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class BlockSettings {

    public static FabricBlockSettings baseSettings(Material material) {
        return FabricBlockSettings.of(material).requiresTool();
    }

    public static final AbstractBlock.Settings REMNANT_BRICKS = baseSettings(Material.STONE)
            .strength(5.0f)
            .sounds(BlockSoundGroup.DEEPSLATE_BRICKS);

    public static final AbstractBlock.Settings ANCIENT_MACHINE = baseSettings(Material.STONE)
            .strength(6.0f)
            .sounds(BlockSoundGroup.NETHER_BRICKS);

    public static final AbstractBlock.Settings MONOLITHIC_PURPUR = baseSettings(Material.STONE)
            .strength(5.0f)
            .sounds(BlockSoundGroup.BONE);

    public static final AbstractBlock.Settings LIGHTNING_BLOCK = baseSettings(Material.METAL)
            .strength(4.0f, 6.0f)
            .sounds(ModSoundGroups.LIGHTNING_BLOCK);
    public static final AbstractBlock.Settings INFESTED_BLOCK = FabricBlockSettings.copyOf(ANCIENT_MACHINE)
            .sounds(ModSoundGroups.INFESTED_BLOCK);
}
