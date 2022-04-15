package com.acikek.purpeille.item;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.item.material.PurpeilleArmorMaterial;
import com.acikek.purpeille.item.material.PurpeilleToolMaterial;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModItems {

    // Materials
    public static final Item IMPURE_PRESERVED_DUST = new Item(defaultSettings());
    public static final Item PRESERVED_DUST = new Item(defaultSettings());
    public static final Item REMNANT_CHUNK = new Item(defaultSettings());
    public static final Item REMNANT_BRICK = new Item(defaultSettings());
    public static final Item PURPEILLE_INGOT = new Item(defaultSettings());
    public static final Item SMOLDERED_PURPEILLE_INGOT = new Item(defaultSettings());
    public static final Item ENCASED_CORE = new Item(defaultSettings());

    // Armor
    public static final ArmorItem PURPEILLE_HELMET = PurpeilleArmorMaterial.getItem(EquipmentSlot.HEAD);
    public static final ArmorItem PURPEILLE_CHESTPLATE = PurpeilleArmorMaterial.getItem(EquipmentSlot.CHEST);
    public static final ArmorItem PURPEILLE_LEGGINGS = PurpeilleArmorMaterial.getItem(EquipmentSlot.LEGS);
    public static final ArmorItem PURPEILLE_BOOTS = PurpeilleArmorMaterial.getItem(EquipmentSlot.FEET);

    // Tools
    public static final ToolItem PURPEILLE_SWORD = new SwordItem(PurpeilleToolMaterial.INSTANCE, 4, -2.4f, defaultSettings());
    public static final ToolItem PURPEILLE_SHOVEL = new ShovelItem(PurpeilleToolMaterial.INSTANCE, 1.5f, -3.0f, defaultSettings());
    public static final ToolItem PURPEILLE_PICKAXE = new PurpeilleToolMaterial.PurpeillePickaxeItem(1, -2.8f);
    public static final ToolItem PURPEILLE_AXE = new PurpeilleToolMaterial.PurpeilleAxeItem(5.0f, -3.0f);
    public static final ToolItem PURPEILLE_HOE = new PurpeilleToolMaterial.PurpeilleHoeItem(-4, -0.0f);

    public static FabricItemSettings defaultSettings() {
        return new FabricItemSettings().group(Purpeille.ITEM_GROUP);
    }

    public static Map<String, Item> ITEMS = new LinkedHashMap<>();

    static {
        ITEMS.put("impure_preserved_dust", IMPURE_PRESERVED_DUST);
        ITEMS.put("preserved_dust", PRESERVED_DUST);
        ITEMS.put("remnant_chunk", REMNANT_CHUNK);
        ITEMS.put("remnant_brick", REMNANT_BRICK);
        ITEMS.put("purpeille_ingot", PURPEILLE_INGOT);
        ITEMS.put("smoldered_purpeille_ingot", SMOLDERED_PURPEILLE_INGOT);
        ITEMS.put("encased_core", ENCASED_CORE);
        ITEMS.put("purpeille_helmet", PURPEILLE_HELMET);
        ITEMS.put("purpeille_chestplate", PURPEILLE_CHESTPLATE);
        ITEMS.put("purpeille_leggings", PURPEILLE_LEGGINGS);
        ITEMS.put("purpeille_boots", PURPEILLE_BOOTS);
        ITEMS.put("purpeille_sword", PURPEILLE_SWORD);
        ITEMS.put("purpeille_shovel", PURPEILLE_SHOVEL);
        ITEMS.put("purpeille_pickaxe", PURPEILLE_PICKAXE);
        ITEMS.put("purpeille_axe", PURPEILLE_AXE);
        ITEMS.put("purpeille_hoe", PURPEILLE_HOE);
    }

    public static void register() {
        for (Map.Entry<String, Item> item : ITEMS.entrySet()) {
            Registry.register(Registry.ITEM, Purpeille.id(item.getKey()), item.getValue());
        }
    }
}
