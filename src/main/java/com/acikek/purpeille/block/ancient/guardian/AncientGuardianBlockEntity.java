package com.acikek.purpeille.block.ancient.guardian;

import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.ancient.AncientMachine;
import com.acikek.purpeille.block.ancient.CorePoweredAncientMachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class AncientGuardianBlockEntity extends CorePoweredAncientMachineBlockEntity {

    public static BlockEntityType<AncientGuardianBlockEntity> BLOCK_ENTITY_TYPE;

    public UUID linkedPlayer;

    public AncientGuardianBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

    public boolean isPlayerLinked(PlayerEntity player) {
        return linkedPlayer != null && linkedPlayer.equals(player.getUuid());
    }

    @Override
    public void addCore(World world, ItemStack stack, boolean unset, PlayerEntity player, BlockPos pos, BlockState state) {
        super.addCore(world, stack, unset, player, pos, state);
        linkedPlayer = player.getUuid();
        world.setBlockState(pos, state.with(AncientMachine.FULL, true));
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.setSpawnPoint(world.getRegistryKey(), pos, 0.0f, false, false);
            playSound(SoundEvents.ITEM_FIRECHARGE_USE, 0.5f);
        }
    }

    @Override
    public void removeCore(World world, PlayerEntity player, boolean remove, BlockPos pos, BlockState state) {
        super.removeCore(world, player, remove, pos, state);
        linkedPlayer = null;
        playSound(SoundEvents.BLOCK_DEEPSLATE_STEP);
        world.setBlockState(pos, state.with(AncientMachine.FULL, false));
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, AncientGuardianBlockEntity blockEntity) {

    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        linkedPlayer = nbt.containsUuid("LinkedPlayer") ? nbt.getUuid("LinkedPlayer") : null;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if (linkedPlayer != null) {
            nbt.putUuid("LinkedPlayer", linkedPlayer);
        }
        super.writeNbt(nbt);
    }

    public static void register() {
        BLOCK_ENTITY_TYPE = build("ancient_guardian_block_entity", AncientGuardianBlockEntity::new, ModBlocks.ANCIENT_GUARDIAN);
    }
}
