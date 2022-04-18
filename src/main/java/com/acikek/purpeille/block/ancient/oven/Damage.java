package com.acikek.purpeille.block.ancient.oven;

import com.acikek.purpeille.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public enum Damage {

    NONE(256, 128),
    DIM(128, 64),
    VERY_DIM(64, 0);

    public int max;
    public int min;

    Damage(int value, int min) {
        this.max = value;
        this.min = min;
    }

    public AncientOven getOven() {
        return new AncientOven(AncientOven.SETTINGS, this);
    }

    public Block getNext(boolean down) {
        return switch (this) {
            case NONE -> down ? ModBlocks.ANCIENT_OVEN_DIM : Blocks.AIR;
            case DIM -> down ? ModBlocks.ANCIENT_OVEN_VERY_DIM : ModBlocks.ANCIENT_OVEN;
            case VERY_DIM -> down ? Blocks.AIR : ModBlocks.ANCIENT_OVEN_DIM;
        };
    }

    public BlockEntityType<AncientOvenBlockEntity> getBlockEntityType() {
        return switch (this) {
            case NONE -> AncientOvenBlockEntity.BLOCK_ENTITY_TYPE;
            case DIM -> AncientOvenBlockEntity.DIM_BLOCK_ENTITY_TYPE;
            case VERY_DIM -> AncientOvenBlockEntity.VERY_DIM_BLOCK_ENTITY_TYPE;
        };
    }

    public List<ItemStack> getDroppedStacks() {
        // TODO drop based on recipe
        return List.of(new ItemStack(Items.APPLE));
    }
}
