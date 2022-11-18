package com.acikek.purpeille.api.allegiance;

import net.minecraft.nbt.NbtCompound;

public class AllegianceData {

    public static final String KEY = "AbyssalAllegiance";

    public int cyclic;
    public int previous;
    public int fulfilled;
    public long initialTime;
    public boolean failedLast;

    public AllegianceData(int cyclic, int previous, int fulfilled, long initialTime, boolean failedLast) {
        this.cyclic = cyclic;
        this.previous = previous;
        this.fulfilled = fulfilled;
        this.initialTime = initialTime;
        this.failedLast = failedLast;
    }

    public int neglected() {
        return previous - fulfilled;
    }

    public boolean passed() {
        return neglected() <= 0;
    }

    public void cycle() {
        failedLast = !passed();
        previous = cyclic;
        cyclic = 0;
        fulfilled = 0;
        initialTime = 0L;
    }

    public static AllegianceData readNbt(NbtCompound nbt) {
        int cyclic = nbt.getInt("Cyclic");
        int previous = nbt.getInt("Previous");
        int fulfilled = nbt.getInt("Fulfilled");
        long initialTime = nbt.getLong("InitialTime");
        boolean failedLast = nbt.getBoolean("FailedLast");
        return new AllegianceData(cyclic, previous, fulfilled, initialTime, failedLast);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("Cyclic", cyclic);
        nbt.putInt("Previous", previous);
        nbt.putInt("Fulfilled", fulfilled);
        nbt.putLong("InitialTime", initialTime);
        nbt.putBoolean("FailedLast", failedLast);
    }
}
