package com.acikek.purpeille.block;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.advancement.ModCriteria;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UltravioletComplex extends Block {

    public enum Type {

        NORMAL(9),
        POLISHED(7);

        public int threshold;

        Type(int threshold) {
            this.threshold = threshold;
        }
    }

    public static final AbstractBlock.Settings SETTINGS = BlockSettings.baseSettings()
            .strength(1.5f)
            .sounds(BlockSoundGroup.CALCITE)
            .luminance(2);

    public static final AbstractBlock.Settings POLISHED_SETTINGS = BlockSettings.baseSettings()
            .strength(2.0f)
            .sounds(BlockSoundGroup.AMETHYST_BLOCK)
            .luminance(4);

    public Type type;

    public UltravioletComplex(Settings settings, Type type) {
        super(settings);
        this.type = type;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof PlayerEntity player && player.isCreative()) {
            return;
        }
        int light = world.getLightLevel(pos.up());
        if (light > type.threshold && !entity.bypassesSteppingEffects()
                && entity instanceof LivingEntity livingEntity
                && !EnchantmentHelper.hasFrostWalker(livingEntity)) {
            float damage = (light - type.threshold) / ((15.0f - type.threshold) / 2.0f);
            entity.damage(new DamageSource(world.getRegistryManager()
                    .getWrapperOrThrow(RegistryKeys.DAMAGE_TYPE)
                    .getOrThrow(RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Purpeille.id("ultraviolet_complex")))
            ), damage);
            if (entity instanceof ServerPlayerEntity player) {
                ModCriteria.ULTRAVIOLET_COMPLEX_BURNS.trigger(player, type, light);
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }
}
