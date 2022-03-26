package com.acikek.purpeille.item.material;

import com.acikek.purpeille.item.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class PurpeilleArmorMaterial implements ArmorMaterial {

    public static final ArmorMaterial INSTANCE = new PurpeilleArmorMaterial();

    public static int[] BASE_DURABILITY = { 13, 15, 16, 11 };
    public static int[] PROTECTION_VALUES = { 4, 7, 9, 4 };

    public static ArmorItem getItem(EquipmentSlot slot) {
        return new ArmorItem(INSTANCE, slot, ModItems.defaultSettings());
    }

    @Override
    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * 45;
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return PROTECTION_VALUES[slot.getEntitySlotId()];
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
