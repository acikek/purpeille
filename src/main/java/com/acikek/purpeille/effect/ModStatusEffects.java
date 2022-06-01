package com.acikek.purpeille.effect;

import com.acikek.purpeille.Purpeille;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.registry.Registry;

public class ModStatusEffects {

    public static final StatusEffect VOID_IMMUNITY = new StatusEffect(StatusEffectType.BENEFICIAL, 0x1c0d3b) {};

    public static void register() {
        Registry.register(Registry.STATUS_EFFECT, Purpeille.id("void_immunity"), VOID_IMMUNITY);
    }
}
