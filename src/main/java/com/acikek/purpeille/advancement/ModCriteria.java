package com.acikek.purpeille.advancement;

import com.acikek.datacriteria.api.DataCriteriaAPI;
import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.UltravioletComplex;
import com.acikek.purpeille.block.entity.ancient.oven.Damage;
import com.acikek.purpeille.item.core.EncasedCore;
import com.acikek.purpeille.warpath.Synergy;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

public class ModCriteria {

    public static void triggerAbyssalAllegianceCycled(ServerPlayerEntity player, boolean passed, boolean passedPrevious) {
        DataCriteriaAPI.trigger(Purpeille.id("abyssal_allegiance_cycled"), true, player, passed, passedPrevious);
    }

    public static void triggerAbyssalTokenImbued(ServerPlayerEntity player, int energy, int altars) {
        DataCriteriaAPI.trigger(Purpeille.id("abyssal_token_imbued"), player, energy, altars);
    }

    public static void triggerAmalgamatedSpyglassUsed(ServerPlayerEntity player, ItemStack stack, boolean token) {
        DataCriteriaAPI.trigger(Purpeille.id("amalgamated_spyglass_used"), player, stack, token);
    }

    public static void triggerAncientGatewayUsed(ServerPlayerEntity player, int blocks) {
        DataCriteriaAPI.trigger(Purpeille.id("abyssal_allegiance_cycled"), player, blocks);
    }

    public static void triggerAncientGuardianUsed(ServerPlayerEntity player, EncasedCore.Type coreType, int killed, boolean interdimensional) {
        DataCriteriaAPI.trigger(Purpeille.id("ancient_guardian_used"), player, coreType, killed, interdimensional);
    }

    public static void triggerAncientOvenDamaged(ServerPlayerEntity player, Damage damage) {
        DataCriteriaAPI.trigger(Purpeille.id("ancient_oven_damaged"), player, damage);
    }

    public static void triggerChorusInfestationSheared(ServerPlayerEntity player, boolean dropChorus) {
        DataCriteriaAPI.trigger(Purpeille.id("chorus_infestation_sheared"), player, dropChorus);
    }

    public static void triggerUltravioletComplexBurns(ServerPlayerEntity player, UltravioletComplex.Type complexType, int lightLevel) {
        DataCriteriaAPI.trigger(Purpeille.id("ultraviolet_complex_burns"), player, complexType, lightLevel);
    }

    public static void triggerVoidSacrifice(ServerPlayerEntity player, int count) {
        DataCriteriaAPI.trigger(Purpeille.id("void_sacrifice"), player, count);
    }

    public static void triggerWarpathCreated(ServerPlayerEntity player, ItemStack item, Identifier revelation, Identifier aspect, Synergy synergy) {
        DataCriteriaAPI.trigger(Purpeille.id("warpath_created"), player, item, revelation, aspect, synergy);
    }

    public static void triggerWarpathUpgraded(ServerPlayerEntity player, int positive, int negative) {
        DataCriteriaAPI.trigger(Purpeille.id("abyssal_allegiance_cycled"), player, positive, negative);
    }

    public static <T extends Enum<T>> void registerEnum(String name, Class<T> type) {
        Registry.register(DataCriteriaAPI.getRegistry(), Purpeille.id(name), DataCriteriaAPI.createEnum(type));
    }

    public static void register() {
        registerEnum("encased_core_type", EncasedCore.Type.class);
        registerEnum("ancient_oven_damage", Damage.class);
        registerEnum("ultraviolet_complex_type", UltravioletComplex.Type.class);
        registerEnum("synergy", Synergy.class);
    }
}
