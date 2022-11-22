package com.acikek.purpeille.impl;

import com.acikek.purpeille.item.ModItems;
import net.minecraft.entity.LivingEntity;

public class PurpeilleAPIImpl {

    public static boolean isUsingAmalgamatedSpyglass(LivingEntity livingEntity) {
        return livingEntity.isUsingItem() && livingEntity.getActiveItem().isOf(ModItems.AMALGAMATED_SPYGLASS);
    }
}
