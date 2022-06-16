package com.acikek.purpeille.sound;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

public class ModSoundGroups {

    public static final BlockSoundGroup LIGHTNING_BLOCK = new BlockSoundGroup(
            1.0f, 1.0f,
            ModSoundEvents.LIGHTNING_BLOCK_BREAK,
            SoundEvents.BLOCK_METAL_STEP,
            ModSoundEvents.LIGHTNING_BLOCK_PLACE,
            SoundEvents.BLOCK_METAL_HIT,
            SoundEvents.BLOCK_METAL_FALL
    );

    public static final BlockSoundGroup INFESTED_BLOCK = new BlockSoundGroup(
            1.0f, 1.0f,
            ModSoundEvents.INFESTED_BLOCK_BREAK,
            SoundEvents.BLOCK_NETHER_BRICKS_STEP,
            ModSoundEvents.INFESTED_BLOCK_PLACE,
            SoundEvents.BLOCK_NETHER_BRICKS_HIT,
            SoundEvents.BLOCK_NETHER_BRICKS_FALL
    );
}
