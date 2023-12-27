package com.acikek.purpeille.item;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.item.core.CreativeCore;
import com.acikek.purpeille.item.core.EncasedCore;
import com.acikek.purpeille.item.material.PurpeilleArmorMaterial;
import com.acikek.purpeille.item.material.PurpeilleToolMaterial;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

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
    public static final Item ULTRAVIOLET_PRISM = new Item(defaultSettings());
    public static final Item METALLIC_LIGHTNING = new Item(defaultSettings());

    // Encased Cores
    public static final EncasedCore ENCASED_CORE = EncasedCore.Type.NORMAL.getCore();
    public static final EncasedCore ENERGETIC_CORE = EncasedCore.Type.ENERGETIC.getCore();
    public static final EncasedCore OVERCLOCKED_CORE = EncasedCore.Type.OVERCLOCKED.getCore();
    public static final EncasedCore VACUOUS_CORE = EncasedCore.Type.VACUOUS.getCore();
    public static final CreativeCore CREATIVE_CORE = new CreativeCore(defaultSettings());

    // Armor
    public static final ArmorItem PURPEILLE_HELMET = PurpeilleArmorMaterial.getItem(ArmorItem.Type.HELMET);
    public static final ArmorItem PURPEILLE_CHESTPLATE = PurpeilleArmorMaterial.getItem(ArmorItem.Type.CHESTPLATE);
    public static final ArmorItem PURPEILLE_LEGGINGS = PurpeilleArmorMaterial.getItem(ArmorItem.Type.LEGGINGS);
    public static final ArmorItem PURPEILLE_BOOTS = PurpeilleArmorMaterial.getItem(ArmorItem.Type.BOOTS);

    // Tools
    public static final ToolItem PURPEILLE_SWORD = new SwordItem(PurpeilleToolMaterial.INSTANCE, 4, -2.4f, defaultSettings());
    public static final ToolItem PURPEILLE_SHOVEL = new ShovelItem(PurpeilleToolMaterial.INSTANCE, 1.5f, -3.0f, defaultSettings());
    public static final ToolItem PURPEILLE_PICKAXE = new PurpeilleToolMaterial.PurpeillePickaxeItem(1, -2.8f);
    public static final ToolItem PURPEILLE_AXE = new PurpeilleToolMaterial.PurpeilleAxeItem(5.0f, -3.0f);
    public static final ToolItem PURPEILLE_HOE = new PurpeilleToolMaterial.PurpeilleHoeItem(-4, -0.0f);

    // Handbook
    public static final Item ANCIENTS_ALMANAC = new AncientsAlmanac(defaultSettings().rarity(Rarity.UNCOMMON).maxCount(1));

    public static FabricItemSettings defaultSettings() {
        return new FabricItemSettings();
    }

    public static Map<String, Item> ITEMS = new LinkedHashMap<>();

    static {
        ITEMS.put("impure_preserved_dust", IMPURE_PRESERVED_DUST);
        ITEMS.put("preserved_dust", PRESERVED_DUST);
        ITEMS.put("remnant_chunk", REMNANT_CHUNK);
        ITEMS.put("remnant_brick", REMNANT_BRICK);
        ITEMS.put("purpeille_ingot", PURPEILLE_INGOT);
        ITEMS.put("smoldered_purpeille_ingot", SMOLDERED_PURPEILLE_INGOT);
        ITEMS.put("ultraviolet_prism", ULTRAVIOLET_PRISM);
        ITEMS.put("metallic_lightning", METALLIC_LIGHTNING);
        ITEMS.put("encased_core", ENCASED_CORE);
        ITEMS.put("energetic_core", ENERGETIC_CORE);
        ITEMS.put("overclocked_core", OVERCLOCKED_CORE);
        ITEMS.put("vacuous_core", VACUOUS_CORE);
        ITEMS.put("creative_core", CREATIVE_CORE);
        ITEMS.put("purpeille_helmet", PURPEILLE_HELMET);
        ITEMS.put("purpeille_chestplate", PURPEILLE_CHESTPLATE);
        ITEMS.put("purpeille_leggings", PURPEILLE_LEGGINGS);
        ITEMS.put("purpeille_boots", PURPEILLE_BOOTS);
        ITEMS.put("purpeille_sword", PURPEILLE_SWORD);
        ITEMS.put("purpeille_shovel", PURPEILLE_SHOVEL);
        ITEMS.put("purpeille_pickaxe", PURPEILLE_PICKAXE);
        ITEMS.put("purpeille_axe", PURPEILLE_AXE);
        ITEMS.put("purpeille_hoe", PURPEILLE_HOE);
        ITEMS.put("ancients_almanac", ANCIENTS_ALMANAC);
    }

    public static void register() {
        for (Map.Entry<String, Item> item : ITEMS.entrySet()) {
            Registry.register(Registries.ITEM, Purpeille.id(item.getKey()), item.getValue());
        }
        ItemGroupEvents.modifyEntriesEvent(Purpeille.ITEM_GROUP_REGISTRY_KEY)
                .register(entries -> ITEMS.values().forEach(entries::add));
    }
}
