package com.acikek.purpeille.block.ancient.gateway;

import com.acikek.purpeille.block.ancient.AncientMachine;
import com.acikek.purpeille.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AncientGateway extends AncientMachine<AncientGatewayBlockEntity> {

    public static BooleanProperty READY = BooleanProperty.of("ready");
    public static BooleanProperty CHARGING = BooleanProperty.of("charging");

    public static Settings SETTINGS = AncientMachine.SETTINGS
            .luminance(state -> state.get(READY) ? 2 : 0);

    public AncientGateway(Settings settings) {
        super(settings, AncientGatewayBlockEntity::new, AncientGatewayBlockEntity::tick);
        setDefaultState(getDefaultFacing().with(READY, false).with(CHARGING, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        if (world.getBlockEntity(pos) instanceof AncientGatewayBlockEntity blockEntity) {
            SoundEvent event = null;
            ItemStack handStack = player.getStackInHand(hand);
            if (blockEntity.hasItem()) {
                event = SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM;
                if (!player.isCreative()) {
                    player.getInventory().offerOrDrop(blockEntity.getItem());
                }
                blockEntity.removeItem();
                BlockState newState = state.with(READY, false);
                if (state.get(CHARGING)) {
                    newState = newState.with(CHARGING, false);
                    blockEntity.charge = 0;
                }
                world.setBlockState(pos, newState);
            }
            else if (handStack.isOf(ModItems.ENCASED_CORE)) {
                event = SoundEvents.BLOCK_END_PORTAL_FRAME_FILL;
                blockEntity.addItem(ModItems.ENCASED_CORE);
                if (!player.isCreative()) {
                    handStack.setCount(handStack.getCount() - 1);
                }
                world.setBlockState(pos, state.with(READY, true).with(CHARGING, world.isReceivingRedstonePower(pos)));
            }
            if (event != null) {
                world.playSound(null, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(READY).add(CHARGING);
    }

    @Override
    public BlockEntityType<AncientGatewayBlockEntity> getBlockEntityType() {
        return AncientGatewayBlockEntity.BLOCK_ENTITY_TYPE;
    }
}
