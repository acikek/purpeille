package com.acikek.purpeille.client.particle;

import com.acikek.purpeille.Purpeille;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;

public class ModParticleTypes {

    public static final DefaultParticleType ANCIENT_GUARDIAN = FabricParticleTypes.simple(false);

    public static void register() {
        Registry.register(Registry.PARTICLE_TYPE, Purpeille.id("ancient_guardian"), ANCIENT_GUARDIAN);
    }
}
