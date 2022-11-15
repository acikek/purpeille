package com.acikek.purpeille.impl;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.api.allegiance.AbyssallyAllegiantEntity;
import com.acikek.purpeille.api.allegiance.AllegianceData;
import com.acikek.purpeille.api.amsg.AncientMessageData;
import com.acikek.purpeille.api.amsg.AncientMessages;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class AbyssalAllegianceImpl implements AncientMessages.SeriesCompleted {

    public static final Identifier SERIES_ID = Purpeille.id("abyssal_allegiance");

    public static final Text CYCLE_FAIL = Text.translatable("message.purpeille.cycle_fail").formatted(Formatting.RED);

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
        //if (player instanceof AbyssallyAllegiantEntity allegiant) {
            // TODO send player ancient message about their tribute
            // if this is the first time in the cycle, only send them the message
            // note: probably need another bool in this interface
            // if they are part of the cycle, also check if they fulfilled demands
            // if not, do max health calculation thing or something (this should be a new potion effect right)
            // possibly also include it as a new hud component?
            // maybe in chat instead until we get a proper config?
        //}
        if (player instanceof AbyssallyAllegiantEntity allegiant) {
            AncientMessages.message(
                    List.of(player),
                    getCycleMessages(allegiant, random, !allegiant.getAllegianceData().passed()),
                    SERIES_ID
            );
        }
    }

    @Override
    public void onSeriesCompleted(ServerPlayerEntity player, Identifier seriesId) {
        if (seriesId.equals(SERIES_ID) && player instanceof AbyssallyAllegiantEntity allegiant) {
            player.sendMessage(Text.translatable("message.purpeille.cycle_next", allegiant.getAllegianceData().cyclic));
            if (!allegiant.getAllegianceData().passed()) {
                player.sendMessage(CYCLE_FAIL);
                player.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
                // TODO: inflict effect on player
            }
            allegiant.getAllegianceData().cycle();
        }
    }

    public static void register() {
        AncientMessages.SERIES_COMPLETED.register(new AbyssalAllegianceImpl());
    }
}
