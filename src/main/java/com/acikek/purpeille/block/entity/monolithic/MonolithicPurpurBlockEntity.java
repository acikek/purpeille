package com.acikek.purpeille.block.entity.monolithic;

import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.entity.ModBlockEntities;
import com.acikek.purpeille.block.entity.SingleSlotBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MonolithicPurpurBlockEntity extends SingleSlotBlockEntity {

    public static BlockEntityType<MonolithicPurpurBlockEntity> BLOCK_ENTITY_TYPE;

    public MonolithicPurpurBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, MonolithicPurpurBlockEntity blockEntity) {

    }

    public static void register() {
        BLOCK_ENTITY_TYPE = ModBlockEntities.build("monolithic_purpur_block_entity", MonolithicPurpurBlockEntity::new, ModBlocks.MONOLITHIC_PURPUR);
    }
}
