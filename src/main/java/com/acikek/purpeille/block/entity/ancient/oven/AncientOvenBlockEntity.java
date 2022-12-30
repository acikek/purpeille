package com.acikek.purpeille.block.entity.ancient.oven;

import com.acikek.purpeille.advancement.ModCriteria;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.entity.CommonBlockWithEntity;
import com.acikek.purpeille.block.entity.SingleSlotBlockEntity;
import com.acikek.purpeille.block.entity.ModBlockEntities;
import com.acikek.purpeille.item.ModItems;
import com.acikek.purpeille.recipe.oven.AncientOvenRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AncientOvenBlockEntity extends SingleSlotBlockEntity {

    public static BlockEntityType<AncientOvenBlockEntity> BLOCK_ENTITY_TYPE;

    public int durability;
    public int cookTime;
    public int damageToTake;
    public int voidAmalgams;
    public ItemStack result;

    public AncientOvenBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
        result = ItemStack.EMPTY;
    }

    public AncientOvenBlockEntity(BlockPos pos, BlockState state, Damage damage) {
        this(pos, state);
        durability = damage.max;
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
            block.spawnBreakParticles(world, null, pos, state);
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ModCriteria.triggerAncientOvenDamaged(serverPlayer, newDamage);
            }
            if (newDamage != null && newDamage.compareTo(block.damage) < 0 && world.random.nextFloat() > 0.75f) {
                voidAmalgams++;
            }
            System.out.println(world.getBlockEntity(pos) instanceof AncientOvenBlockEntity);
            System.out.println(voidAmalgams);
            if (world.getBlockEntity(pos) instanceof AncientOvenBlockEntity blockEntity) {
                blockEntity.durability = durability;
                blockEntity.voidAmalgams = voidAmalgams;
            }
            else {
                int size = voidAmalgams / 64 + 1;
                DefaultedList<ItemStack> items = DefaultedList.ofSize(size);
                for (int i = 0; i < size; i++) {
                    ItemStack stack = ModItems.VOID_AMALGAM.getDefaultStack();
                    stack.setCount(MathHelper.clamp(voidAmalgams, 0, 64));
                    items.add(stack);
                    voidAmalgams -= 64;
                }
                ItemScatterer.spawn(world, pos, items);
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

    public boolean startRecipe(World world, ItemStack stack, boolean unset, PlayerEntity player, BlockPos pos, BlockState state) {
        Optional<AncientOvenRecipe> optional = getRecipeMatch(world, stack);
        if (optional.isEmpty()) {
            return false;
        }
        onAddItem(stack, unset, player);
        addRecipe(optional.get());
        world.setBlockState(pos, state.with(AncientOven.LIT, true).with(CommonBlockWithEntity.FULL, true));
        return true;
    }

    public void finishRecipe(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        onRemoveItem(player, false, false, true);
        durability -= damageToTake;
        damageToTake = 0;
        result = ItemStack.EMPTY;
        if (checkDamage(world, player, pos, state)) {
            world.setBlockState(pos, state.with(CommonBlockWithEntity.FULL, false));
        }
        playSound(SoundEvents.UI_STONECUTTER_TAKE_RESULT);
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
                && !getCachedState().get(CommonBlockWithEntity.FULL)
                && getRecipeMatch(world, stack).isPresent();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        BlockState state = getCachedState();
        return dir == Direction.DOWN
                && state.get(CommonBlockWithEntity.FULL)
                && !state.get(AncientOven.LIT);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        durability = nbt.getInt("Durability");
        cookTime = nbt.getInt("CookTime");
        damageToTake = nbt.getInt("DamageToTake");
        voidAmalgams = nbt.getInt("VoidAmalgams");
        result = ItemStack.fromNbt(nbt.getCompound("Result"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("Durability", durability);
        nbt.putInt("CookTime", cookTime);
        nbt.putInt("DamageToTake", damageToTake);
        nbt.putInt("VoidAmalgams", voidAmalgams);
        nbt.put("Result", result.writeNbt(new NbtCompound()));
        super.writeNbt(nbt);
    }

    public static void register() {
        BLOCK_ENTITY_TYPE = ModBlockEntities.build(
                "ancient_oven_block_entity",
                AncientOvenBlockEntity::new,
                ModBlocks.ANCIENT_OVEN,
                ModBlocks.ANCIENT_OVEN_DIM,
                ModBlocks.ANCIENT_OVEN_VERY_DIM
        );
    }
}
