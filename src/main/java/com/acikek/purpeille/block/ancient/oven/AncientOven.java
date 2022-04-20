package com.acikek.purpeille.block.ancient.oven;

import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.ancient.AncientMachine;
import com.acikek.purpeille.block.ancient.AncientMachineBlockEntity;
import com.acikek.purpeille.recipe.oven.AncientOvenRecipe;
import lib.BlockItemProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

public class AncientOven extends AncientMachine<AncientOvenBlockEntity> implements BlockItemProvider {

    public static BooleanProperty LIT = FurnaceBlock.LIT;
    public static BooleanProperty FULL = BooleanProperty.of("full");

    public static final Settings SETTINGS = AncientMachine.SETTINGS
            .luminance(state -> state.get(LIT) ? 8 : state.get(FULL) ? 3 : 0);

    public Damage damage;

    public AncientOven(Settings settings, Damage damage) {
        super(settings, AncientOvenBlockEntity::tick);
        setDefaultState(getDefaultFacing().with(LIT, false).with(FULL, false));
        this.damage = damage;
    }

    public BlockEntityType<AncientOvenBlockEntity> getBlockEntityType(String id) {
        return AncientMachineBlockEntity.build(id, AncientOvenBlockEntity::new, this);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof AncientOvenBlockEntity blockEntity) {
            ItemStack handStack = player.getStackInHand(hand);
            boolean lit = state.get(LIT);
            boolean full = state.get(FULL);
            if (blockEntity.hasItem() && !lit && full) {
                blockEntity.finishRecipe(world, player, pos, state);
                world.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            else if (!handStack.isEmpty() && !full) {
                SimpleInventory inventory = new SimpleInventory(handStack);
                world.getRecipeManager().getFirstMatch(AncientOvenRecipe.Type.INSTANCE, inventory, world).ifPresent(match -> {
                    blockEntity.setItem(handStack.getItem());
                    blockEntity.addRecipe(match);
                    if (!player.isCreative()) {
                        handStack.setCount(handStack.getCount() - 1);
                    }
                    world.setBlockState(pos, state.with(LIT, true).with(FULL, true));
                });
            }
        }
        return ActionResult.SUCCESS;
    }

    public static BlockState getNextState(BlockState state, Damage damage) {
        BlockState newState = Damage.getNext(damage).getDefaultState();
        return newState.isOf(Blocks.AIR) ? newState : newState
                .with(FACING, state.get(FACING))
                .with(LIT, state.get(LIT));
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT) && random.nextDouble() < 0.1 && world instanceof ClientWorld clientWorld) {
            clientWorld.playSound(pos, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        }
    }

    @Override
    public boolean isStateAllowed(BlockState state, BlockState newState) {
        return newState.getBlock() == ModBlocks.ANCIENT_OVEN
                || newState.getBlock() == ModBlocks.ANCIENT_OVEN_DIM
                || newState.getBlock() == ModBlocks.ANCIENT_OVEN_VERY_DIM;
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        if (builder.get(LootContextParameters.BLOCK_ENTITY) instanceof AncientOvenBlockEntity blockEntity) {
            if (blockEntity.durability == damage.max) {
                return List.of(new ItemStack(this));
            }
            return List.of(blockEntity.getOvenStack(state));
        }
        return Collections.emptyList();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LIT).add(FULL);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AncientOvenBlockEntity(pos, state, damage);
    }

    @Override
    public BlockEntityType<AncientOvenBlockEntity> getBlockEntityType() {
        return damage.getBlockEntityType();
    }

    @Override
    public BiFunction<Block, Item.Settings, BlockItem> getBlockItem() {
        return AncientOvenBlockItem::new;
    }
}
