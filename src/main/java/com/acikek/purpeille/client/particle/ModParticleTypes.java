package com.acikek.purpeille.client.particle;

import com.acikek.purpeille.Purpeille;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class ModParticleTypes {

    public static final DefaultParticleType ANCIENT_GUARDIAN = FabricParticleTypes.simple(false);
    public static final DefaultParticleType FALLING_MONOLITHIC_RESIDUE = FabricParticleTypes.simple(false);
    public static final DefaultParticleType DRIPPING_MONOLITHIC_RESIDUE = FabricParticleTypes.simple(false);
    public static final DefaultParticleType LANDING_MONOLITHIC_RESIDUE = FabricParticleTypes.simple(false);

    public static Map<String, ParticleType<?>> PARTICLES = new HashMap<>();

    static {
        PARTICLES.put("ancient_guardian", ANCIENT_GUARDIAN);
        PARTICLES.put("falling_monolithic_residue", FALLING_MONOLITHIC_RESIDUE);
        PARTICLES.put("dripping_monolithic_residue", DRIPPING_MONOLITHIC_RESIDUE);
        PARTICLES.put("landing_monolithic_residue", LANDING_MONOLITHIC_RESIDUE);
        PlayerEntityRenderer
    }

    public static void register() {
        for (Map.Entry<String, ParticleType<?>> entry : PARTICLES.entrySet()) {
            Registry.register(Registry.PARTICLE_TYPE, Purpeille.id(entry.getKey()), entry.getValue());
        }
    }
}
