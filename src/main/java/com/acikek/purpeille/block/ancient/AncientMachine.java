package com.acikek.purpeille.block.ancient;

import lib.BlockItemProvider;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public abstract class AncientMachine<T extends AncientMachineBlockEntity> extends BlockWithEntity implements BlockItemProvider {

    public static DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public static final Settings SETTINGS = FabricBlockSettings.of(Material.STONE)
            .strength(6.0f)
            .requiresTool()
            .sounds(BlockSoundGroup.NETHER_BRICKS);

    public BlockEntityTicker<T> ticker;
    public BiFunction<BlockPos, BlockState, T> supplier;

    public AncientMachine(Settings settings, BlockEntityTicker<T> ticker, BiFunction<BlockPos, BlockState, T> supplier) {
        super(settings);
        this.ticker = ticker;
        this.supplier = supplier;
    }

    public AncientMachine(Settings settings, BlockEntityTicker<T> ticker) {
        this(settings, ticker, null);
    }

    public BlockState getDefaultFacing() {
        return getStateManager().getDefaultState().with(FACING, Direction.NORTH);
    }

    public boolean isStateAllowed(BlockState state, BlockState newState) {
        return state.getBlock() == newState.getBlock();
    }

    public void breakParticles(World world, BlockPos pos, BlockState state) {
        spawnBreakParticles(world, null, pos, state);
    }

    public abstract BlockEntityType<T> getBlockEntityType();

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.getBlockEntity(pos) instanceof AncientMachineBlockEntity blockEntity) {
            if (!isStateAllowed(state, newState)) {
                ItemScatterer.spawn(world, pos, blockEntity.items);
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
        return checkType(type, getBlockEntityType(), ticker);
    }

    @Override
    public BiFunction<Block, Item.Settings, BlockItem> getBlockItem() {
        return AncientMachineBlockItem::new;
    }
}
