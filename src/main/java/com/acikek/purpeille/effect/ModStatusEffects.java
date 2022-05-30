package com.acikek.purpeille.effect;

import com.acikek.purpeille.Purpeille;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.registry.Registry;

public class ModStatusEffects {

    public static final StatusEffect VOID_IMMUNITY = new StatusEffect(StatusEffectCategory.BENEFICIAL, 0x1c0d3b) {};

    public static void register() {
        Registry.register(Registry.STATUS_EFFECT, Purpeille.id("void_immunity"), VOID_IMMUNITY);
    }
}
