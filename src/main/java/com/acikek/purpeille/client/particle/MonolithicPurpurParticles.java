package com.acikek.purpeille.client.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.NotNull;

public class MonolithicPurpurParticles {

    public static void setColor(Particle particle) {
        particle.setColor(0.51171875F, 0.03125F, 0.890625F);
    }

    public static class Dripping extends BlockLeakParticle.Dripping {

        public Dripping(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            super(world, x, y, z, Fluids.EMPTY, ModParticleTypes.FALLING_MONOLITHIC_RESIDUE);
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.velocityZ = velocityZ;
        }

        @Override
        public void move(double dx, double dy, double dz) {
            move(0.2f);
            super.move(dx, dy, dz);
        }

        public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType> {

            @Override
            public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
                MonolithicPurpurParticles.Dripping dripping = new MonolithicPurpurParticles.Dripping(world, x, y, z, velocityX, velocityY, velocityZ);
                dripping.gravityStrength *= 0.01F;
                dripping.maxAge = 100;
                MonolithicPurpurParticles.setColor(dripping);
                dripping.setSprite(this.spriteProvider);
                return dripping;
            }
        }
    }

    public record FallingFactory(SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType> {

        @Override
        public @NotNull Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            BlockLeakParticle falling = new BlockLeakParticle.ContinuousFalling(world, x, y, z, Fluids.EMPTY, ModParticleTypes.LANDING_MONOLITHIC_RESIDUE);
            falling.gravityStrength = 0.01F;
            setColor(falling);
            falling.setSprite(this.spriteProvider);
            return falling;
        }
    }

    public record LandingFactory(SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType> {

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            BlockLeakParticle landing = new BlockLeakParticle.Landing(world, x, y, z, Fluids.EMPTY);
            landing.maxAge = (int) (28.0 / (Math.random() * 0.8 + 0.2));
            setColor(landing);
            landing.setSprite(this.spriteProvider);
            return landing;
        }
    }

    public static void register() {
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.DRIPPING_MONOLITHIC_RESIDUE, Dripping.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.FALLING_MONOLITHIC_RESIDUE, FallingFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.LANDING_MONOLITHIC_RESIDUE, LandingFactory::new);
    }
}
