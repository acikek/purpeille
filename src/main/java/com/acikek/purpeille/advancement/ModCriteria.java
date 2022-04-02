package com.acikek.purpeille.advancement;

import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;

public class ModCriteria {

    public static WarpathCreatedCriterion WARPATH_CREATED = new WarpathCreatedCriterion();

    public static void register() {
        CriterionRegistry.register(WARPATH_CREATED);
    }
}
