package com.acikek.purpeille.impl;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.api.allegiance.AbyssallyAllegiantEntity;
import com.acikek.purpeille.api.allegiance.AllegianceData;
import com.acikek.purpeille.api.amsg.AncientMessageData;
import com.acikek.purpeille.api.amsg.AncientMessages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbyssalAllegianceImpl implements AncientMessages.SeriesCompleted {

    public static final Identifier SERIES_ID = Purpeille.id("abyssal_allegiance");

    public static final Text CYCLE_FAIL = Text.translatable("message.purpeille.cycle_fail").formatted(Formatting.RED);

    public static final UUID TREASONOUS_UUID = UUID.fromString("ee8ced72-9bcc-4892-8e9c-de4dcc2f23de");

    public static AncientMessageData.Builder getFailureMessage(int n) {
        return AncientMessages.singleLang("amsg.purpeille.failure_" + n).color(0xFF0000);
    }

    public static List<AncientMessageData> getCycleMessages(AbyssallyAllegiantEntity allegiant, Random random, boolean failedLast) {
        AllegianceData data = allegiant.getAllegianceData();
        List<AncientMessageData> result = new ArrayList<>(List.of(
                AncientMessages.singleLang("amsg.purpeille.cycle_" + (random.nextInt(3) + 1))
                        .soundEvent(SoundEvents.ENTITY_ENDER_DRAGON_GROWL)
                        .build(),
                AncientMessages.singleLang("amsg.purpeille.payment", data.cyclic).build()
        ));
        if (failedLast) {
            result.add(getFailureMessage(1).build());
            result.add(getFailureMessage(2).soundEvent(SoundEvents.ENTITY_WITHER_DEATH).build());
            result.add(getFailureMessage(3).soundEvent(SoundEvents.ENTITY_WITHER_AMBIENT).build());
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
                    SERIES_ID
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
        if (seriesId.equals(SERIES_ID) && player instanceof AbyssallyAllegiantEntity allegiant) {
            player.sendMessage(Text.translatable("message.purpeille.cycle_next", allegiant.getAllegianceData().cyclic));
            if (!allegiant.getAllegianceData().passed()) {
                player.sendMessage(CYCLE_FAIL);
                player.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
                EntityAttributeInstance instance = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                if (instance != null) {
                    instance.addPersistentModifier(getTreasonousModifier(allegiant.getAllegianceData().neglected()));
                }
            }
            allegiant.getAllegianceData().cycle();
        }
    }

    public static void register() {
        AncientMessages.SERIES_COMPLETED.register(new AbyssalAllegianceImpl());
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
