package com.acikek.purpeille.client.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class AncientGuardianParticle extends AnimatedParticle {

    public AncientGuardianParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 1.3f);
        velocityMultiplier = 0.75f;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        scale *= 0.65f;
        maxAge = 60 + random.nextInt(15);
        setSpriteForAge(spriteProvider);
        boolean r = random.nextInt(4) == 0;
        setColor((r ? 0.6f : 0.4f) + random.nextFloat() * 0.2f, random.nextFloat() * 0.2f, (r ? 0.6f : 0.3f) + random.nextFloat() * 0.3f);
    }

    public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType> {

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new AncientGuardianParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }

    public static void register() {
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.ANCIENT_GUARDIAN, Factory::new);
    }
}
