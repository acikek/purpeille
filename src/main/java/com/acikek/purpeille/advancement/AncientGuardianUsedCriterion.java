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
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class AncientGuardianUsedCriterion extends AbstractCriterion<AncientGuardianUsedCriterion.Conditions> {

    public static Identifier ID = Purpeille.id("ancient_guardian_used");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        EnumPredicate<EncasedCore.Type> coreType = EnumPredicate.fromJson(obj.get("core_type"), EncasedCore.Type::valueOf);
        NumberRange.IntRange killed = NumberRange.IntRange.fromJson(obj.get("killed"));
        boolean interdimensional = JsonHelper.getBoolean(obj, "interdimensional", false);
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
        public boolean interdimensional;

        public Conditions(LootContextPredicate playerPredicate, EnumPredicate<EncasedCore.Type> coreType, NumberRange.IntRange killed, boolean interdimensional) {
            super(ID, playerPredicate);
            this.coreType = coreType;
            this.killed = killed;
            this.interdimensional = interdimensional;
        }

        public boolean matches(EncasedCore.Type coreType, int killed, boolean interdimensional) {
            return this.coreType.test(coreType)
                    && this.killed.test(killed)
                    && (!this.interdimensional || interdimensional);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            obj.add("core_type", coreType.toJson());
            obj.add("killed", killed.toJson());
            obj.addProperty("interdimensional", interdimensional);
            return obj;
        }
    }
}
