package com.acikek.purpeille.loot;

import com.acikek.purpeille.Purpeille;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.registry.Registry;

public class RandomWarpathLootFunction extends ConditionalLootFunction {

    public static LootFunctionType LOOT_FUNCTION_TYPE;

    protected RandomWarpathLootFunction(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        return Items.STICK.getDefaultStack();
    }

    @Override
    public LootFunctionType getType() {
        return LOOT_FUNCTION_TYPE;
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<RandomWarpathLootFunction> {

        @Override
        public RandomWarpathLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return new RandomWarpathLootFunction(conditions);
        }
    }

    public static void register() {
        LOOT_FUNCTION_TYPE = Registry.register(
                Registry.LOOT_FUNCTION_TYPE,
                Purpeille.id("random_warpath"),
                new LootFunctionType(new Serializer())
        );
    }
}
