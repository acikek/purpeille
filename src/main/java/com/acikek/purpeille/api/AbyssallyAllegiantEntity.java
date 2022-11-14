package com.acikek.purpeille.api;

import net.minecraft.server.network.ServerPlayerEntity;

public interface AbyssallyAllegiantEntity {

    int getCyclicAllegiance();

    long getLastAllegiantTime();

    void setLastAllegiantTime(long time);

    int getFulfilledAllegiance();

    void setFulfilledAllegiance(int allegiance);

    default void reset() {
        setLastAllegiantTime(0L);
        setFulfilledAllegiance(0);
    }

    static void cycle(ServerPlayerEntity player) {
        if (player instanceof AbyssallyAllegiantEntity allegiant) {
            // TODO send player ancient message about their tribute
            // if this is the first time in the cycle, only send them the message
            // note: probably need another bool in this interface
            // if they are part of the cycle, also check if they fulfilled demands
              // if not, do max health calculation thing or something (this should be a new potion effect right)
            // possibly also include it as a new hud component?
            // maybe in chat instead until we get a proper config?
        }
    }
}
