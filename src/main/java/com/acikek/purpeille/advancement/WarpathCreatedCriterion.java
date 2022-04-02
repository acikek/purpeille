package com.acikek.purpeille.advancement;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.warpath.Aspect;
import com.acikek.purpeille.warpath.Revelation;
import com.google.gson.JsonObject;
import lib.EnumPredicate;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class WarpathCreatedCriterion extends AbstractCriterion<WarpathCreatedCriterion.Conditions> {

    public static Identifier ID = Purpeille.id("warpath_created");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        EnumPredicate<EquipmentSlot> slot = EnumPredicate.fromJson(obj.get("slot"), EquipmentSlot::valueOf);
        EnumPredicate<Revelation> revelation = EnumPredicate.fromJson(obj.get("revelation"), Revelation::valueOf);
        EnumPredicate<Aspect> aspect = EnumPredicate.fromJson(obj.get("aspect"), Aspect::valueOf);
        return new Conditions(playerPredicate, slot, revelation, aspect);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, EquipmentSlot slot, Revelation revelation, Aspect aspect) {
        trigger(player, conditions -> conditions.matches(slot, revelation, aspect));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public EnumPredicate<EquipmentSlot> slot;
        public EnumPredicate<Revelation> revelation;
        public EnumPredicate<Aspect> aspect;

        public Conditions(EntityPredicate.Extended playerPredicate, EnumPredicate<EquipmentSlot> slot, EnumPredicate<Revelation> revelation, EnumPredicate<Aspect> aspect) {
            super(ID, playerPredicate);
            this.slot = slot;
            this.revelation = revelation;
            this.aspect = aspect;
        }

        public boolean matches(EquipmentSlot slot, Revelation revelation, Aspect aspect) {
            return this.slot.test(slot) && this.revelation.test(revelation) && this.aspect.test(aspect);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            obj.add("slot", slot.toJson());
            obj.add("revelation", revelation.toJson());
            obj.add("aspect", aspect.toJson());
            return obj;
        }
    }
}
