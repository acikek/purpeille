package com.acikek.purpeille.warpath;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

public class Util {

    public static <T extends Enum<T>> T enumFromJson(JsonObject obj, String key, Function<String, T> valueOf) {
        JsonElement element = obj.get(key);
        try {
            return valueOf.apply(element.getAsString().toUpperCase());
        }
        catch (Exception e) {
            throw new IllegalStateException(key + " is not a valid enum");
        }
    }

    public static Item itemFromJson(JsonObject obj, String key) {
        Identifier id = Identifier.tryParse(obj.get(key).getAsString());
        return Registry.ITEM.get(id);
    }

    /**
     * Returns the slot in which the warpath should activate.
     * For tools, returns {@link EquipmentSlot#MAINHAND}.
     * For armor, returns its corresponding slot type.
     * Otherwise, returns {@code null}.
     */
    public static EquipmentSlot getSlot(ItemStack stack) {
        if (stack.getItem() instanceof ToolItem) {
            return EquipmentSlot.MAINHAND;
        }
        if (stack.getItem() instanceof ArmorItem armorItem) {
            return armorItem.getSlotType();
        }
        return null;
    }
}
