package com.acikek.purpeille.warpath;

import com.acikek.purpeille.api.warpath.Components;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class WarpathData {

    public static final String KEY = "WarpathData";

    public Identifier revelation;
    public Identifier aspect;
    public Identifier appliedToken;

    public WarpathData(Identifier revelation, Identifier aspect, Identifier appliedToken) {
        this.revelation = revelation;
        this.aspect = aspect;
        this.appliedToken = appliedToken;
    }

    public Revelation getRevelation() {
        return Components.getRevelations().get(revelation);
    }

    public Aspect getAspect() {
        return aspect != null ? Components.getAspects().get(aspect) : null;
    }

    public static WarpathData fromNbt(NbtCompound nbt) {
        return new WarpathData(
                Identifier.tryParse(nbt.getString("Revelation")),
                nbt.contains("Aspect") ? Identifier.tryParse(nbt.getString("Aspect")) : null,
                nbt.contains("AppliedToken") ? Identifier.tryParse(nbt.getString("AppliedToken")) : null
        );
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putString("Revelation", revelation.toString());
        if (aspect != null) {
            nbt.putString("Aspect", aspect.toString());
        }
        if (appliedToken != null) {
            nbt.putString("AppliedToken", appliedToken.toString());
        }
    }
}
