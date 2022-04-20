package com.acikek.purpeille.block.ancient.oven;

import com.acikek.purpeille.advancement.ModCriteria;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.ancient.AncientMachineBlockEntity;
import com.acikek.purpeille.recipe.oven.AncientOvenRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
        result = (world != null ? recipe.getOutput(world.random) : recipe.getOutput()).copy();
    }

    public boolean checkDamage(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        durability = Damage.clamp(durability);
        if (state.getBlock() instanceof AncientOven block && !block.damage.inRange(durability)) {
            Damage newDamage = Damage.getFromDurability(durability);
            world.setBlockState(pos, AncientOven.getNextState(state, newDamage));
            block.breakParticles(world, pos, state);
            if (player != null) {
                ModCriteria.ANCIENT_OVEN_DAMAGED.trigger((ServerPlayerEntity) player, newDamage);
            }
            if (world.getBlockEntity(pos) instanceof AncientOvenBlockEntity blockEntity) {
                blockEntity.durability = durability;
            }
            return false;
        }
        return true;
    }

    public Optional<AncientOvenRecipe> getRecipeMatch(World world, ItemStack stack) {
        if (world == null) {
            return Optional.empty();
        }
        return world.getRecipeManager().getFirstMatch(AncientOvenRecipe.Type.INSTANCE, new SimpleInventory(stack), world);
    }

    public void startRecipe(World world, ItemStack stack, boolean unset, PlayerEntity player, BlockPos pos, BlockState state) {
        getRecipeMatch(world, stack).ifPresent(match -> {
            if (unset) {
                setItem(stack.getItem());
            }
            addRecipe(match);
            if (player != null && !player.isCreative()) {
                stack.setCount(stack.getCount() - 1);
            }
            world.setBlockState(pos, state.with(AncientOven.LIT, true).with(AncientOven.FULL, true));
        });
    }

    public void finishRecipe(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        if (player != null) {
            player.getInventory().offerOrDrop(getItem());
        }
        durability -= damageToTake;
        damageToTake = 0;
        result = ItemStack.EMPTY;
        if (checkDamage(world, player, pos, state)) {
            world.setBlockState(pos, state.with(AncientOven.FULL, false));
        }
        world.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public ItemStack getOvenStack(BlockState state) {
        ItemStack stack = new ItemStack(state.getBlock());
        stack.getOrCreateNbt().putInt("AncientOvenDurability", durability);
        return stack;
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
    public void setStack(int slot, ItemStack stack) {
        super.setStack(slot, stack);
        startRecipe(world, stack, false, null, pos, getCachedState());
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        finishRecipe(world, null, pos, getCachedState());
        return super.removeStack(slot, count);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return dir != Direction.DOWN
                && !getCachedState().get(AncientOven.FULL)
                && getRecipeMatch(world, stack).isPresent();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        BlockState state = getCachedState();
        return dir == Direction.DOWN
                && state.get(AncientOven.FULL)
                && !state.get(AncientOven.LIT);
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
