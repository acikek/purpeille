package com.acikek.purpeille.block.ancient.gateway;

import com.acikek.purpeille.block.ancient.AncientMachine;
import com.acikek.purpeille.block.ancient.CorePoweredAncientMachine;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AncientGateway extends CorePoweredAncientMachine<AncientGatewayBlockEntity> {

    public static BooleanProperty READY = BooleanProperty.of("ready");
    public static BooleanProperty CHARGING = BooleanProperty.of("charging");

    public static Settings SETTINGS = FabricBlockSettings.copyOf(AncientMachine.SETTINGS)
            .luminance(state -> state.get(READY) ? 2 : 0);

    public AncientGateway(Settings settings) {
        super(settings, AncientGatewayBlockEntity::tick, AncientGatewayBlockEntity::new, AncientGatewayBlockEntity.class, true);
        setDefaultState(getDefaultFacing().with(READY, false).with(CHARGING, false));
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
