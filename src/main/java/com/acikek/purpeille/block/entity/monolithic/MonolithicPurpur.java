package com.acikek.purpeille.block.entity.monolithic;

import com.acikek.purpeille.block.BlockSettings;
import com.acikek.purpeille.block.entity.CommonBlockWithEntity;
import com.acikek.purpeille.block.entity.SingleSlotBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MonolithicPurpur extends CommonBlockWithEntity<MonolithicPurpurBlockEntity> {

    public static final AbstractBlock.Settings SETTINGS = BlockSettings.baseSettings(Material.STONE)
            .strength(5.0f)
            .sounds(BlockSoundGroup.BONE);

    public static final DirectionProperty FACING = Properties.FACING;

    public MonolithicPurpur(Settings settings) {
        super(settings, MonolithicPurpurBlockEntity::tick);
    }

    @Override
    public ActionResult addItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        super.addItem(state, world, pos, player, hand, handStack, blockEntity);
        blockEntity.playSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, world.random.nextFloat() * 0.4f + 0.8f);
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult removeItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        if (blockEntity instanceof MonolithicPurpurBlockEntity monolithicPurpur && monolithicPurpur.canRemove()) {
            super.removeItem(state, world, pos, player, hand, handStack, blockEntity);
            blockEntity.playSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, world.random.nextFloat() * 0.4f + 0.8f);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayer() != null && ctx.getPlayer().isSneaking() ? ctx.getSide().getOpposite() : ctx.getSide();
        return getDefaultState().with(FACING, direction);
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
        return MonolithicPurpurBlockEntity.BLOCK_ENTITY_TYPE;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.get(FACING) != Direction.DOWN) {
            return null;
        }
        return new MonolithicPurpurBlockEntity(pos, state);
    }
}
