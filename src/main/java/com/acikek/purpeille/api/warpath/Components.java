package com.acikek.purpeille.api.warpath;

import com.acikek.purpeille.impl.ComponentsImpl;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.util.Identifier;

import java.util.Map;

public class Components {

    public static Map<Identifier, Aspect> getAspects() {
        return ComponentsImpl.ASPECTS;
    }

    public static Map<Identifier, Revelation> getRevelations() {
        return ComponentsImpl.REVELATIONS;
    }

    public static boolean isCompatible(Aspect aspect, Aspect other) {
        return ComponentsImpl.isCompatible(aspect, other);
    }

    public static boolean areCompatible(Aspect first, Aspect second) {
        return ComponentsImpl.areCompatible(first, second);
    }
}
