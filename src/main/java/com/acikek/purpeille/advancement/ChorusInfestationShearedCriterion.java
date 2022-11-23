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

public class ChorusInfestationShearedCriterion extends AbstractCriterion<ChorusInfestationShearedCriterion.Conditions> {

    public static Identifier ID = Purpeille.id("chorus_infestation_sheared");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        Boolean dropChorus = JsonHelper.hasBoolean(obj, "drop_chorus") ? JsonHelper.getBoolean(obj, "drop_chorus") : null;
        return new Conditions(playerPredicate, dropChorus);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, boolean dropChorus) {
        trigger(player, conditions -> conditions.matches(dropChorus));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public Boolean dropChorus;

        public Conditions(EntityPredicate.Extended playerPredicate, Boolean dropChorus) {
            super(ID, playerPredicate);
            this.dropChorus = dropChorus;
        }

        public boolean matches(boolean dropChorus) {
            return this.dropChorus == null || this.dropChorus == dropChorus;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            if (dropChorus != null) {
                obj.addProperty("drop_chorus", dropChorus);
            }
            return obj;
        }
    }
}
