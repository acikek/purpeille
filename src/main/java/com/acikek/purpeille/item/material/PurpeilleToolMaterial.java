package com.acikek.purpeille.item.material;

import com.acikek.purpeille.item.ModItems;
import com.acikek.purpeille.tag.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;

public class PurpeilleToolMaterial implements ToolMaterial {

    public static final PurpeilleToolMaterial INSTANCE = new PurpeilleToolMaterial();

    public static class PurpeillePickaxeItem extends PickaxeItem {

        public PurpeillePickaxeItem(int attackDamage, float attackSpeed) {
            super(INSTANCE, attackDamage, attackSpeed, ModItems.defaultSettings());
        }

        @Override
        public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
            int efficiency = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
            return state.isIn(ModTags.PURPEILLE_PICKAXE_HASTENERS)
                    ? miningSpeed * (1.0f + efficiency * 0.36f)
                    : super.getMiningSpeedMultiplier(stack, state);
        }
    }

    public static class PurpeilleHoeItem extends HoeItem {

        public PurpeilleHoeItem(int attackDamage, float attackSpeed) {
            super(INSTANCE, attackDamage, attackSpeed, ModItems.defaultSettings());
        }
    }

    public static class PurpeilleAxeItem extends AxeItem {

        public PurpeilleAxeItem(float attackDamage, float attackSpeed) {
            super(INSTANCE, attackDamage, attackSpeed, ModItems.defaultSettings());
        }
    }

    @Override
    public int getDurability() {
        return 2732;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 11.0f;
    }

    @Override
    public float getAttackDamage() {
        return 5.0f;
    }

    @Override
    public int getMiningLevel() {
        return 5;
    }

    @Override
    public int getEnchantability() {
        return 10;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ModItems.PURPEILLE_INGOT);
    }
}
