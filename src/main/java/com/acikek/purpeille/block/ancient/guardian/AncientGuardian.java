package com.acikek.purpeille.block.ancient.guardian;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.ancient.AncientMachine;
import com.acikek.purpeille.block.ancient.CorePoweredAncientMachine;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AncientGuardian extends CorePoweredAncientMachine<AncientGuardianBlockEntity> implements Waterloggable {

    public static final Identifier ANCIENT_GUARDIAN_ACTIVATED = Purpeille.id("ancient_guardian_activated");
    public static final Identifier VACUOUS_BLAST = Purpeille.id("vacuous_blast");

    public static Settings SETTINGS = FabricBlockSettings.copyOf(AncientMachine.SETTINGS)
            .luminance(state -> state.get(FULL) ? 2 : 0);

    public static BooleanProperty ON_COOLDOWN = BooleanProperty.of("on_cooldown");
    public static BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final MutableText HAS_TETHERED_PLAYER = Text.translatable("use.purpeille.ancient_guardian.has_tethered_player");

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
        setDefaultState(getDefaultFacing().with(FULL, false).with(ON_COOLDOWN, false).with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return isZ(state.get(FACING)) ? Z_SHAPE : X_SHAPE;
    }

    @Override
    public boolean canPlayerUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, AncientGuardianBlockEntity blockEntity) {
        if (blockEntity.pendingRemoval) {
            return false;
        }
        if (blockEntity.isPlayerTethered(player)) {
            return true;
        }
        player.sendMessage(HAS_TETHERED_PLAYER, true);
        return false;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (!world.isClient() && world.getBlockEntity(pos) instanceof AncientGuardianBlockEntity blockEntity && blockEntity.cooldown != 0) {
            world.createExplosion(null, DamageSource.badRespawnPoint(), null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5.0f, true, Explosion.DestructionType.DESTROY);
        }
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        boolean full = state.get(FULL);
        boolean cooldown = state.get(ON_COOLDOWN);
        if (full || cooldown) {
            Vec3f center = new Vec3f(Vec3d.ofCenter(pos));
            boolean isZ = isZ(state.get(FACING));
            float x = center.getX() + ((random.nextFloat() * 2) - 1) * (isZ ? 0.3f : 0.1f);
            float y = center.getY() + ((random.nextFloat() * 2) - 1) * 0.15f;
            float z = center.getZ() + ((random.nextFloat() * 2) - 1) * (isZ ? 0.1f : 0.3f);
            DefaultParticleType particle = cooldown ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.SMALL_FLAME;
            world.addParticle(particle, x, y, z, 0.0, 0.01, 0.0);
            if (!cooldown || random.nextFloat() > 0.9f) {
                SoundEvent event = cooldown ? SoundEvents.ENTITY_ENDERMAN_AMBIENT : SoundEvents.BLOCK_CANDLE_AMBIENT;
                world.playSound(center.getX(), center.getY(), center.getZ(), event, SoundCategory.BLOCKS, 0.5f, random.nextFloat() * 0.5f + 0.1f, false);
            }
        }
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
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        return state.get(ON_COOLDOWN)
                ? Collections.emptyList()
                : Collections.singletonList(new ItemStack(ModBlocks.ANCIENT_GUARDIAN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FULL).add(ON_COOLDOWN).add(WATERLOGGED);
    }

    @Override
    public BlockEntityType<AncientGuardianBlockEntity> getBlockEntityType() {
        return AncientGuardianBlockEntity.BLOCK_ENTITY_TYPE;
    }
}
