package com.acikek.purpeille.block;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.advancement.ModCriteria;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class ChorusInfestedBlocks implements UseBlockCallback {

    public static final Identifier INFESTATION_TRIM = Purpeille.id("infestation_trim");

    public static final List<Block> CHORAL_BLOOM = List.of(
            ModBlocks.CHORAL_BLOOM_INFESTED_MECHANICAL_BRICKS,
            ModBlocks.CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_SLAB,
            ModBlocks.CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_STAIRS,
            ModBlocks.CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_WALL
    );

    public static final BiMap<Block, Block> STAGES = HashBiMap.create();

    static {
        STAGES.put(ModBlocks.ANCIENT_MECHANICAL_BRICKS, ModBlocks.CHORUS_INFESTED_MECHANICAL_BRICKS);
        STAGES.put(ModBlocks.ANCIENT_MECHANICAL_BRICK_SLAB, ModBlocks.CHORUS_INFESTED_MECHANICAL_BRICK_SLAB);
        STAGES.put(ModBlocks.ANCIENT_MECHANICAL_BRICK_STAIRS, ModBlocks.CHORUS_INFESTED_MECHANICAL_BRICK_STAIRS);
        STAGES.put(ModBlocks.ANCIENT_MECHANICAL_BRICK_WALL, ModBlocks.CHORUS_INFESTED_MECHANICAL_BRICK_WALL);
        STAGES.put(ModBlocks.CHORUS_INFESTED_MECHANICAL_BRICKS, ModBlocks.CHORAL_BLOOM_INFESTED_MECHANICAL_BRICKS);
        STAGES.put(ModBlocks.CHORUS_INFESTED_MECHANICAL_BRICK_SLAB, ModBlocks.CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_SLAB);
        STAGES.put(ModBlocks.CHORUS_INFESTED_MECHANICAL_BRICK_STAIRS, ModBlocks.CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_STAIRS);
        STAGES.put(ModBlocks.CHORUS_INFESTED_MECHANICAL_BRICK_WALL, ModBlocks.CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_WALL);
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (hand == Hand.MAIN_HAND && player.getStackInHand(hand).isOf(Items.SHEARS)) {
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (STAGES.inverse().containsKey(state.getBlock())) {
                world.setBlockState(pos, STAGES.inverse().get(state.getBlock()).getStateWithProperties(state));
                world.playSound(null, pos, SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.BLOCKS, 1.0f, 1.0f);
                if (world instanceof ServerWorld serverWorld) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(pos);
                    buf.writeInt(Block.getRawIdFromState(state));
                    for (ServerPlayerEntity playerEntity : PlayerLookup.tracking(serverWorld, pos)) {
                        ServerPlayNetworking.send(playerEntity, INFESTATION_TRIM, buf);
                    }
                }
                boolean dropped = false;
                if (CHORAL_BLOOM.contains(state.getBlock()) && world.random.nextFloat() > 0.5f) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), Items.CHORUS_FRUIT.getDefaultStack());
                    dropped = true;
                }
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    if (!player.isCreative()) {
                        serverPlayer.getStackInHand(hand).damage(1, world.random, serverPlayer);
                    }
                    ModCriteria.CHORUS_INFESTATION_SHEARED.trigger(serverPlayer, dropped);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
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
            world.setBlockState(pos, STAGES.get(state.getBlock()).getStateWithProperties(state));
        }
    }

    public static class InfestedBlock extends Block implements ChorusInfested {

        public InfestedBlock(Settings settings) {
            super(settings);
        }
    }

    public static class Slab extends SlabBlock implements ChorusInfested {

        public Slab(Settings settings) {
            super(settings);
        }
    }

    public static class Stairs extends StairsBlock implements ChorusInfested {

        public Stairs(BlockState baseState, Settings settings) {
            super(baseState, settings);
        }
    }

    public static class Wall extends WallBlock implements ChorusInfested {

        public Wall(Settings settings) {
            super(settings);
        }
    }
}
