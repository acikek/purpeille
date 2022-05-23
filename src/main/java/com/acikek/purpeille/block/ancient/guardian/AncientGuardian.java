package com.acikek.purpeille.block.ancient.guardian;

import com.acikek.purpeille.block.ancient.AncientMachine;
import com.acikek.purpeille.block.ancient.CorePoweredAncientMachine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AncientGuardian extends CorePoweredAncientMachine<AncientGuardianBlockEntity> {

    public static Settings SETTINGS = AncientMachine.SETTINGS
            .luminance(state -> state.get(FULL) ? 2 : 0);

    public static final MutableText HAS_LINKED_PLAYER = new TranslatableText("tooltip.purpeille.guardian.has_linked_player");

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
        setDefaultState(getDefaultFacing().with(FULL, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return isZ(state.get(FACING)) ? Z_SHAPE : X_SHAPE;
    }

    @Override
    public boolean canPlayerUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, AncientGuardianBlockEntity blockEntity) {
        if (blockEntity.isPlayerLinked(player)) {
            return true;
        }
        player.sendMessage(HAS_LINKED_PLAYER, true);
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FULL);
    }

    @Override
    public BlockEntityType<AncientGuardianBlockEntity> getBlockEntityType() {
        return AncientGuardianBlockEntity.BLOCK_ENTITY_TYPE;
    }
}
