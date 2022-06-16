package com.acikek.purpeille.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UltravioletComplex extends Block {

    public static final AbstractBlock.Settings SETTINGS = BlockSettings.baseSettings(Material.STONE)
            .strength(1.5f)
            .sounds(BlockSoundGroup.CALCITE)
            .luminance(2);

    public static final AbstractBlock.Settings POLISHED_SETTINGS = BlockSettings.baseSettings(Material.AMETHYST)
            .strength(2.0f)
            .sounds(BlockSoundGroup.AMETHYST_BLOCK)
            .luminance(4);

    public static final DamageSource DAMAGE_SOURCE = new DamageSource("ultravioletComplex").setFire();

    public int threshold;

    public UltravioletComplex(Settings settings, int threshold) {
        super(settings);
        this.threshold = threshold;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        int light = world.getLightLevel(pos.up());
        if (light > threshold && !entity.bypassesSteppingEffects()
                && entity instanceof LivingEntity livingEntity
                && !EnchantmentHelper.hasFrostWalker(livingEntity)) {
            float damage = (light - threshold) / ((15.0f - threshold) / 2.0f);
            entity.damage(DAMAGE_SOURCE, damage);
        }
        super.onSteppedOn(world, pos, state, entity);
    }
}
