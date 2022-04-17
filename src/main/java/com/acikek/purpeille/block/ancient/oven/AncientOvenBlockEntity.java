package com.acikek.purpeille.block.ancient.oven;

import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.ancient.AncientMachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AncientOvenBlockEntity extends AncientMachineBlockEntity {

    public static BlockEntityType<AncientOvenBlockEntity> BLOCK_ENTITY_TYPE;
    public static BlockEntityType<AncientOvenBlockEntity> DIM_BLOCK_ENTITY_TYPE;
    public static BlockEntityType<AncientOvenBlockEntity> VERY_DIM_BLOCK_ENTITY_TYPE;

    public int durability;
    public int cookTime;

    public AncientOvenBlockEntity(BlockPos pos, BlockState state) {
        super(state.getBlock() instanceof AncientOven block ? block.getBlockEntityType() : BLOCK_ENTITY_TYPE, pos, state);
    }

    public AncientOvenBlockEntity(BlockPos pos, BlockState state, Damage damage) {
        this(pos, state);
        this.durability = damage.value;
    }

    public boolean checkDamage(World world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof AncientOven block) {
            if (durability <= block.damage.min) {
                world.setBlockState(pos, block.getNextState(state));
                block.breakParticles(world, pos, state);
                return true;
            }
        }
        return false;
    }

    public static void tick(World world, BlockPos pos, BlockState state, AncientOvenBlockEntity blockEntity) {
        if (state.get(AncientOven.LIT)) {
            blockEntity.cookTime--;
            if (blockEntity.cookTime == 0) {
                world.setBlockState(pos, state.with(AncientOven.LIT, false));
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        durability = nbt.getInt("Durability");
        cookTime = nbt.getInt("CookTime");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("Durability", durability);
        nbt.putInt("CookTime", cookTime);
        super.writeNbt(nbt);
    }

    public static void register() {
        BLOCK_ENTITY_TYPE = ModBlocks.ANCIENT_OVEN.getBlockEntityType("ancient_oven_block_entity");
        DIM_BLOCK_ENTITY_TYPE = ModBlocks.ANCIENT_OVEN_DIM.getBlockEntityType("ancient_oven_dim_block_entity");
        VERY_DIM_BLOCK_ENTITY_TYPE = ModBlocks.ANCIENT_OVEN_VERY_DIM.getBlockEntityType("ancient_oven_very_dim_block_entity");
    }
}
