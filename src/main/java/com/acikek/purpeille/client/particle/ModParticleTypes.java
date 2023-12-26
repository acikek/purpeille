package com.acikek.purpeille.client.particle;

import com.acikek.purpeille.Purpeille;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModParticleTypes {

    public static final DefaultParticleType ANCIENT_GUARDIAN = FabricParticleTypes.simple(false);

    public static void register() {
        Registry.register(Registries.PARTICLE_TYPE, Purpeille.id("ancient_guardian"), ANCIENT_GUARDIAN);
    }
}
