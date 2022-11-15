package com.acikek.purpeille.api.amsg;

import com.acikek.purpeille.impl.AncientMessagesImpl;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;

public class AncientMessages {

    public static final Event<SeriesCompleted> SERIES_COMPLETED = EventFactory.createArrayBacked(
            SeriesCompleted.class,
            listeners -> (player, seriesId) -> {
                for (SeriesCompleted listener : listeners) {
                    listener.onSeriesCompleted(player, seriesId);
                }
            });

    public static Identifier getChannel() {
        return AncientMessagesImpl.CHANNEL;
    }

    public static Identifier getCompletedChannel() {
        return AncientMessagesImpl.COMPLETED_CHANNEL;
    }

    public static void message(Collection<ServerPlayerEntity> players, List<AncientMessageData> list, Identifier seriesId) {
        AncientMessagesImpl.message(players, list, seriesId);
    }

    public static AncientMessageData.Builder singleLang(String key, Object... args) {
        return AncientMessagesImpl.singleLang(key, args);
    }

    public static AncientMessageData.Builder singleLang(String key) {
        return AncientMessagesImpl.singleLang(key);
    }

    @FunctionalInterface
    public interface SeriesCompleted {
        void onSeriesCompleted(ServerPlayerEntity player, Identifier seriesId);
    }
}
