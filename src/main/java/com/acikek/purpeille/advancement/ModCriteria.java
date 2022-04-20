package com.acikek.purpeille.advancement;

import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;

public class ModCriteria {

    public static WarpathCreatedCriterion WARPATH_CREATED = new WarpathCreatedCriterion();
    public static AncientOvenDamagedCriterion ANCIENT_OVEN_DAMAGED = new AncientOvenDamagedCriterion();
    public static AncientGatewayUsedCriterion ANCIENT_GATEWAY_USED = new AncientGatewayUsedCriterion();

    public static void register() {
        CriterionRegistry.register(WARPATH_CREATED);
        CriterionRegistry.register(ANCIENT_OVEN_DAMAGED);
        CriterionRegistry.register(ANCIENT_GATEWAY_USED);
    }
}
