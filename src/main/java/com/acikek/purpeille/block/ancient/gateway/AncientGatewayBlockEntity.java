package com.acikek.purpeille.block.ancient.gateway;

import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.ancient.AncientMachineBlockEntity;
import com.acikek.purpeille.sound.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AncientGatewayBlockEntity extends AncientMachineBlockEntity {

    public static BlockEntityType<AncientGatewayBlockEntity> BLOCK_ENTITY_TYPE;

    public int charge = 0;

    public AncientGatewayBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
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
        int blocks = charge / 4;
        charge = 0;
        return blocks;
    }

    public Vec3d getDestination(PlayerEntity player, BlockState state, int blocks) {
        return Vec3d.ofCenter(player.getBlockPos().offset(state.get(AncientGateway.FACING), blocks));
    }

    public void activate(World world, BlockPos pos, BlockState state) {
        PlayerEntity player = getPlayer(world, pos);
        int blocks = getBlocks();
        if (player == null || blocks == 0) {
            return;
        }
        Vec3d destination = getDestination(player, state, blocks);
        player.teleport(destination.getX(), destination.getY(), destination.getZ());
        player.playSound(ModSoundEvents.ANCIENT_GATEWAY_TELEPORT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.playSound(player, pos, ModSoundEvents.ANCIENT_GATEWAY_TELEPORT, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AncientGatewayBlockEntity blockEntity) {
        if (state.get(AncientGateway.CHARGING)) {
            blockEntity.charge++;
            if (world.getTime() % 80L == 0) {
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 1.0f, 0.5f);
            }
        }
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

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public static void register() {
        BLOCK_ENTITY_TYPE = build("ancient_gateway_block_entity", AncientGatewayBlockEntity::new, ModBlocks.ANCIENT_GATEWAY);
    }
}
