package com.acikek.purpeille.block.ancient.guardian;

import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.ancient.AncientMachine;
import com.acikek.purpeille.block.ancient.CorePoweredAncientMachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class AncientGuardianBlockEntity extends CorePoweredAncientMachineBlockEntity {

    public static BlockEntityType<AncientGuardianBlockEntity> BLOCK_ENTITY_TYPE;

    public static MutableText VOID_TETHER_RESTORED = new TranslatableText("message.purpeille.ancient_guardian.void_tether_restored")
            .formatted(Formatting.GRAY, Formatting.ITALIC);

    public UUID tetheredPlayer;
    public int cooldown;

    public AncientGuardianBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

    public boolean isPlayerTethered(PlayerEntity player) {
        return tetheredPlayer != null && tetheredPlayer.equals(player.getUuid());
    }

    public static AncientGuardianBlockEntity getTether(ServerPlayerEntity player) {
        if (player.getSpawnPointPosition() != null
                && player.world.getBlockEntity(player.getSpawnPointPosition()) instanceof AncientGuardianBlockEntity blockEntity) {
            return blockEntity;
        }
        return null;
    }

    public void activate(PlayerEntity player) {
        cooldown = 6000;
        player.setHealth(player.getMaxHealth());
    }

    @Override
    public void addCore(World world, ItemStack stack, boolean unset, PlayerEntity player, BlockPos pos, BlockState state) {
        super.addCore(world, stack, unset, player, pos, state);
        tetheredPlayer = player.getUuid();
        world.setBlockState(pos, state.with(AncientMachine.FULL, true));
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.setSpawnPoint(world.getRegistryKey(), pos, 0.0f, false, false);
            playSound(SoundEvents.ITEM_FIRECHARGE_USE, 0.5f);
        }
    }

    @Override
    public void removeCore(World world, PlayerEntity player, boolean remove, BlockPos pos, BlockState state) {
        super.removeCore(world, player, remove, pos, state);
        tetheredPlayer = null;
        playSound(SoundEvents.BLOCK_DEEPSLATE_STEP);
        world.setBlockState(pos, state.with(AncientMachine.FULL, false));
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, AncientGuardianBlockEntity blockEntity) {
        if (blockEntity.cooldown > 0) {
            blockEntity.cooldown--;
            if (!world.isClient() && blockEntity.cooldown == 0) {
                MinecraftServer server = world.getServer();
                if (server == null) {
                    return;
                }
                PlayerEntity player = server.getPlayerManager().getPlayer(blockEntity.tetheredPlayer);
                if (player != null) {
                    player.sendMessage(VOID_TETHER_RESTORED, false);
                }
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        tetheredPlayer = nbt.containsUuid("TetheredPlayer") ? nbt.getUuid("TetheredPlayer") : null;
        cooldown = nbt.getInt("Cooldown");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if (tetheredPlayer != null) {
            nbt.putUuid("TetheredPlayer", tetheredPlayer);
        }
        nbt.putInt("Cooldown", cooldown);
        super.writeNbt(nbt);
    }

    public static void register() {
        BLOCK_ENTITY_TYPE = build("ancient_guardian_block_entity", AncientGuardianBlockEntity::new, ModBlocks.ANCIENT_GUARDIAN);
    }
}
