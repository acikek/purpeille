package com.acikek.purpeille.item.material;

import com.acikek.purpeille.item.ModItems;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class PurpeilleArmorMaterial implements ArmorMaterial {

    public static final ArmorMaterial INSTANCE = new PurpeilleArmorMaterial();

    public static int[] BASE_DURABILITY = { 13, 15, 16, 11 };
    public static int[] PROTECTION_VALUES = { 4, 7, 9, 4 };

    public static ArmorItem getItem(ArmorItem.Type type) {
        return new ArmorItem(INSTANCE, type, ModItems.defaultSettings());
    }

    @Override
    public int getDurability(ArmorItem.Type type) {
        return BASE_DURABILITY[type.ordinal()] * 45;
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return PROTECTION_VALUES[type.ordinal()];
    }

    @Override
    public int getEnchantability() {
        return 10;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ModItems.PURPEILLE_INGOT);
    }

    @Override
    public String getName() {
        return "purpeille";
    }

    @Override
    public float getToughness() {
        return 3.0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.3f;
    }
}
