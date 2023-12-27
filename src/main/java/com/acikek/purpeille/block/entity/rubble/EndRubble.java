package com.acikek.purpeille.block.entity.rubble;

import com.acikek.purpeille.block.entity.CommonBlockWithEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EndRubble extends CommonBlockWithEntity<EndRubbleBlockEntity> {

    public static final Settings SETTINGS = FabricBlockSettings.create()
            .strength(2.5f)
            .collidable(false)
            .requiresTool();

    public static final VoxelShape SHAPE = VoxelShapes.cuboid(0.0625, 0.0, 0.0625, 0.9375, 0.375, 0.9375);

    public EndRubble(Settings settings) {
        super(settings, EndRubbleBlockEntity::tick, EndRubbleBlockEntity::new, false);
        setDefaultState(getDefaultFacing());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        if (world.getBlockEntity(pos) instanceof EndRubbleBlockEntity blockEntity) {
            player.openHandledScreen(blockEntity);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName() && world.getBlockEntity(pos) instanceof EndRubbleBlockEntity blockEntity) {
            blockEntity.setCustomName(itemStack.getName());
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public BlockEntityType<EndRubbleBlockEntity> getBlockEntityType() {
        return EndRubbleBlockEntity.BLOCK_ENTITY_TYPE;
    }
}
