package com.acikek.purpeille.block.ancient.gateway;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.item.ModItems;
import com.acikek.purpeille.sound.ModSoundEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AncientGatewayBlockEntity extends BlockEntity {

    public static BlockEntityType<AncientGatewayBlockEntity> BLOCK_ENTITY_TYPE;

    public DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    public int charge = 0;

    public AncientGatewayBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

    public ItemStack getCore() {
        return items.get(0);
    }

    public boolean hasCore() {
        return !getCore().isEmpty();
    }

    public void addCore() {
        items.set(0, new ItemStack(ModItems.ENCASED_CORE));
    }

    public void removeCore() {
        items.set(0, ItemStack.EMPTY);
    }

    public PlayerEntity getPlayer(World world, BlockPos pos) {
        return world.getClosestPlayer(pos.getX(), pos.up().getY(), pos.getZ(), 1.5, false);
    }

    public int getBlocks() {
        int blocks = charge / 5;
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
        Inventories.readNbt(nbt, items);
        charge = nbt.getInt("Charge");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
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
        BLOCK_ENTITY_TYPE = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                Purpeille.id("ancient_gateway_block_entity"),
                FabricBlockEntityTypeBuilder.create(
                        AncientGatewayBlockEntity::new,
                        ModBlocks.ANCIENT_GATEWAY
                )
                        .build(null)
        );
    }
}
