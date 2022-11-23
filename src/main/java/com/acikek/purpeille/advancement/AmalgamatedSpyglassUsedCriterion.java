package com.acikek.purpeille.advancement;

import com.acikek.purpeille.Purpeille;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class AmalgamatedSpyglassUsedCriterion extends AbstractCriterion<AmalgamatedSpyglassUsedCriterion.Conditions> {

    public static final Identifier ID = Purpeille.id("amalgamated_spyglass_used");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        ItemPredicate item = ItemPredicate.fromJson(obj.get("item"));
        Boolean token = JsonHelper.hasBoolean(obj, "token") ? JsonHelper.getBoolean(obj, "token") : null;
        return new Conditions(playerPredicate, item, token);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack, boolean token) {
        trigger(player, conditions -> conditions.matches(stack, token));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public ItemPredicate item;
        public Boolean token;

        public Conditions(EntityPredicate.Extended entity, ItemPredicate item, Boolean token) {
            super(ID, entity);
            this.item = item;
            this.token = token;
        }

        public boolean matches(ItemStack stack, boolean token) {
            return item.test(stack)
                    && (this.token == null || this.token == token);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            obj.add("item", item.toJson());
            if (token != null) {
                obj.addProperty("token", token);
            }
            return obj;
        }
    }
}
