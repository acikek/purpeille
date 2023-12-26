package com.acikek.purpeille.block.entity.ancient.oven;

import com.acikek.purpeille.block.BlockSettings;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.entity.CommonBlockWithEntity;
import com.acikek.purpeille.block.entity.SingleSlotBlockEntity;
import com.acikek.purpeille.util.BlockItemProvider;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public class AncientOven extends CommonBlockWithEntity<AncientOvenBlockEntity> implements BlockItemProvider {

    public static BooleanProperty LIT = FurnaceBlock.LIT;

    public static final Settings SETTINGS = FabricBlockSettings.copyOf(BlockSettings.ANCIENT_MACHINE)
            .luminance(state -> state.get(LIT) ? 8 : state.get(FULL) ? 3 : 0);

    public Damage damage;

    public AncientOven(Settings settings, Damage damage) {
        super(settings, AncientOvenBlockEntity::tick, null, false);
        setDefaultState(getDefaultFacing().with(LIT, false).with(FULL, false));
        this.damage = damage;
    }

    @Override
    public ActionResult removeItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        if (!state.get(LIT) && state.get(FULL) && blockEntity instanceof AncientOvenBlockEntity ancientOven) {
            ancientOven.finishRecipe(world, player, pos, state);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult addItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        return !state.get(FULL)
                && blockEntity instanceof AncientOvenBlockEntity ancientOven
                && ancientOven.startRecipe(world, handStack, true, player, pos, state)
                ? ActionResult.SUCCESS
                : ActionResult.PASS;
    }

    public static BlockState getNextState(BlockState state, Damage damage) {
        BlockState newState = Damage.getNext(damage).getDefaultState();
        return newState.isOf(Blocks.AIR) ? newState : newState
                .with(FACING, state.get(FACING))
                .with(LIT, state.get(LIT));
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT) && random.nextDouble() < 0.1 && world instanceof ClientWorld clientWorld) {
            clientWorld.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        }
    }

    @Override
    public boolean isStateAllowed(BlockState state, BlockState newState) {
        return newState.getBlock() == ModBlocks.ANCIENT_OVEN
                || newState.getBlock() == ModBlocks.ANCIENT_OVEN_DIM
                || newState.getBlock() == ModBlocks.ANCIENT_OVEN_VERY_DIM;
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        if (builder.get(LootContextParameters.BLOCK_ENTITY) instanceof AncientOvenBlockEntity blockEntity) {
            if (blockEntity.durability == damage.max) {
                return List.of(new ItemStack(this));
            }
            return List.of(blockEntity.getOvenStack(state));
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (!state.get(FULL)) {
            return 0;
        }
        if (!state.get(LIT)) {
            return 1;
        }
        if (world.getBlockEntity(pos) instanceof AncientOvenBlockEntity blockEntity) {
            int value = (int) ((blockEntity.cookTime / 1200.0) * 15);
            return MathHelper.clamp(value, 1, 15);
        }
        return 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FULL).add(LIT);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AncientOvenBlockEntity(pos, state, damage);
    }

    @Override
    public BlockEntityType<AncientOvenBlockEntity> getBlockEntityType() {
        return AncientOvenBlockEntity.BLOCK_ENTITY_TYPE;
    }

    @Override
    public BiFunction<Block, Item.Settings, BlockItem> getBlockItem() {
        return AncientOvenBlockItem::new;
    }
}
