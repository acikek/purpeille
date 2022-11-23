package com.acikek.purpeille.advancement;

import com.acikek.purpeille.Purpeille;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AbyssalTokenImbuedCriterion extends AbstractCriterion<AbyssalTokenImbuedCriterion.Conditions> {

    public static Identifier ID = Purpeille.id("abyssal_token_imbued");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        NumberRange.IntRange energy = NumberRange.IntRange.fromJson(obj.get("energy"));
        NumberRange.IntRange altars = NumberRange.IntRange.fromJson(obj.get("altars"));
        return new Conditions(playerPredicate, energy, altars);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, int energy, int altars) {
        trigger(player, conditions -> conditions.matches(energy, altars));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public NumberRange.IntRange energy;
        public NumberRange.IntRange altars;

        public Conditions(EntityPredicate.Extended playerPredicate, NumberRange.IntRange energy, NumberRange.IntRange altars) {
            super(ID, playerPredicate);
            this.energy = energy;
            this.altars = altars;
        }

        public boolean matches(int energy, int altars) {
            return this.energy.test(energy)
                    && this.altars.test(altars);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            obj.add("energy", energy.toJson());
            obj.add("altars", altars.toJson());
            return obj;
        }
    }
}
