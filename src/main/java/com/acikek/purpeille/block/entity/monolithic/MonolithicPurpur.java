package com.acikek.purpeille.block.entity.monolithic;

import com.acikek.purpeille.advancement.ModCriteria;
import com.acikek.purpeille.api.abyssal.AbyssalToken;
import com.acikek.purpeille.api.abyssal.AbyssalTokens;
import com.acikek.purpeille.api.abyssal.ImbuementData;
import com.acikek.purpeille.block.BlockSettings;
import com.acikek.purpeille.block.entity.CommonBlockWithEntity;
import com.acikek.purpeille.block.entity.SingleSlotBlockEntity;
import com.acikek.purpeille.client.particle.ModParticleTypes;
import com.acikek.purpeille.item.core.EncasedCore;
import com.acikek.purpeille.sound.ModSoundEvents;
import com.acikek.purpeille.warpath.AbyssaliteData;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MonolithicPurpur extends CommonBlockWithEntity<MonolithicPurpurBlockEntity> {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final IntProperty TRANSITION = IntProperty.of("transition", 0, 5);

    public static final AbstractBlock.Settings SETTINGS = BlockSettings.baseSettings(Material.STONE)
            .strength(5.0f)
            .sounds(BlockSoundGroup.BONE)
            .luminance(value -> (int) (value.get(TRANSITION) * 1.75));

    public static final int MAX_IMBUEMENT_DISTANCE = 5;

    public MonolithicPurpur(Settings settings) {
        super(settings, MonolithicPurpurBlockEntity::tick, null, true);
        setDefaultState(getDefaultFacing().with(FULL, false).with(TRANSITION, 0));
    }

    public static List<Pair<BlockPos, Integer>> findAltars(World world, BlockPos pos) {
        List<Pair<BlockPos, Integer>> result = new ArrayList<>();
        for (Direction direction : Direction.HORIZONTAL) {
            for (int i = 1; i < MAX_IMBUEMENT_DISTANCE + 1; i++) {
                BlockPos target = pos.offset(direction, i);
                if (world.getBlockState(target).getBlock() instanceof MonolithicPurpur) {
                    result.add(new Pair<>(target, i - 1));
                    break;
                }
            }
        }
        return result;
    }

    public static float getBaseEnergyForDistance(int distance) {
        if (distance == 0) {
            return 90.0f;
        }
        return 90.0f * (-(float) Math.log10((double) distance / (MAX_IMBUEMENT_DISTANCE + 1))) / 6.0f;
    }

    public static float getRandomnessFactor(int altars, int timesImbued, List<AbyssaliteData.Effect> effects) {
        float altarFactor = altars * 2.5f;
        float timesX = timesImbued / 4.0f;
        float timesFactor = timesX * timesX * timesX;
        int effectsSize = effects.stream()
                .distinct()
                .toList()
                .size();
        float effectsFactor = effectsSize * 2.75f;
        return altarFactor + timesFactor - effectsFactor;
    }

    public static ActionResult tryImbue(World world, BlockPos pos, PlayerEntity player, ItemStack stack, AbyssalToken token) {
        // TODO: Refine these values
        // TODO: Remove debug statements
        // Find all altar blocks on the same x and z axis as this block.
        List<Pair<BlockPos, Integer>> altars = findAltars(world, pos);
        // If there aren't any, the imbuement fails.
        if (altars.isEmpty()) {
            return null;
        }
        // Get all the surrounding altars' block entities.
        List<MonolithicPurpurBlockEntity> blockEntities = altars.stream()
                .map(Pair::getLeft)
                .map(world::getBlockEntity)
                .map(blockEntity -> ((MonolithicPurpurBlockEntity) blockEntity))
                .filter(Objects::nonNull)
                .toList();
        // Get all the altars' item stacks.
        List<ItemStack> altarItems = blockEntities.stream()
                .map(MonolithicPurpurBlockEntity::getItem)
                .toList();
        // Get all the item stacks' modifier objects.
        AbyssaliteData abyssalite = token.getRevelation().abyssalite;
        List<Pair<Float, AbyssaliteData.Effect>> modifiers = altarItems.stream()
                .map(abyssalite::getModifier)
                .toList();
        // If any of the stacks doesn't match to a modifier, the imbuement fails.
        if (modifiers.contains(null)) {
            return null;
        }
        // Calculate the randomness factor with the token stack's imbuement data and the altars' stack effects.
        ImbuementData imbuements = ImbuementData.readNbt(stack.getOrCreateNbt().getCompound(ImbuementData.KEY));
        List<AbyssaliteData.Effect> effects = modifiers.stream()
                .map(Pair::getRight)
                .toList();
        float randomness = getRandomnessFactor(altars.size(), imbuements.count, effects);
        System.out.println("Distances: " + altars.stream().map(Pair::getRight).toList());
        System.out.println("Randomness: " + randomness);
        // Calculate the spiritual energy by multiplying the modifiers to the base distance energy for each altar.
        List<Float> baseEnergies = altars.stream()
                .map(Pair::getRight)
                .map(MonolithicPurpur::getBaseEnergyForDistance)
                .toList();
        System.out.println("Base energies: " + baseEnergies);
        List<Float> energies = new ArrayList<>();
        for (int i = 0; i < modifiers.size(); i++) {
            energies.add(baseEnergies.get(i) * modifiers.get(i).getLeft());
        }
        System.out.println(energies);
        float pureEnergy = energies.stream()
                .reduce(Float::sum)
                .orElse(0.0f);
        // Calculate the total energy based on the randomness factor.
        float energy = pureEnergy + world.random.nextFloat() * randomness;
        // Imbue the token stack.
        List<Item> itemsUsed = altarItems.stream()
                .map(ItemStack::getItem)
                .toList();
        System.out.println((int) energy);
        // Modify the energy values before the animation.
        AbyssalTokens.imbue(stack, (int) energy, itemsUsed);
        // Trigger criterion for the activating player.
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ModCriteria.ABYSSAL_TOKEN_IMBUED.trigger(serverPlayer, (int) energy, altars.size());
        }
        // Queue imbuements for all block entities.
        // The animation will automatically be different for the central altar.
        if (world.getBlockEntity(pos) instanceof MonolithicPurpurBlockEntity blockEntity) {
            blockEntity.queueImbuement();
        }
        for (MonolithicPurpurBlockEntity blockEntity : blockEntities) {
            blockEntity.queueImbuement();
        }
        return ActionResult.SUCCESS;
    }

    public static final EnumProperty<?>[] SUPPORTED_PROPERTIES = {
            Properties.AXIS,
            Properties.HORIZONTAL_AXIS,
            Properties.FACING,
            Properties.HORIZONTAL_FACING,
    };

    public static Pair<Integer, EnumProperty<?>> getSupportedProperty(BlockState state) {
        for (int i = 0; i < SUPPORTED_PROPERTIES.length; i++) {
            if (state.contains(SUPPORTED_PROPERTIES[i])) {
                return new Pair<>(i, SUPPORTED_PROPERTIES[i]);
            }
        }
        return null;
    }

    public static ActionResult tryCycleProperty(MonolithicPurpurBlockEntity blockEntity, BlockItem blockItem) {
        boolean canCycle = blockEntity.property != -1;
        Pair<Integer, EnumProperty<?>> property = null;
        if (!canCycle) {
            property = getSupportedProperty(blockItem.getBlock().getDefaultState());
        }
        if (property != null) {
            blockEntity.property = property.getLeft();
            canCycle = true;
        }
        if (canCycle) {
            blockEntity.playSound(blockItem.getBlock().getDefaultState().getSoundGroup().getPlaceSound(), 1.5f);
            blockEntity.cycleProperty();
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static void playSound(World world, SingleSlotBlockEntity blockEntity, SoundEvent event) {
        blockEntity.playSound(event, world.random.nextFloat() * 0.4f + 0.8f);
    }

    @Override
    public ActionResult extraChecks(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        MonolithicPurpurBlockEntity monolithicPurpur = (MonolithicPurpurBlockEntity) blockEntity;
        if (handStack.getItem() instanceof EncasedCore
                && monolithicPurpur.getItem().getItem() instanceof AbyssalToken token
                && token.isAbyssalToken()) {
            ActionResult result = tryImbue(world, pos, player, monolithicPurpur.getItem(), token);
            if (result != null) {
                handStack.damage(128, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
                playSound(world, blockEntity, ModSoundEvents.IMBUE_RISE);
            }
            return result;
        }
        if (world.getBlockState(pos.down()).isOf(Blocks.OBSIDIAN)) {
            return ActionResult.PASS;
        }
        if (player.isSneaking()
                && !blockEntity.isEmpty()
                && blockEntity.getItem().getItem() instanceof BlockItem blockItem) {
            return tryCycleProperty(monolithicPurpur, blockItem);
        }
        return null;
    }

    @Override
    public ActionResult addItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        if (world.getBlockState(pos.up()).isAir()) {
            super.addItem(state, world, pos, player, hand, handStack, blockEntity);
            playSound(world, blockEntity, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE);
            if (blockEntity.getItem().getItem() instanceof BlockItem blockItem) {
                blockEntity.playSound(blockItem.getBlock().getDefaultState().getSoundGroup().getPlaceSound(), 1.5f);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult removeItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack handStack, SingleSlotBlockEntity blockEntity) {
        if (blockEntity instanceof MonolithicPurpurBlockEntity monolithicPurpur && monolithicPurpur.canRemove()) {
            super.removeItem(state, world, pos, player, hand, handStack, blockEntity);
            playSound(world, blockEntity, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.getBlockState(pos.up()).isAir() && state.get(FULL)) {
            world.breakBlock(pos, true);
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayer() != null && ctx.getPlayer().isSneaking() ? ctx.getSide().getOpposite() : ctx.getSide();
        return getDefaultState().with(FACING, direction);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(FULL) && random.nextInt(5) == 0) {
            world.addParticle(ModParticleTypes.DRIPPING_MONOLITHIC_RESIDUE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0.6, 0.0, 0.0);
            /*Direction direction = Direction.random(random);
            if (direction != Direction.UP) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState = world.getBlockState(blockPos);
                if (!state.isOpaque() || !blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                    double d = direction.getOffsetX() == 0 ? random.nextDouble() : 0.5 + (double)direction.getOffsetX() * 0.6;
                    double e = direction.getOffsetY() == 0 ? random.nextDouble() : 0.5 + (double)direction.getOffsetY() * 0.6;
                    double f = direction.getOffsetZ() == 0 ? random.nextDouble() : 0.5 + (double)direction.getOffsetZ() * 0.6;
                    world.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + f, 0.0, 0.0, 0.0);
                }
            }*/
        }
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState getDefaultFacing() {
        return getStateManager().getDefaultState().with(FACING, Direction.NORTH);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FULL).add(FACING).add(TRANSITION);
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockEntityType<MonolithicPurpurBlockEntity> getBlockEntityType() {
        return MonolithicPurpurBlockEntity.BLOCK_ENTITY_TYPE;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.get(FACING) != Direction.DOWN) {
            return null;
        }
        return new MonolithicPurpurBlockEntity(pos, state);
    }
}
