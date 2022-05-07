package com.acikek.purpeille.advancement;

import net.minecraft.advancement.criterion.Criteria;

public class ModCriteria {

    public static WarpathCreatedCriterion WARPATH_CREATED = new WarpathCreatedCriterion();
    public static AncientOvenDamagedCriterion ANCIENT_OVEN_DAMAGED = new AncientOvenDamagedCriterion();
    public static AncientGatewayUsedCriterion ANCIENT_GATEWAY_USED = new AncientGatewayUsedCriterion();

    public static void register() {
        Criteria.register(WARPATH_CREATED);
        Criteria.register(ANCIENT_OVEN_DAMAGED);
        Criteria.register(ANCIENT_GATEWAY_USED);
    }
}
