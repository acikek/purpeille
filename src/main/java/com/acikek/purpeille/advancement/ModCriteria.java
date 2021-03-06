package com.acikek.purpeille.advancement;

import net.minecraft.advancement.criterion.Criteria;

public class ModCriteria {

    public static AncientGatewayUsedCriterion ANCIENT_GATEWAY_USED = new AncientGatewayUsedCriterion();
    public static AncientGuardianUsedCriterion ANCIENT_GUARDIAN_USED = new AncientGuardianUsedCriterion();
    public static AncientOvenDamagedCriterion ANCIENT_OVEN_DAMAGED = new AncientOvenDamagedCriterion();
    public static ChorusInfestationSheared CHORUS_INFESTATION_SHEARED = new ChorusInfestationSheared();
    public static UltravioletComplexBurnsCriterion ULTRAVIOLET_COMPLEX_BURNS = new UltravioletComplexBurnsCriterion();
    public static WarpathCreatedCriterion WARPATH_CREATED = new WarpathCreatedCriterion();

    public static void register() {
        Criteria.register(ANCIENT_GATEWAY_USED);
        Criteria.register(ANCIENT_GUARDIAN_USED);
        Criteria.register(ANCIENT_OVEN_DAMAGED);
        Criteria.register(CHORUS_INFESTATION_SHEARED);
        Criteria.register(ULTRAVIOLET_COMPLEX_BURNS);
        Criteria.register(WARPATH_CREATED);
    }
}
