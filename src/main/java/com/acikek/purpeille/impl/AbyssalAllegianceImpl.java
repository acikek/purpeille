package com.acikek.purpeille.impl;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.advancement.ModCriteria;
import com.acikek.purpeille.api.allegiance.AbyssallyAllegiantEntity;
import com.acikek.purpeille.api.allegiance.AllegianceData;
import com.acikek.purpeille.api.amsg.AncientMessageData;
import com.acikek.purpeille.api.amsg.AncientMessages;
import com.acikek.purpeille.tag.ModTags;
import com.acikek.voidcrafting.api.VoidCraftingAPI;
import com.acikek.voidcrafting.api.event.StackVoided;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbyssalAllegianceImpl implements AncientMessages.SeriesCompleted, ServerPlayConnectionEvents.Join, StackVoided {

    public static final Identifier ID = Purpeille.id("abyssal_allegiance");
    public static final Identifier VOIDED_SERIES = Purpeille.id("abyssal_allegiance_voided");

    public static final Text CYCLE_FAIL = Text.translatable("message.purpeille.cycle_fail").formatted(Formatting.RED);

    public static final UUID TREASONOUS_UUID = UUID.fromString("ee8ced72-9bcc-4892-8e9c-de4dcc2f23de");

    public static AncientMessageData.Builder getSuccessMessage(int n) {
        return AncientMessages.singleLang("amsg.purpeille.success_" + n).color(0x22E68E);
    }

    public static AncientMessageData.Builder getFailureMessage(int n) {
        return AncientMessages.singleLang("amsg.purpeille.failure_" + n).color(0xD90936);
    }

    public static List<AncientMessageData> getCycleMessages(AbyssallyAllegiantEntity allegiant, Random random, boolean failed) {
        AllegianceData data = allegiant.getAllegianceData();
        List<AncientMessageData> result = new ArrayList<>(List.of(
                AncientMessages.singleLang("amsg.purpeille.cycle_" + (random.nextInt(3) + 1))
                        .soundEvent(SoundEvents.ENTITY_ENDER_DRAGON_GROWL)
                        .build(),
                AncientMessages.singleLang("amsg.purpeille.payment", data.cyclic).build()
        ));
        if (failed) {
            result.add(getFailureMessage(1).build());
            result.add(getFailureMessage(2).soundEvent(SoundEvents.ENTITY_WITHER_DEATH).build());
            result.add(getFailureMessage(3).soundEvent(SoundEvents.ENTITY_WITHER_AMBIENT).build());
        }
        else if (!data.passedLast) {
            result.add(getSuccessMessage(3).build());
            result.add(getSuccessMessage(4).soundEvent(SoundEvents.AMBIENT_NETHER_WASTES_MOOD).build());
        }
        return result;
    }

    public static void cycle(ServerPlayerEntity player, Random random) {
        if (player instanceof AbyssallyAllegiantEntity allegiant) {
            EntityAttributeInstance instance = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if (instance != null) {
                instance.removeModifier(TREASONOUS_UUID);
            }
            AncientMessages.message(
                    List.of(player),
                    getCycleMessages(allegiant, random, !allegiant.getAllegianceData().passed()),
                    ID
            );
        }
    }

    public static EntityAttributeModifier getTreasonousModifier(int neglected) {
        return new EntityAttributeModifier(
                TREASONOUS_UUID,
                "Treasonous",
                -4.0 * MathHelper.clamp(neglected / 3, 1, 3),
                EntityAttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public void onSeriesCompleted(ServerPlayerEntity player, Identifier seriesId) {
        if (player instanceof AbyssallyAllegiantEntity allegiant) {
            AllegianceData data = allegiant.getAllegianceData();
            if (seriesId.equals(ID)) {
                player.sendMessage(Text.translatable("message.purpeille.cycle_next", data.cyclic));
                if (!data.passed()) {
                    player.sendMessage(CYCLE_FAIL);
                    player.playSound(SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    EntityAttributeInstance instance = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                    if (instance != null) {
                        instance.addPersistentModifier(getTreasonousModifier(data.neglected()));
                    }
                }
                ModCriteria.triggerAbyssalAllegianceCycled(player, data.passed(), data.passedLast);
                data.cycle();
            }
            else if (seriesId.equals(VOIDED_SERIES)) {
                ModCriteria.triggerVoidSacrifice(player, data.cyclic);
            }
        }
    }

    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        if (handler.player instanceof AbyssallyAllegiantEntity allegiant && allegiant.getAllegianceData().initialTime != 0L) {
            if (server.getSaveProperties() instanceof LevelProperties levelProperties
                    && levelProperties.getTime() - allegiant.getAllegianceData().initialTime >= 168000L) {
                cycle(handler.player, handler.player.world.random);
            }
        }
    }

    @Override
    public void onStackVoided(World world, ItemEntity itemEntity, ItemStack itemStack, PlayerEntity thrower) {
        if (thrower instanceof ServerPlayerEntity serverPlayer
                && serverPlayer instanceof AbyssallyAllegiantEntity allegiant
                && itemStack.isIn(ModTags.ABYSSAL_TRIBUTE)) {
            AllegianceData data = allegiant.getAllegianceData();
            if (data.cyclic == 0) {
                return;
            }
            data.fulfilled += itemStack.getCount();
            if (data.fulfilled >= data.cyclic) {
                AncientMessages.message(
                        List.of(serverPlayer),
                        List.of(getSuccessMessage(world.random.nextInt(2) + 1)
                                .soundEvent(SoundEvents.ENTITY_ENDER_DRAGON_GROWL)
                                .build()),
                        VOIDED_SERIES
                );
            }
        }
    }

    public static void register() {
        AbyssalAllegianceImpl obj = new AbyssalAllegianceImpl();
        AncientMessages.SERIES_COMPLETED.register(obj);
        ServerPlayConnectionEvents.JOIN.register(obj);
        VoidCraftingAPI.STACK_VOIDED.register(ID, obj);
        VoidCraftingAPI.addStackVoidedPhase(ID);
    }

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("tribute")
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(context -> {
                            for (ServerPlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
                                cycle(player, context.getSource().getWorld().random);
                            }
                            return 0;
                        }))
                .requires(source -> source.hasPermissionLevel(4)));
    }
}
