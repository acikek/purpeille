package com.acikek.purpeille.block.ancient.gateway;

import com.acikek.purpeille.block.ancient.AncientMachine;
import com.acikek.purpeille.item.core.EncasedCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
        super(settings, AncientGatewayBlockEntity::tick, AncientGatewayBlockEntity::new);
        setDefaultState(getDefaultFacing().with(READY, false).with(CHARGING, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.MAIN_HAND && world.getBlockEntity(pos) instanceof AncientGatewayBlockEntity blockEntity && !blockEntity.playerCheckCore(player, hand)) {
            ItemStack handStack = player.getStackInHand(hand);
            if (!blockEntity.isEmpty()) {
                blockEntity.removeCore(world, player, true, pos, state);
                return ActionResult.SUCCESS;
            }
            else if (handStack.getItem() instanceof EncasedCore) {
                blockEntity.addCore(world, handStack, true, player, pos, state);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
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
                BlockState newState = blockEntity.activate(world, pos, state);
                world.setBlockState(pos, newState.with(CHARGING, false));
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
