package com.acikek.purpeille.block.ancient.oven;

import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.ancient.AncientMachineBlockEntity;
import com.acikek.purpeille.recipe.oven.AncientOvenRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AncientOvenBlockEntity extends AncientMachineBlockEntity {

    public static BlockEntityType<AncientOvenBlockEntity> BLOCK_ENTITY_TYPE;
    public static BlockEntityType<AncientOvenBlockEntity> DIM_BLOCK_ENTITY_TYPE;
    public static BlockEntityType<AncientOvenBlockEntity> VERY_DIM_BLOCK_ENTITY_TYPE;

    public int durability;
    public int cookTime;
    public int damageToTake;
    public ItemStack result;

    public AncientOvenBlockEntity(BlockPos pos, BlockState state) {
        super(state.getBlock() instanceof AncientOven block ? block.getBlockEntityType() : BLOCK_ENTITY_TYPE, pos, state);
        result = ItemStack.EMPTY;
    }

    public AncientOvenBlockEntity(BlockPos pos, BlockState state, Damage damage) {
        this(pos, state);
        this.durability = damage.max;
    }

    public void addRecipe(AncientOvenRecipe recipe) {
        cookTime = recipe.cookTime();
        damageToTake = recipe.damage();
        result = recipe.result().copy();
    }

    public boolean checkDamage(World world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof AncientOven block) {
            boolean below = durability <= block.damage.min;
            boolean above = durability >= block.damage.max;
            if (above && block.damage == Damage.NONE) {
                durability = block.damage.max;
            }
            else if (below || above) {
                world.setBlockState(pos, block.getNextState(state, below));
                block.breakParticles(world, pos, state);
                if (world.getBlockEntity(pos) instanceof AncientOvenBlockEntity blockEntity) {
                    blockEntity.durability = durability;
                }
                return false;
            }
        }
        return true;
    }

    public void finishRecipe(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        player.getInventory().offerOrDrop(getItem());
        removeItem();
        durability -= damageToTake;
        damageToTake = 0;
        result = ItemStack.EMPTY;
        if (checkDamage(world, pos, state)) {
            world.setBlockState(pos, state.with(AncientOven.FULL, false));
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, AncientOvenBlockEntity blockEntity) {
        if (!world.isClient() && state.get(AncientOven.LIT)) {
            blockEntity.cookTime--;
            if (blockEntity.cookTime == 0) {
                world.setBlockState(pos, state.with(AncientOven.LIT, false));
                blockEntity.setItem(blockEntity.result);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        durability = nbt.getInt("Durability");
        cookTime = nbt.getInt("CookTime");
        damageToTake = nbt.getInt("DamageToTake");
        result = ItemStack.fromNbt(nbt.getCompound("Result"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("Durability", durability);
        nbt.putInt("CookTime", cookTime);
        nbt.putInt("DamageToTake", damageToTake);
        nbt.put("Result", result.writeNbt(new NbtCompound()));
        super.writeNbt(nbt);
    }

    public static void register() {
        BLOCK_ENTITY_TYPE = ModBlocks.ANCIENT_OVEN.getBlockEntityType("ancient_oven_block_entity");
        DIM_BLOCK_ENTITY_TYPE = ModBlocks.ANCIENT_OVEN_DIM.getBlockEntityType("ancient_oven_dim_block_entity");
        VERY_DIM_BLOCK_ENTITY_TYPE = ModBlocks.ANCIENT_OVEN_VERY_DIM.getBlockEntityType("ancient_oven_very_dim_block_entity");
    }
}