package com.acikek.purpeille.attribute;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MiningContinuationEfficiency implements PlayerBlockBreakEvents.After {

    public interface DowntimeTracker {
        void setIsNotMining();
    }

    public static final TrackedData<Integer> CONTINUOUS_BLOCKS_MINED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> IS_NOT_MINING = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> NOT_MINING_TICKS = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        EntityAttributeInstance instance = player.getAttributeInstance(ModAttributes.GENERIC_MINING_CONTINUATION_EFFICIENCY);
        if (instance == null || instance.getValue() == 0.0) {
            return;
        }
        player.getDataTracker().set(CONTINUOUS_BLOCKS_MINED, player.getDataTracker().get(CONTINUOUS_BLOCKS_MINED) + 1);
        ((DowntimeTracker) player).setIsNotMining();
    }

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register(new MiningContinuationEfficiency());
    }
}
