package com.acikek.purpeille.warpath;

public enum Tone {

    STRENGTH(0),
    TENSION(1),
    RELEASE(2);

    public int index;

    Tone(int index) {
        this.index = index;
    }

    public Tone getOpposition() {
        return switch (this) {
            case STRENGTH -> TENSION;
            case TENSION -> RELEASE;
            case RELEASE -> STRENGTH;
        };
    }
}
