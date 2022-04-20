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

public class AncientGatewayUsedCriterion extends AbstractCriterion<AncientGatewayUsedCriterion.Conditions> {

    public static Identifier ID = Purpeille.id("ancient_gateway_used");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        NumberRange.IntRange blocks = NumberRange.IntRange.fromJson(obj.get("blocks"));
        return new Conditions(playerPredicate, blocks);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, int blocks) {
        trigger(player, conditions -> conditions.matches(blocks));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public NumberRange.IntRange blocks;

        public Conditions(EntityPredicate.Extended playerPredicate, NumberRange.IntRange blocks) {
            super(ID, playerPredicate);
            this.blocks = blocks;
        }

        public boolean matches(int blocks) {
            return this.blocks.test(blocks);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            obj.add("blocks", blocks.toJson());
            return obj;
        }
    }
}
