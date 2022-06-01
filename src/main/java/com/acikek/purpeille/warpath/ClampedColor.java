package com.acikek.purpeille.warpath;

import com.google.gson.JsonElement;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public class ClampedColor {

    public static final int THRESHOLD = 50;
    public static final int COLOR_MAX = 255;
    public static final long INTERVAL = 4000L;
    public static final float INTERVAL_F = (float) INTERVAL;
    public int colorValue;
    public Value r;
    public Value g;
    public Value b;

    public ClampedColor(int colorValue) {
        this.colorValue = colorValue;
        r = new Value((colorValue & 0xFF0000) >> 16);
        g = new Value((colorValue & 0x00FF00) >> 8);
        b = new Value(colorValue & 0x0000FF);
    }

    public ClampedColor(Formatting formatting) {
        this(getColorValue(formatting));
    }

    public static int getColorValue(Formatting formatting) {
        Integer value = formatting.getColorValue();
        return value != null ? value : 0xFFFFFF;
    }

    public int getModified(int wave) {
        return MathHelper.packRgb(r.clamp(wave), g.clamp(wave), b.clamp(wave));
    }

    public static int getWave() {
        float progress = (System.currentTimeMillis() % INTERVAL) / INTERVAL_F;
        return (int) (MathHelper.sin(progress * MathHelper.TAU) * (float) THRESHOLD);
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

    public static int colorFromJson(JsonElement element) {
        if (JsonHelper.isNumber(element)) {
            return element.getAsInt();
        }
        Formatting formatting = Formatting.byName(element.getAsString());
        if (formatting == null) {
            throw new IllegalStateException("'" + element.getAsString() + "' is not a valid color");
        }
        return formatting.getColorValue() != null ? formatting.getColorValue() : -1;
    }
}
