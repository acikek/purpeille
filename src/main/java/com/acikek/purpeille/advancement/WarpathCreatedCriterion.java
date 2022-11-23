package com.acikek.purpeille.advancement;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.warpath.Synergy;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lib.EnumPredicate;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class WarpathCreatedCriterion extends AbstractCriterion<WarpathCreatedCriterion.Conditions> {

    public static Identifier ID = Purpeille.id("warpath_created");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        ItemPredicate item = ItemPredicate.fromJson(obj.get("item"));
        Identifier revelation = obj.has("revelation") ? Identifier.tryParse(obj.get("revelation").getAsString()) : null;
        Identifier aspect = obj.has("aspect") ? Identifier.tryParse(obj.get("aspect").getAsString()) : null;
        EnumPredicate<Synergy> synergy = EnumPredicate.fromJson(obj.get("synergy"), Synergy::valueOf);
        return new Conditions(playerPredicate, item, revelation, aspect, synergy);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack, Revelation revelation, Aspect aspect, Synergy synergy) {
        trigger(player, conditions -> conditions.matches(stack, revelation, aspect, synergy));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public ItemPredicate item;
        public Identifier revelation;
        public Identifier aspect;
        public EnumPredicate<Synergy> synergy;

        public Conditions(EntityPredicate.Extended playerPredicate, ItemPredicate item, Identifier revelation, Identifier aspect, EnumPredicate<Synergy> synergy) {
            super(ID, playerPredicate);
            this.item = item;
            this.revelation = revelation;
            this.aspect = aspect;
            this.synergy = synergy;
        }

        public boolean matches(ItemStack stack, Revelation revelation, Aspect aspect, Synergy synergy) {
            return this.item.test(stack)
                    && (this.revelation == null || this.revelation.equals(revelation.id))
                    && (this.aspect == null || this.aspect.equals(aspect.id))
                    && this.synergy.test(synergy);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            obj.add("item", item.toJson());
            if (revelation != null) {
                obj.addProperty("revelation", revelation.toString());
            }
            if (aspect != null) {
                obj.addProperty("aspect", aspect.toString());
            }
            obj.add("synergy", synergy.toJson());
            return obj;
        }
    }
}
