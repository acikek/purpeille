package com.acikek.purpeille.api;

import com.acikek.purpeille.impl.PurpeilleAPIImpl;
import net.minecraft.entity.LivingEntity;

public class PurpeilleAPI {

    public static boolean isUsingAmalgamatedSpyglass(LivingEntity livingEntity) {
        return PurpeilleAPIImpl.isUsingAmalgamatedSpyglass(livingEntity);
    }
}
