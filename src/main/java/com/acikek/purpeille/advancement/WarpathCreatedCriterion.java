package com.acikek.purpeille.advancement;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.warpath.Aspect;
import com.acikek.purpeille.warpath.Revelation;
import com.acikek.purpeille.warpath.Synergy;
import com.google.gson.JsonObject;
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
        EnumPredicate<Revelation> revelation = EnumPredicate.fromJson(obj.get("revelation"), Revelation::valueOf);
        EnumPredicate<Aspect> aspect = EnumPredicate.fromJson(obj.get("aspect"), Aspect::valueOf);
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
        public EnumPredicate<Revelation> revelation;
        public EnumPredicate<Aspect> aspect;
        public EnumPredicate<Synergy> synergy;

        public Conditions(EntityPredicate.Extended playerPredicate, ItemPredicate item, EnumPredicate<Revelation> revelation, EnumPredicate<Aspect> aspect, EnumPredicate<Synergy> synergy) {
            super(ID, playerPredicate);
            this.item = item;
            this.revelation = revelation;
            this.aspect = aspect;
            this.synergy = synergy;
        }

        public boolean matches(ItemStack stack, Revelation revelation, Aspect aspect, Synergy synergy) {
            return this.item.test(stack)
                    && this.revelation.test(revelation)
                    && this.aspect.test(aspect)
                    && this.synergy.test(synergy);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            obj.add("item", item.toJson());
            obj.add("revelation", revelation.toJson());
            obj.add("aspect", aspect.toJson());
            obj.add("synergy", aspect.toJson());
            return obj;
        }
    }
}
