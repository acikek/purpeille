package com.acikek.purpeille.block.entity;

import com.acikek.purpeille.block.entity.monolithic.MonolithicPurpurBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public abstract class CommonBlockWithEntity<T extends BlockEntity> extends BlockWithEntity {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty FULL = BooleanProperty.of("full");

    public BlockEntityTicker<T> ticker;
    public BiFunction<BlockPos, BlockState, T> supplier;
    public boolean canSetFull;

    public CommonBlockWithEntity(Settings settings, BlockEntityTicker<T> ticker, BiFunction<BlockPos, BlockState, T> supplier, boolean canSetFull) {
        super(settings);
        this.ticker = ticker;
        this.supplier = supplier;
        this.canSetFull = canSetFull;
    }

    public ActionResult extraChecks(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        return null;
    }

    public ActionResult removeItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        blockEntity.onRemoveItem(player, true, false, true);
        if (canSetFull) {
            world.setBlockState(pos, state.with(FULL, false));
        }
        return ActionResult.SUCCESS;
    }

    public boolean isValidHandStack(ItemStack stack) {
        return !stack.isEmpty();
    }

    public ActionResult addItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        blockEntity.onAddItem(handStack, true, player);
        if (canSetFull) {
            world.setBlockState(pos, state.with(FULL, true));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.MAIN_HAND && world.getBlockEntity(pos) instanceof SingleSlotBlockEntity blockEntity) {
            ItemStack handStack = player.getStackInHand(hand);
            ActionResult check = extraChecks(state, world, pos, player, hand, handStack, blockEntity);
            if (check != null) {
                return check;
            }
            if (!blockEntity.isEmpty()) {
                return removeItem(state, world, pos, player, hand, handStack, blockEntity);
            }
            else if (isValidHandStack(handStack)) {
                return addItem(state, world, pos, player, hand, handStack, blockEntity);
            }
        }
        return ActionResult.PASS;
    }



    public BlockState getDefaultFacing() {
        return getStateManager().getDefaultState().with(FACING, Direction.NORTH);
    }

    public boolean isStateAllowed(BlockState state, BlockState newState) {
        return state.getBlock() == newState.getBlock();
    }

    public abstract BlockEntityType<T> getBlockEntityType();

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.getBlockEntity(pos) instanceof Inventory inventory) {
            if (!isStateAllowed(state, newState)) {
                ItemScatterer.spawn(world, pos, inventory);
            }
            if (state.getBlock() != newState.getBlock()) {
                world.removeBlockEntity(pos);
            }
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (supplier == null) {
            return null;
        }
        return supplier.apply(pos, state);
    }

    @Nullable
    @Override
    public <E extends BlockEntity> BlockEntityTicker<E> getTicker(World world, BlockState state, BlockEntityType<E> type) {
        if (ticker == null) {
            return null;
        }
        return checkType(type, getBlockEntityType(), ticker);
    }
}
