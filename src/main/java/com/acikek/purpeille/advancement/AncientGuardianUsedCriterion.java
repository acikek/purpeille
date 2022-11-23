package com.acikek.purpeille.advancement;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.item.core.EncasedCore;
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
import net.minecraft.util.JsonHelper;

public class AncientGuardianUsedCriterion extends AbstractCriterion<AncientGuardianUsedCriterion.Conditions> {

    public static Identifier ID = Purpeille.id("ancient_guardian_used");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        EnumPredicate<EncasedCore.Type> coreType = EnumPredicate.fromJson(obj.get("core_type"), EncasedCore.Type::valueOf);
        NumberRange.IntRange killed = NumberRange.IntRange.fromJson(obj.get("killed"));
        Boolean interdimensional = JsonHelper.hasBoolean(obj, "interdimensional") ? JsonHelper.getBoolean(obj, "interdimensional") : null;
        return new Conditions(playerPredicate, coreType, killed, interdimensional);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, EncasedCore.Type coreType, int killed, boolean interdimensional) {
        trigger(player, conditions -> conditions.matches(coreType, killed, interdimensional));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public EnumPredicate<EncasedCore.Type> coreType;
        public NumberRange.IntRange killed;
        public Boolean interdimensional;

        public Conditions(EntityPredicate.Extended playerPredicate, EnumPredicate<EncasedCore.Type> coreType, NumberRange.IntRange killed, Boolean interdimensional) {
            super(ID, playerPredicate);
            this.coreType = coreType;
            this.killed = killed;
            this.interdimensional = interdimensional;
        }

        public boolean matches(EncasedCore.Type coreType, int killed, boolean interdimensional) {
            return this.coreType.test(coreType)
                    && this.killed.test(killed)
                    && (this.interdimensional == null || this.interdimensional == interdimensional);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            obj.add("core_type", coreType.toJson());
            obj.add("killed", killed.toJson());
            if (this.interdimensional != null) {
                obj.addProperty("interdimensional", interdimensional);
            }
            return obj;
        }
    }
}
