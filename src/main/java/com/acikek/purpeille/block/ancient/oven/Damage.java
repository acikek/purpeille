package com.acikek.purpeille.block.ancient.oven;

import com.acikek.purpeille.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.MathHelper;

public enum Damage {

    NONE(256, 128, 0),
    DIM(128, 64, 1),
    VERY_DIM(64, 0, 2);

    public int max;
    public int min;
    public int index;

    Damage(int value, int min, int index) {
        this.max = value;
        this.min = min;
        this.index = index;
    }

    public AncientOven createBlock() {
        return new AncientOven(AncientOven.SETTINGS, this);
    }

    public boolean inRange(int durability) {
        return durability > min && durability < max;
    }

    public static AncientOven[] getOvens() {
        return new AncientOven[] {
                ModBlocks.ANCIENT_OVEN,
                ModBlocks.ANCIENT_OVEN_DIM,
                ModBlocks.ANCIENT_OVEN_VERY_DIM
        };
    }

    public static Damage getFromDurability(int durability) {
        if (durability <= VERY_DIM.min) {
            return null;
        }
        for (Damage damage : Damage.values()) {
            if (damage.inRange(durability)) {
                return damage;
            }
        }
        return null;
    }

    public static Block getNext(int durability) {
        Damage damage = getFromDurability(durability);
        if (damage == null) {
            return Blocks.AIR;
        }
        return getOvens()[damage.index];
    }

    public static int clamp(int durability) {
        return MathHelper.clamp(durability, VERY_DIM.min, NONE.max);
    }

    public BlockEntityType<AncientOvenBlockEntity> getBlockEntityType() {
        return switch (this) {
            case NONE -> AncientOvenBlockEntity.BLOCK_ENTITY_TYPE;
            case DIM -> AncientOvenBlockEntity.DIM_BLOCK_ENTITY_TYPE;
            case VERY_DIM -> AncientOvenBlockEntity.VERY_DIM_BLOCK_ENTITY_TYPE;
        };
    }
}
