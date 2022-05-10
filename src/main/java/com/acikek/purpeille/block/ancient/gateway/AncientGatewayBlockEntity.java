package com.acikek.purpeille.block.ancient.gateway;

import com.acikek.purpeille.advancement.ModCriteria;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.ancient.AncientMachineBlockEntity;
import com.acikek.purpeille.item.core.EncasedCore;
import com.acikek.purpeille.sound.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AncientGatewayBlockEntity extends AncientMachineBlockEntity {

    public static BlockEntityType<AncientGatewayBlockEntity> BLOCK_ENTITY_TYPE;

    public int charge = 0;

    public AncientGatewayBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

    public void addCore(World world, ItemStack stack, boolean unset, PlayerEntity player, BlockPos pos, BlockState state) {
        onAddItem(stack, unset, player);
        if (world != null) {
            world.setBlockState(pos, state
                    .with(AncientGateway.READY, true)
                    .with(AncientGateway.CHARGING, world.isReceivingRedstonePower(pos)));
            playSound(SoundEvents.BLOCK_END_PORTAL_FRAME_FILL);
        }
    }

    public void removeCore(World world, PlayerEntity player, boolean remove, BlockPos pos, BlockState state) {
        onRemoveItem(player, true);
        if (remove) {
            removeItem();
        }
        BlockState newState = state.with(AncientGateway.READY, false);
        if (state.get(AncientGateway.CHARGING)) {
            newState = newState.with(AncientGateway.CHARGING, false);
            charge = 0;
        }
        world.setBlockState(pos, newState);
        playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM);
    }

    public PlayerEntity getPlayer(World world, BlockPos pos) {
        BlockPos top = pos.up();
        Vec3d center = Vec3d.ofCenter(top);
        PlayerEntity player = world.getClosestPlayer(center.x, center.y, center.z, 1.0, false);
        if (player == null || !player.getBlockPos().equals(top)) {
            return null;
        }
        return player;
    }

    public int getBlocks() {
        int blocks = (charge / 4) * EncasedCore.getModifier(getItem());
        charge = 0;
        return blocks;
    }

    public Vec3d getDestination(PlayerEntity player, BlockState state, int blocks) {
        return Vec3d.ofCenter(player.getBlockPos().offset(state.get(AncientGateway.FACING), blocks));
    }

    public BlockState damageCore(World world, BlockState state, int blocks) {
        EncasedCore core = getCore();
        if (core != null && core.type == EncasedCore.Type.CREATIVE) {
            return state;
        }
        int damage = Math.abs(blocks / 10);
        if (damageCore(damage > 0 ? damage : 1, world.random)) {
            return state.with(AncientGateway.READY, false);
        }
        return state;
    }

    public BlockState activate(World world, BlockPos pos, BlockState state) {
        PlayerEntity player = getPlayer(world, pos);
        int blocks = getBlocks();
        if (player == null || blocks == 0) {
            return state;
        }
        Vec3d destination = getDestination(player, state, blocks);
        player.teleport(destination.getX(), destination.getY(), destination.getZ());
        player.playSound(ModSoundEvents.ANCIENT_GATEWAY_TELEPORT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.playSound(player, pos, ModSoundEvents.ANCIENT_GATEWAY_TELEPORT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        ModCriteria.ANCIENT_GATEWAY_USED.trigger((ServerPlayerEntity) player, blocks);
        return damageCore(world, state, blocks);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AncientGatewayBlockEntity blockEntity) {
        if (!world.isClient() && state.get(AncientGateway.CHARGING)) {
            blockEntity.charge++;
            if (world.getTime() % 80L == 0) {
                blockEntity.playSound(SoundEvents.BLOCK_BEACON_AMBIENT, 0.5f);
            }
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        super.setStack(slot, stack);
        addCore(world, stack, false, null, pos, getCachedState());
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        removeCore(world, null, false, pos, getCachedState());
        return super.removeStack(slot, count);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return dir != Direction.DOWN
                && !getCachedState().get(AncientGateway.READY)
                && stack.getItem() instanceof EncasedCore;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN
                && !getCachedState().get(AncientGateway.CHARGING);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        charge = nbt.getInt("Charge");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("Charge", charge);
        super.writeNbt(nbt);
    }

    public static void register() {
        BLOCK_ENTITY_TYPE = build("ancient_gateway_block_entity", AncientGatewayBlockEntity::new, ModBlocks.ANCIENT_GATEWAY);
    }
}
