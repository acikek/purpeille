package com.acikek.purpeille.advancement;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.entity.ancient.oven.Damage;
import com.google.gson.JsonObject;
import lib.EnumPredicate;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AncientOvenDamagedCriterion extends AbstractCriterion<AncientOvenDamagedCriterion.Conditions> {

    public static Identifier ID = Purpeille.id("ancient_oven_damaged");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        EnumPredicate<Damage> damage = EnumPredicate.fromJson(obj.get("damage"), Damage::valueOf);
        return new Conditions(playerPredicate, damage);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, Damage damage) {
        trigger(player, conditions -> conditions.matches(damage));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public EnumPredicate<Damage> damage;

        public Conditions(EntityPredicate.Extended playerPredicate, EnumPredicate<Damage> damage) {
            super(ID, playerPredicate);
            this.damage = damage;
        }

        public boolean matches(Damage damage) {
            return this.damage.test(damage);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            obj.add("damage", damage.toJson());
            return obj;
        }
    }
}
