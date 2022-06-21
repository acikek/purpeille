package com.acikek.purpeille.advancement;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.UltravioletComplex;
import com.google.gson.JsonObject;
import lib.EnumPredicate;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class UltravioletComplexBurnsCriterion extends AbstractCriterion<UltravioletComplexBurnsCriterion.Conditions> {

    public static Identifier ID = Purpeille.id("ultraviolet_complex_burns");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        EnumPredicate<UltravioletComplex.Type> complexType = EnumPredicate.fromJson(obj.get("complex_type"), UltravioletComplex.Type::valueOf);
        NumberRange.IntRange blocks = NumberRange.IntRange.fromJson(obj.get("light_level"));
        return new Conditions(playerPredicate, complexType, blocks);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, UltravioletComplex.Type complexType, int lightLevel) {
        trigger(player, conditions -> conditions.matches(complexType, lightLevel));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public EnumPredicate<UltravioletComplex.Type> complexType;
        public NumberRange.IntRange lightLevel;

        public Conditions(EntityPredicate.Extended playerPredicate, EnumPredicate<UltravioletComplex.Type> complexType, NumberRange.IntRange lightLevel) {
            super(ID, playerPredicate);
            this.complexType = complexType;
            this.lightLevel = lightLevel;
        }

        public boolean matches(UltravioletComplex.Type complexType, int lightLevel) {
            return this.complexType.test(complexType) && this.lightLevel.test(lightLevel);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            obj.add("complex_type", complexType.toJson());
            obj.add("light_level", lightLevel.toJson());
            return obj;
        }
    }
}
