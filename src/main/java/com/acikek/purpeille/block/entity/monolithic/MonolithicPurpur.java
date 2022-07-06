package com.acikek.purpeille.block.entity.monolithic;

import com.acikek.purpeille.block.BlockSettings;
import com.acikek.purpeille.block.entity.CommonBlockWithEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class MonolithicPurpur extends CommonBlockWithEntity<MonolithicPurpurBlockEntity> {

    public static final AbstractBlock.Settings SETTINGS = BlockSettings.baseSettings(Material.STONE)
            .strength(5.0f)
            .sounds(BlockSoundGroup.BONE);

    public static final DirectionProperty FACING = Properties.FACING;

    public MonolithicPurpur(Settings settings) {
        super(settings, MonolithicPurpurBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState getDefaultFacing() {
        return getStateManager().getDefaultState().with(FACING, Direction.NORTH);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockEntityType<MonolithicPurpurBlockEntity> getBlockEntityType() {
        return null;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.get(FACING) != Direction.DOWN) {
            return null;
        }
        return new MonolithicPurpurBlockEntity(pos, state);
    }
}
