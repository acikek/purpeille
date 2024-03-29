package com.acikek.purpeille.warpath;

import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.EnumUtils;

import java.util.HashMap;
import java.util.Map;

public enum Synergy {

    OPPOSITION(-1.0),
    NEUTRAL(1.0),
    TONE(1.5),
    IDENTICAL(1.75);

    public final double modifier;

    Synergy(double modifier) {
        this.modifier = modifier;
    }

    /**
     * Returns the synergy between two component instances.<br>
     * If the revelation's synergy overrides contain the aspect ID, returns the corresponding synergy.<br>
     * If the revelation's opposition matches the aspect's tone, returns {@link Synergy#OPPOSITION}.<br>
     * If both components' tones match, returns {@link Synergy#TONE}.
     * Otherwise, returns {@link Synergy#NEUTRAL}.
     */
    public static Synergy getSynergy(Revelation revelation, Aspect aspect) {
        if (aspect == null) {
            return NEUTRAL;
        }
        if (revelation.synergy != null && revelation.synergy.containsKey(aspect.id)) {
            return revelation.synergy.get(aspect.id);
        }
        if (revelation.tone.getOpposition() == aspect.tone) {
            return OPPOSITION;
        }
        if (revelation.tone == aspect.tone) {
            return TONE;
        }
        return NEUTRAL;
    }

    public static Synergy read(PacketByteBuf buf) {
        return buf.readEnumConstant(Synergy.class);
    }

    public static void write(PacketByteBuf buf, Synergy synergy) {
        buf.writeEnumConstant(synergy);
    }

    public static Map<Identifier, Synergy> overridesFromJson(JsonObject obj) {
        if (obj == null) {
            return null;
        }
        Map<Identifier, Synergy> result = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            Identifier id = Identifier.tryParse(entry.getKey());
            Synergy synergy = EnumUtils.getEnumIgnoreCase(Synergy.class, entry.getValue().getAsString());
            result.put(id, synergy);
        }
        return result;
    }
}
