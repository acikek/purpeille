package com.acikek.purpeille.advancement;

import com.acikek.purpeille.Purpeille;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class AbyssalAllegianceCycledCriterion extends AbstractCriterion<AbyssalAllegianceCycledCriterion.Conditions> {

    public static final Identifier ID = Purpeille.id("abyssal_allegiance_cycled");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        Boolean passed = JsonHelper.hasBoolean(obj, "passed") ? JsonHelper.getBoolean(obj, "passed") : null;
        Boolean passedPrevious = JsonHelper.hasBoolean(obj, "passed_previous") ? JsonHelper.getBoolean(obj, "passed_previous") : null;
        return new Conditions(playerPredicate, passed, passedPrevious);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, boolean passed, boolean passedPrevious) {
        trigger(player, conditions -> conditions.matches(passed, passedPrevious));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public Boolean passed;
        public Boolean passedPrevious;

        public Conditions(EntityPredicate.Extended entity, Boolean passed, Boolean passedPrevious) {
            super(ID, entity);
            this.passed = passed;
            this.passedPrevious = passedPrevious;
        }

        public boolean matches(boolean passed, boolean passedPrevious) {
            return (this.passed == null || this.passed == passed)
                    && (this.passedPrevious == null || this.passedPrevious == passedPrevious);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            if (passed != null) {
                obj.addProperty("passed", passed);
            }
            if (passedPrevious != null) {
                obj.addProperty("passed_previous", passedPrevious);
            }
            return obj;
        }
    }
}
