package com.acikek.purpeille.block.ancient.guardian;

import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.ancient.AncientMachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class AncientGuardianBlockEntity extends AncientMachineBlockEntity {

    public static BlockEntityType<AncientGuardianBlockEntity> BLOCK_ENTITY_TYPE;

    public UUID linkedPlayer;

    public AncientGuardianBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

    public boolean isPlayerLinked(PlayerEntity player) {
        return linkedPlayer != null && linkedPlayer.equals(player.getUuid());
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
