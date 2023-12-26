package com.acikek.purpeille.loot;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.api.warpath.Components;
import com.acikek.purpeille.warpath.Warpath;
import com.acikek.purpeille.warpath.component.Revelation;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.List;

public class RandomRevelationLootFunction extends ConditionalLootFunction {

    public static LootFunctionType LOOT_FUNCTION_TYPE;

    protected RandomRevelationLootFunction(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        List<Revelation> revelations = Components.getRevelations().values().stream().toList();
        Warpath.add(stack, revelations.get(context.getRandom().nextInt(revelations.size())), null);
        return stack;
    }

    @Override
    public LootFunctionType getType() {
        return LOOT_FUNCTION_TYPE;
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<RandomRevelationLootFunction> {

        @Override
        public RandomRevelationLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return new RandomRevelationLootFunction(conditions);
        }
    }

    public static void register() {
        LOOT_FUNCTION_TYPE = Registry.register(
                Registries.LOOT_FUNCTION_TYPE,
                Purpeille.id("random_revelation"),
                new LootFunctionType(new Serializer())
        );
    }
}
