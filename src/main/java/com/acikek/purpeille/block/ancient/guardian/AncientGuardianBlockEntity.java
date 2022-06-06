package com.acikek.purpeille.block.ancient.guardian;

import com.acikek.purpeille.advancement.ModCriteria;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.ancient.AncientMachine;
import com.acikek.purpeille.block.ancient.CorePoweredAncientMachineBlockEntity;
import com.acikek.purpeille.item.core.EncasedCore;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.UUID;

public class AncientGuardianBlockEntity extends CorePoweredAncientMachineBlockEntity {

    public static BlockEntityType<AncientGuardianBlockEntity> BLOCK_ENTITY_TYPE;

    public static MutableText VOID_TETHER_RESTORED = Text.translatable("message.purpeille.ancient_guardian.void_tether_restored")
            .formatted(Formatting.GRAY, Formatting.ITALIC);

    public UUID tetheredPlayer;
    public int cooldown;
    public boolean pendingRemoval;

    public AncientGuardianBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

    public boolean isPlayerTethered(PlayerEntity player) {
        return tetheredPlayer != null && tetheredPlayer.equals(player.getUuid());
    }

    public static ServerWorld getSpawnWorld(ServerPlayerEntity player) {
        if (player.getSpawnPointPosition() == null) {
            return null;
        }
        return player.server.getWorld(player.getSpawnPointDimension());
    }

    public static AncientGuardianBlockEntity getTether(ServerPlayerEntity player) {
        World world = getSpawnWorld(player);
        if (world != null && world.getBlockEntity(player.getSpawnPointPosition()) instanceof AncientGuardianBlockEntity blockEntity) {
            return blockEntity;
        }
        return null;
    }

    public static PacketByteBuf getBasePacket(Entity entity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        return buf;
    }

    public PacketByteBuf getActivationPacket(ServerPlayerEntity player) {
        PacketByteBuf buf = getBasePacket(player);
        buf.writeItemStack(getItem());
        return buf;
    }

    public static void sendActivation(ServerPlayerEntity player, PacketByteBuf base, PacketByteBuf activation, boolean item, boolean vacuous) {
        if (vacuous) {
            ServerPlayNetworking.send(player, AncientGuardian.VACUOUS_BLAST, base);
        }
        if (item) {
            ServerPlayNetworking.send(player, AncientGuardian.ANCIENT_GUARDIAN_ACTIVATED, activation);
        }
    }

    public static void sendActivationNearby(Entity entity, PacketByteBuf base, PacketByteBuf activation, boolean item, boolean vacuous) {
        for (ServerPlayerEntity p : PlayerLookup.tracking(entity)) {
            if (p != entity) {
                sendActivation(p, base, activation, item, vacuous);
            }
        }
    }

    public static DamageSource getDamageSource(PlayerEntity player) {
        return new EntityDamageSource("guardianWrath", player).setUsesMagic();
    }

    public static int damageAOE(ServerPlayerEntity player) {
        Box area = Box.of(player.getPos(), 10, 10, 10);
        int killed = 0;
        DamageSource damageSource = getDamageSource(player);
        for (Entity entity : player.world.getOtherEntities(player, area)) {
            if (entity instanceof LivingEntity livingEntity) {
                entity.damage(damageSource, 45.0f);
                if (livingEntity.isDead()) {
                    killed++;
                }
                else {
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 1200, 2), player);
                }
            }
        }
        return killed;
    }

    public boolean teleportPlayer(ServerPlayerEntity player) {
        boolean interdimensional = false;
        ServerWorld world = getSpawnWorld(player);
        if (player.getWorld() != world) {
            interdimensional = true;
        }
        RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, world, pos).ifPresent(pos ->
                player.teleport(world, pos.x, pos.y, pos.z, player.getYaw(), player.getPitch())
        );
        return interdimensional;
    }

    public void activate(ServerPlayerEntity player, int armorPieces) {
        cooldown = 6000 / getCore().type.modifier;
        player.setHealth(armorPieces * 0.25f * player.getMaxHealth());
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 60, 5));
        EncasedCore.Type coreType = getCore().type;
        boolean vacuous = coreType == EncasedCore.Type.VACUOUS;
        PacketByteBuf base = getBasePacket(player);
        PacketByteBuf activation = getActivationPacket(player);
        sendActivationNearby(player, base, activation, true, vacuous);
        player.playSound(SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0f, 0.75f);
        if (world != null) {
            world.playSound(player, player.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0f, 0.75f);
            int killed = 0;
            boolean interdimensional = false;
            if (coreType == EncasedCore.Type.VACUOUS) {
                killed = damageAOE(player);
                interdimensional = teleportPlayer(player);
            }
            sendActivation(player, base, activation, true, vacuous);
            BlockState newState = getCachedState().with(AncientGuardian.ON_COOLDOWN, true);
            if (damageCore(256, world.random)) {
                newState = newState.with(AncientMachine.FULL, false);
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.getChunkManager().markForUpdate(pos);
                }
            }
            world.setBlockState(pos, newState);
            ModCriteria.ANCIENT_GUARDIAN_USED.trigger(player, coreType, killed, interdimensional);
        }
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
    public void removeItem() {
        super.removeItem();
        pendingRemoval = true;
    }

    @Override
    public void removeCore(World world, PlayerEntity player, boolean remove, BlockPos pos, BlockState state) {
        super.removeCore(world, player, remove, pos, state);
        tetheredPlayer = null;
        playSound(SoundEvents.BLOCK_DEEPSLATE_STEP);
        world.setBlockState(pos, state.with(AncientMachine.FULL, false));
    }

    public ServerPlayerEntity getTetheredPlayer(World world) {
        MinecraftServer server = world.getServer();
        if (server == null) {
            return null;
        }
        return server.getPlayerManager().getPlayer(tetheredPlayer);
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, AncientGuardianBlockEntity blockEntity) {
        if (blockEntity.cooldown > 0) {
            blockEntity.cooldown--;
            if (!world.isClient() && blockEntity.cooldown == 0) {
                ServerPlayerEntity player = blockEntity.getTetheredPlayer(world);
                if (player != null) {
                    player.sendMessage(VOID_TETHER_RESTORED, false);
                }
                world.setBlockState(blockPos, state.with(AncientGuardian.ON_COOLDOWN, false));
            }
        }
        if (blockEntity.pendingRemoval) {
            blockEntity.removeItem();
            blockEntity.pendingRemoval = false;
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        tetheredPlayer = nbt.containsUuid("TetheredPlayer") ? nbt.getUuid("TetheredPlayer") : null;
        cooldown = nbt.getInt("Cooldown");
        pendingRemoval = nbt.getBoolean("PendingRemoval");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if (tetheredPlayer != null) {
            nbt.putUuid("TetheredPlayer", tetheredPlayer);
        }
        nbt.putInt("Cooldown", cooldown);
        nbt.putBoolean("PendingRemoval", pendingRemoval);
        super.writeNbt(nbt);
    }

    public static void register() {
        BLOCK_ENTITY_TYPE = build("ancient_guardian_block_entity", AncientGuardianBlockEntity::new, ModBlocks.ANCIENT_GUARDIAN);
    }
}
