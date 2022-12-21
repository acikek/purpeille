package com.acikek.purpeille.api.warpath;

import com.acikek.purpeille.warpath.AbyssaliteData;
import com.acikek.purpeille.warpath.attribute.AttributeData;
import com.acikek.purpeille.warpath.Synergy;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.MutableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Objects;

public class RevelationBuilder extends AspectBuilder {

    AttributeData attribute;
    Ingredient affinity;
    Map<Identifier, Synergy> synergy;
    AbyssaliteData abyssalite;
    MutableText rite;
    int dyeColor = -1;

    public RevelationBuilder aspect(Aspect aspect) {
        tone = aspect.tone;
        color = aspect.color;
        catalyst = aspect.catalyst;
        index = aspect.index;
        modifier = aspect.modifier;
        ignoreSlot = aspect.ignoreSlot;
        whitelist = aspect.whitelist;
        display = aspect.display;
        return this;
    }

    public RevelationBuilder attribute(AttributeData attribute) {
        this.attribute = attribute;
        return this;
    }

    public RevelationBuilder affinity(Ingredient affinity) {
        this.affinity = affinity;
        return this;
    }

    public RevelationBuilder synergy(Map<Identifier, Synergy> synergy) {
        this.synergy = synergy;
        return this;
    }

    public RevelationBuilder abyssalite(AbyssaliteData abyssalite) {
        this.abyssalite = abyssalite;
        return this;
    }

    public RevelationBuilder rite(MutableText rite) {
        this.rite = rite;
        return this;
    }

    public RevelationBuilder dyeColor(int dyeColor) {
        this.dyeColor = dyeColor;
        return this;
    }

    public RevelationBuilder dyeColor(DyeColor color) {
        return dyeColor(color.getId());
    }

    @Override
    boolean isValid(Identifier id) {
        Objects.requireNonNull(attribute);
        Objects.requireNonNull(affinity);
        if (dyeColor < -1 || dyeColor >= DyeColor.values().length) {
            throw new IllegalStateException("'dyeColor' must be a valid dye color ID or -1");
        }
        return super.isValid(id);
    }

    public Revelation buildRevelation(Identifier id) {
        if (isValid(id)) {
            return new Revelation(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist, display, attribute, affinity, synergy, abyssalite, rite, dyeColor);
        }
        return null;
    }
}
