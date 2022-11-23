package com.acikek.purpeille.advancement;

import net.minecraft.advancement.criterion.Criteria;

public class ModCriteria {

    public static AbyssalAllegianceCycledCriterion ABYSSAL_ALLEGIANCE_CYCLED = new AbyssalAllegianceCycledCriterion();
    public static AbyssalTokenImbuedCriterion ABYSSAL_TOKEN_IMBUED = new AbyssalTokenImbuedCriterion();
    public static AncientGatewayUsedCriterion ANCIENT_GATEWAY_USED = new AncientGatewayUsedCriterion();
    public static AncientGuardianUsedCriterion ANCIENT_GUARDIAN_USED = new AncientGuardianUsedCriterion();
    public static AncientOvenDamagedCriterion ANCIENT_OVEN_DAMAGED = new AncientOvenDamagedCriterion();
    public static ChorusInfestationSheared CHORUS_INFESTATION_SHEARED = new ChorusInfestationSheared();
    public static UltravioletComplexBurnsCriterion ULTRAVIOLET_COMPLEX_BURNS = new UltravioletComplexBurnsCriterion();
    public static VoidSacrificeCriterion VOID_SACRIFICE = new VoidSacrificeCriterion();
    public static WarpathCreatedCriterion WARPATH_CREATED = new WarpathCreatedCriterion();
    public static WarpathUpgradedCriterion WARPATH_UPGRADED = new WarpathUpgradedCriterion();

    public static void register() {
        Criteria.register(ABYSSAL_ALLEGIANCE_CYCLED);
        Criteria.register(ABYSSAL_TOKEN_IMBUED);
        Criteria.register(ANCIENT_GATEWAY_USED);
        Criteria.register(ANCIENT_GUARDIAN_USED);
        Criteria.register(ANCIENT_OVEN_DAMAGED);
        Criteria.register(CHORUS_INFESTATION_SHEARED);
        Criteria.register(ULTRAVIOLET_COMPLEX_BURNS);
        Criteria.register(VOID_SACRIFICE);
        Criteria.register(WARPATH_CREATED);
        Criteria.register(WARPATH_UPGRADED);
    }
}
