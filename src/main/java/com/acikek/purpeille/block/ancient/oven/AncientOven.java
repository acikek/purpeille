package com.acikek.purpeille.block.ancient.oven;

import com.acikek.purpeille.block.ancient.AncientMachine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AncientOven extends AncientMachine<AncientOvenBlockEntity> {

    public static BooleanProperty LIT = FurnaceBlock.LIT;

    public static final Settings SETTINGS = AncientMachine.SETTINGS
            .luminance(state -> state.get(LIT) ? 8 : 0);

    public AncientOven(Settings settings) {
        super(settings, AncientOvenBlockEntity::new, AncientOvenBlockEntity::tick);
        setDefaultState(getDefaultFacing().with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LIT);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AncientOvenBlockEntity(pos, state);
    }

    @Override
    public BlockEntityType<AncientOvenBlockEntity> getBlockEntityType() {
        return AncientOvenBlockEntity.BLOCK_ENTITY_TYPE;
    }
}
