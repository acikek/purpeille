package com.acikek.purpeille.impl;

import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ComponentsImpl {

    public static Map<Identifier, Aspect> ASPECTS = new HashMap<>();
    public static Map<Identifier, Revelation> REVELATIONS = new HashMap<>();

    public static boolean isCompatible(Aspect aspect, Aspect other) {
        return other == null || aspect.whitelist == null || aspect.whitelist.contains(other.id);
    }

    public static boolean areCompatible(Aspect first, Aspect second) {
        return isCompatible(first, second) && (second == null || isCompatible(second, first));
    }
}
