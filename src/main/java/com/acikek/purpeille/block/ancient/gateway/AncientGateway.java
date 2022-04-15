package com.acikek.purpeille.block.ancient.gateway;

import com.acikek.purpeille.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AncientGateway extends BlockWithEntity implements BlockEntityProvider {

    public static BooleanProperty READY = BooleanProperty.of("ready");
    public static BooleanProperty CHARGING = BooleanProperty.of("charging");
    public static DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public static Settings SETTINGS = FabricBlockSettings.of(Material.STONE)
            .strength(6.0f)
            .requiresTool()
            .sounds(BlockSoundGroup.NETHER_BRICKS)
            .luminance(state -> state.get(READY) ? 2 : 0);

    public AncientGateway(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(READY, false)
                .with(CHARGING, false)
                .with(FACING, Direction.NORTH)
        );
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        if (world.getBlockEntity(pos) instanceof AncientGatewayBlockEntity blockEntity) {
            SoundEvent event = null;
            ItemStack handStack = player.getStackInHand(hand);
            if (blockEntity.hasCore()) {
                event = SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM;
                blockEntity.removeCore();
                if (!player.isCreative()) {
                    player.getInventory().offerOrDrop(blockEntity.getCore());
                }
                BlockState newState = state.with(READY, false);
                if (state.get(CHARGING)) {
                    newState = newState.with(CHARGING, false);
                    blockEntity.charge = 0;
                }
                world.setBlockState(pos, newState);
            }
            else if (handStack.isOf(ModItems.ENCASED_CORE)) {
                event = SoundEvents.BLOCK_END_PORTAL_FRAME_FILL;
                blockEntity.addCore();
                if (!player.isCreative()) {
                    handStack.setCount(handStack.getCount() - 1);
                }
                world.setBlockState(pos, state.with(READY, true).with(CHARGING, world.isReceivingRedstonePower(pos)));
            }
            world.playSound(null, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient()) {
            boolean powered = world.isReceivingRedstonePower(pos);
            boolean ready = state.get(READY);
            boolean charging = state.get(CHARGING);
            if (powered && ready && !charging) {
                world.setBlockState(pos, state.with(CHARGING, true));
            }
            else if (charging && !powered && world.getBlockEntity(pos) instanceof AncientGatewayBlockEntity blockEntity) {
                blockEntity.activate(world, pos, state);
                world.setBlockState(pos, state.with(CHARGING, false));
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()
                && world.getBlockEntity(pos) instanceof AncientGatewayBlockEntity blockEntity) {
            ItemScatterer.spawn(world, pos, blockEntity.items);
            world.removeBlockEntity(pos);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(READY).add(CHARGING).add(FACING);
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, AncientGatewayBlockEntity.BLOCK_ENTITY_TYPE, AncientGatewayBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AncientGatewayBlockEntity(pos, state);
    }
}
