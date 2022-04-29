package com.acikek.purpeille.warpath;

import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ClampedColor {

    public static final int THRESHOLD = 50;
    public static final int COLOR_MAX = 255;
    public static final long INTERVAL = 80L;
    public static final float INTERVAL_F = (float) INTERVAL;

    public Formatting formatting;
    public int colorValue;
    public Value r;
    public Value g;
    public Value b;

    public ClampedColor(Formatting formatting) {
        this.formatting = formatting;
        colorValue = getColorValue();
        r = new Value((colorValue & 0xFF0000) >> 16);
        g = new Value((colorValue & 0x00FF00) >> 8);
        b = new Value(colorValue & 0x0000FF);
    }

    public int getColorValue() {
        Integer value = formatting.getColorValue();
        return value != null ? value : 0xFFFFFF;
    }

    public int getModified(int wave) {
        return MathHelper.packRgb(r.clamp(wave), g.clamp(wave), b.clamp(wave));
    }

    public static int getWave(World world) {
        return (int) (MathHelper.sin(((world.getTime() % INTERVAL) / INTERVAL_F) * MathHelper.TAU) * (float) THRESHOLD);
    }

    public static class Value {

        public int value;
        public int min;
        public int max;

        public Value(int value) {
            this.value = value;
            min = value - THRESHOLD;
            max = (value + THRESHOLD) - COLOR_MAX;
        }

        public int clamp(int wave) {
            int result = value + wave;
            if (min < 0) {
                result -= min;
            }
            else if (max > 0) {
                result -= max;
            }
            return result;
        }
    }
}
