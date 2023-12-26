package com.acikek.purpeille.sound;

import com.acikek.purpeille.Purpeille;
import net.minecraft.sound.SoundEvent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModSoundEvents {

    public static final SoundEvent ANCIENT_GATEWAY_TELEPORT = create("block.ancient_gateway.teleport");
    public static final SoundEvent LIGHTNING_BLOCK_PLACE = create("block.lightning_block.place");
    public static final SoundEvent LIGHTNING_BLOCK_BREAK = create("block.lightning_block.break");
    public static final SoundEvent INFESTED_BLOCK_PLACE = create("block.infested_block.place");
    public static final SoundEvent INFESTED_BLOCK_BREAK = create("block.infested_block.break");
    public static final SoundEvent RUBBLE_OPEN = create("block.rubble.open");
    public static final SoundEvent RUBBLE_CLOSE = create("block.rubble.close");
    public static final SoundEvent IMBUE_RISE = create("imbue.rise");
    public static final SoundEvent IMBUE_COLLAPSE = create("imbue.collapse");

    public static SoundEvent create(String id) {
        return SoundEvent.of(Purpeille.id(id));
    }

    public static SoundEvent[] SOUNDS = {
            ANCIENT_GATEWAY_TELEPORT,
            LIGHTNING_BLOCK_PLACE,
            LIGHTNING_BLOCK_BREAK,
            INFESTED_BLOCK_PLACE,
            INFESTED_BLOCK_BREAK,
            RUBBLE_OPEN,
            RUBBLE_CLOSE,
            IMBUE_RISE,
            IMBUE_COLLAPSE
    };

    public static void register() {
        for (SoundEvent sound : SOUNDS) {
            Registry.register(Registries.SOUND_EVENT, sound.getId(), sound);
        }
    }
}
