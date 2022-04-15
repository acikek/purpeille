package com.acikek.purpeille.sound;

import com.acikek.purpeille.Purpeille;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class ModSoundEvents {

    public static SoundEvent ANCIENT_GATEWAY_TELEPORT = create("block.ancient_gateway.teleport");

    public static SoundEvent create(String id) {
        return new SoundEvent(Purpeille.id(id));
    }

    public static SoundEvent[] SOUNDS = {
            ANCIENT_GATEWAY_TELEPORT
    };

    public static void register() {
        for (SoundEvent sound : SOUNDS) {
            Registry.register(Registry.SOUND_EVENT, sound.getId(), sound);
        }
    }
}
