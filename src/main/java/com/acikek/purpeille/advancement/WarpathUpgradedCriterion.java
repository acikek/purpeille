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

public class WarpathUpgradedCriterion extends AbstractCriterion<WarpathUpgradedCriterion.Conditions> {

    public static Identifier ID = Purpeille.id("warpath_upgraded");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        NumberRange.IntRange positive = NumberRange.IntRange.fromJson(obj.get("positive"));
        NumberRange.IntRange negative = NumberRange.IntRange.fromJson(obj.get("negative"));
        return new Conditions(playerPredicate, positive, negative);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, int positive, int negative) {
        trigger(player, conditions -> conditions.matches(positive, negative));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public NumberRange.IntRange positive;
        public NumberRange.IntRange negative;

        public Conditions(EntityPredicate.Extended playerPredicate, NumberRange.IntRange positive, NumberRange.IntRange negative) {
            super(ID, playerPredicate);
            this.positive = positive;
            this.negative = negative;
        }

        public boolean matches(int energy, int altars) {
            return this.positive.test(energy)
                    && this.negative.test(altars);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            obj.add("positive", positive.toJson());
            obj.add("negative", negative.toJson());
            return obj;
        }
    }
}
