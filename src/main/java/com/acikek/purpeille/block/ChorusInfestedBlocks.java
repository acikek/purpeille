package com.acikek.purpeille.block;

import com.acikek.purpeille.block.ancient.AncientMachine;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ChorusInfestedBlocks {

    public static final Map<Block, Block> GROWTH = new HashMap<>();

    static {
        GROWTH.put(ModBlocks.CHORUS_INFESTED_MECHANICAL_BRICKS, ModBlocks.CHORAL_BLOOM_INFESTED_MECHANICAL_BRICKS);
        GROWTH.put(ModBlocks.CHORUS_INFESTED_MECHANICAL_BRICK_SLAB, ModBlocks.CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_SLAB);
        GROWTH.put(ModBlocks.CHORUS_INFESTED_MECHANICAL_BRICK_STAIRS, ModBlocks.CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_STAIRS);
        GROWTH.put(ModBlocks.CHORUS_INFESTED_MECHANICAL_BRICK_WALL, ModBlocks.CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_WALL);
    }

    public interface ChorusInfested extends Fertilizable {

        @Override
        default boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
            return true;
        }

        @Override
        default boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
            return true;
        }

        @Override
        default void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
            world.setBlockState(pos, GROWTH.get(state.getBlock()).getStateWithProperties(state));
        }
    }

    public static class InfestedBlock extends Block implements ChorusInfested {

        public InfestedBlock() {
            super(AncientMachine.SETTINGS);
        }
    }

    public static class Slab extends SlabBlock implements ChorusInfested {

        public Slab() {
            super(AncientMachine.SETTINGS);
        }
    }

    public static class Stairs extends StairsBlock implements ChorusInfested {

        public Stairs(BlockState baseState) {
            super(baseState, AncientMachine.SETTINGS);
        }
    }

    public static class Wall extends WallBlock implements ChorusInfested {

        public Wall() {
            super(AncientMachine.SETTINGS);
        }
    }
}
