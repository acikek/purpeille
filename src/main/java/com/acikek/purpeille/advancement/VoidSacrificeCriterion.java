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

public class VoidSacrificeCriterion extends AbstractCriterion<VoidSacrificeCriterion.Conditions> {

    public static final Identifier ID = Purpeille.id("void_sacrifice");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        NumberRange.IntRange count = NumberRange.IntRange.fromJson(obj.get("count"));
        return new Conditions(playerPredicate, count);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, int count) {
        trigger(player, conditions -> conditions.matches(count));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public NumberRange.IntRange count;

        public Conditions(EntityPredicate.Extended entity, NumberRange.IntRange count) {
            super(ID, entity);
            this.count = count;
        }

        public boolean matches(int count) {
            return this.count.test(count);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            obj.add("count", count.toJson());
            return obj;
        }
    }
}
