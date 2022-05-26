package com.acikek.purpeille.advancement;

import net.minecraft.advancement.criterion.Criteria;

public class ModCriteria {

    public static AncientGatewayUsedCriterion ANCIENT_GATEWAY_USED = new AncientGatewayUsedCriterion();
    public static AncientGuardianUsedCriterion ANCIENT_GUARDIAN_USED = new AncientGuardianUsedCriterion();
    public static AncientOvenDamagedCriterion ANCIENT_OVEN_DAMAGED = new AncientOvenDamagedCriterion();
    public static WarpathCreatedCriterion WARPATH_CREATED = new WarpathCreatedCriterion();

    public static void register() {
        Criteria.register(ANCIENT_GATEWAY_USED);
        Criteria.register(ANCIENT_GUARDIAN_USED);
        Criteria.register(ANCIENT_OVEN_DAMAGED);
        Criteria.register(WARPATH_CREATED);
    }
}
