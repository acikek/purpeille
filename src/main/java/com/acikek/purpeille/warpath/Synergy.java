package com.acikek.purpeille.warpath;

import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;

public enum Synergy {

    OPPOSITION(-1.0),
    NEUTRAL(1.0),
    TONE(1.5),
    IDENTICAL(1.75);

    public double modifier;

    Synergy(double modifier) {
        this.modifier = modifier;
    }

    /**
     * Returns the synergy between two component instances.<br>
     * If the revelation's opposition matches the aspect's tone, returns {@link Synergy#OPPOSITION}.<br>
     * If both components' tones match, returns {@link Synergy#TONE}. If then the indices are the same, returns {@link Synergy#IDENTICAL}.<br>
     * Otherwise, returns {@link Synergy#NEUTRAL}.
     */
    public static Synergy getSynergy(Revelation revelation, Aspect aspect) {
        if (revelation.tone.getOpposition() == aspect.tone) {
            return OPPOSITION;
        }
        if (revelation.tone == aspect.tone) {
            return revelation.index == aspect.index ? IDENTICAL : TONE;
        }
        return NEUTRAL;
    }
}
