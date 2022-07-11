package com.acikek.purpeille.block.entity.monolithic;

import com.acikek.purpeille.block.BlockSettings;
import com.acikek.purpeille.block.entity.CommonBlockWithEntity;
import com.acikek.purpeille.block.entity.SingleSlotBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MonolithicPurpur extends CommonBlockWithEntity<MonolithicPurpurBlockEntity> {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final IntProperty TRANSITION = IntProperty.of("transition", 0, 5);

    public static final AbstractBlock.Settings SETTINGS = BlockSettings.baseSettings(Material.STONE)
            .strength(5.0f)
            .sounds(BlockSoundGroup.BONE)
            .luminance(value -> (int) (value.get(TRANSITION) * 1.75));

    public MonolithicPurpur(Settings settings) {
        super(settings, MonolithicPurpurBlockEntity::tick, null, true);
        setDefaultState(getDefaultFacing().with(FULL, false).with(TRANSITION, 0));
    }

    public static final EnumProperty<?>[] SUPPORTED_PROPERTIES = {
            Properties.AXIS,
            Properties.HORIZONTAL_AXIS,
            Properties.FACING,
            Properties.HORIZONTAL_FACING,
    };

    public Pair<Integer, EnumProperty<?>> getSupportedProperty(BlockState state) {
        for (int i = 0; i < SUPPORTED_PROPERTIES.length; i++) {
            if (state.contains(SUPPORTED_PROPERTIES[i])) {
                return new Pair<>(i, SUPPORTED_PROPERTIES[i]);
            }
        }
        return null;
    }

    @Override
    public ActionResult extraChecks(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        if (player.isSneaking()
                && !blockEntity.isEmpty()
                && blockEntity.getItem().getItem() instanceof BlockItem blockItem
                && blockEntity instanceof MonolithicPurpurBlockEntity monolithicPurpur) {
            boolean canCycle = monolithicPurpur.property != -1;
            Pair<Integer, EnumProperty<?>> property = null;
            if (!canCycle) {
                property = getSupportedProperty(blockItem.getBlock().getDefaultState());
            }
            if (property != null) {
                monolithicPurpur.property = property.getLeft();
                canCycle = true;
            }
            if (canCycle) {
                if (!world.isClient()) {
                    monolithicPurpur.playSound(blockItem.getBlock().getDefaultState().getSoundGroup().getPlaceSound(), 1.5f);
                }
                monolithicPurpur.cycleProperty();
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        return null;
    }

    public static void playSound(World world, SingleSlotBlockEntity blockEntity, SoundEvent event) {
        blockEntity.playSound(event, world.random.nextFloat() * 0.4f + 0.8f);
    }

    @Override
    public ActionResult addItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        if (world.getBlockState(pos.up()).isAir()) {
            super.addItem(state, world, pos, player, hand, handStack, blockEntity);
            if (!world.isClient()) {
                playSound(world, blockEntity, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE);
                blockEntity.playSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, world.random.nextFloat() * 0.4f + 0.8f);
            }
            if (handStack.getItem() instanceof BlockItem blockItem) {
                blockEntity.playSound(blockItem.getBlock().getDefaultState().getSoundGroup().getPlaceSound(), 1.5f);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult removeItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        if (blockEntity instanceof MonolithicPurpurBlockEntity monolithicPurpur && monolithicPurpur.canRemove()) {
            super.removeItem(state, world, pos, player, hand, handStack, blockEntity);
            if (!world.isClient()) {
                playSound(world, blockEntity, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.getBlockState(pos.up()).isAir() && state.get(FULL)) {
            world.breakBlock(pos, true);
        }
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
        builder.add(FULL).add(FACING).add(TRANSITION);
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
