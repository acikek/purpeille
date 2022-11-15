package com.acikek.purpeille.api.allegiance;

import com.acikek.purpeille.impl.AbyssalAllegianceImpl;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;

public class AbyssalAllegiance {

    public static void cycle(ServerPlayerEntity player, Random random) {
        AbyssalAllegianceImpl.cycle(player, random);
    }
}
