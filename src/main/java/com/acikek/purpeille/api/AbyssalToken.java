package com.acikek.purpeille.api;

import com.acikek.purpeille.warpath.component.Revelation;

import java.util.HashSet;
import java.util.Set;

public interface AbyssalToken {

    int MAX_ENERGY = 100;
    String ENERGY_KEY = "SpiritualEnergy";

    Set<AbyssalToken> TOKENS = new HashSet<>();

    static void clearTokens() {
        for (AbyssalToken token : TOKENS) {
            token.setRevelation(null);
        }
        TOKENS.clear();
    }

    Revelation getRevelation();

    void setRevelation(Revelation revelation);

    default boolean hasRevelation() {
        return getRevelation() != null;
    }
}
