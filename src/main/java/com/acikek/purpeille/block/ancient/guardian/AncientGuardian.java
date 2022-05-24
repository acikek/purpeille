package com.acikek.purpeille.block.ancient.guardian;

import com.acikek.purpeille.block.ancient.AncientMachine;
import com.acikek.purpeille.block.ancient.CorePoweredAncientMachine;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class AncientGuardian extends CorePoweredAncientMachine<AncientGuardianBlockEntity> implements Waterloggable {

    public static Settings SETTINGS = AncientMachine.SETTINGS
            .luminance(state -> state.get(FULL) ? 2 : 0);

    public static BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final MutableText HAS_TETHERED_PLAYER = new TranslatableText("use.purpeille.ancient_guardian.has_tethered_player");

    public static final VoxelShape HEAD_SHAPE = VoxelShapes.cuboid(0.375f, 0.75f, 0.375f, 0.625f, 1.0f, 0.625f);

    public static final VoxelShape Z_SHAPE = VoxelShapes.union(HEAD_SHAPE, getTorsoShape(true), getLegsShape(true));
    public static final VoxelShape X_SHAPE = VoxelShapes.union(HEAD_SHAPE, getTorsoShape(false), getLegsShape(false));

    public static VoxelShape getRotationalCuboid(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean z) {
        return VoxelShapes.cuboid(z ? minX : minZ, minY, z ? minZ : minX, z ? maxX : maxZ, maxY, z ? maxZ : maxX);
    }

    public static VoxelShape getTorsoShape(boolean z) {
        return getRotationalCuboid(0.25f, 0.375f, 0.4375f, 0.75f, 0.75f, 0.5625f, z);
    }

    public static VoxelShape getLegsShape(boolean z) {
        return getRotationalCuboid(0.375f, 0.0f, 0.4375f, 0.625f, 0.375f, 0.5625f, z);
    }

    public static boolean isZ(Direction direction) {
        return switch (direction) {
            case EAST, WEST -> false;
            default -> true;
        };
    }

    public AncientGuardian(Settings settings) {
        super(settings, AncientGuardianBlockEntity::tick, AncientGuardianBlockEntity::new, AncientGuardianBlockEntity.class, false);
        setDefaultState(getDefaultFacing().with(FULL, false).with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return isZ(state.get(FACING)) ? Z_SHAPE : X_SHAPE;
    }

    @Override
    public boolean canPlayerUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, AncientGuardianBlockEntity blockEntity) {
        if (blockEntity.isPlayerTethered(player)) {
            return true;
        }
        player.sendMessage(HAS_TETHERED_PLAYER, true);
        return false;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FULL).add(WATERLOGGED);
    }

    @Override
    public BlockEntityType<AncientGuardianBlockEntity> getBlockEntityType() {
        return AncientGuardianBlockEntity.BLOCK_ENTITY_TYPE;
    }
}
