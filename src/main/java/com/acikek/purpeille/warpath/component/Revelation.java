package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.api.abyssal.AbyssalTokens;
import com.acikek.purpeille.api.warpath.Components;
import com.acikek.purpeille.api.warpath.RevelationBuilder;
import com.acikek.purpeille.impl.ComponentsImpl;
import com.acikek.purpeille.warpath.*;
import com.acikek.purpeille.warpath.attribute.AttributeData;
import com.google.gson.JsonObject;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class Revelation extends Aspect implements Writer {

    public static final String KEY = "revelation";

    public static int RITE_RGB = 13421772;
    public static Identifier FINISH_RELOAD = Purpeille.id("revelation_finish_reload");

    public AttributeData attribute;
    public Ingredient affinity;
    public Map<Identifier, Synergy> synergy;
    public AbyssaliteData abyssalite;
    public MutableText rite;
    public int dyeColor;

    public Revelation(Identifier id, Tone tone, int color, Ingredient catalyst, int index, double modifier, boolean ignoreSlot, List<Identifier> whitelist, MutableText display, AttributeData attribute, Ingredient affinity, Map<Identifier, Synergy> synergy, AbyssaliteData abyssalite, MutableText rite, int dyeColor) {
        super(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist, display);
        this.attribute = attribute;
        this.affinity = affinity;
        this.synergy = synergy;
        this.abyssalite = abyssalite;
        this.rite = rite.styled(style -> style.withColor(RITE_RGB));
        this.dyeColor = dyeColor == -1 ? getClosestDyeColor(waveColor) : dyeColor;
    }

    public Revelation(Aspect aspect, AttributeData attribute, Ingredient affinity, Map<Identifier, Synergy> synergy, AbyssaliteData abyssalite, MutableText rite, int dyeColor) {
        this(aspect.id, aspect.tone, aspect.color, aspect.catalyst, aspect.index, aspect.modifier, aspect.ignoreSlot, aspect.whitelist, aspect.display, attribute, affinity, synergy, abyssalite, rite, dyeColor);
    }

    @Override
    public int getIndex() {
        return relativeIndex;
    }

    public static void finishAbyssaliteReload(boolean log) {
        AbyssalTokens.clearTokens();
        List<Map.Entry<Identifier, Revelation>> hasAbyssalite = Components.getRevelations().entrySet().stream()
                .filter(pair -> pair.getValue().abyssalite != null)
                .toList();
        Set<Item> uniques = new HashSet<>();
        List<Item> duplicates = hasAbyssalite.stream()
                .filter(pair -> !uniques.add(pair.getValue().abyssalite.token))
                .map(pair -> pair.getValue().abyssalite.token)
                .toList();
        for (Map.Entry<Identifier, Revelation> pair : hasAbyssalite) {
            if (duplicates.contains(pair.getValue().abyssalite.token)) {
                if (log) {
                    Identifier tokenId = Registry.ITEM.getId(pair.getValue().abyssalite.token);
                    Purpeille.LOGGER.error("Revelation '" + pair.getKey() + "' has duplicate abyssal token: '" + tokenId + "'");
                }
                continue;
            }
            pair.getValue().abyssalite.load(pair.getValue());
        }
    }

    public static void finishAttributeReload(boolean log) {
        List<Identifier> toRemove = new ArrayList<>();
        for (Map.Entry<Identifier, Revelation> pair : Components.getRevelations().entrySet()) {
            Revelation revelation = pair.getValue();
            if (!revelation.attribute.finishReload() || (revelation.abyssalite != null && !revelation.abyssalite.attribute.finishReload())) {
                if (log) {
                    Purpeille.LOGGER.error("Revelation '" + pair.getKey() + "' has an invalid attribute ID: '" + pair.getValue().attribute.id + "'");
                }
                toRemove.add(pair.getKey());
            }
        }
        for (Identifier id : toRemove) {
            ComponentsImpl.REVELATIONS.remove(id);
        }
    }

    public static int getClosestDyeColor(ClampedColor waveColor) {
        int closestColor = -1;
        float difference = Float.MAX_VALUE;
        for (DyeColor dyeColor : DyeColor.values()) {
            float diff = waveColor.getDifference(dyeColor.getColorComponents());
            if (diff < difference) {
                difference = diff;
                closestColor = dyeColor.getId();
            }
        }
        return closestColor;
    }

    public double getModifierValue(ItemStack stack, Aspect aspect) {
        double value = affinity.test(stack) ? modifier * 1.2 : modifier;
        if (aspect == null) {
            return value;
        }
        return value * aspect.modifier * Synergy.getSynergy(this, aspect).modifier;
    }

    public EntityAttributeModifier getModifier(ItemStack stack, EquipmentSlot slot, Aspect aspect) {
        return attribute.getModifier(slot, "Warpath modifier", getModifierValue(stack, aspect));
    }

    public static Revelation fromJson(JsonObject obj, Identifier id) {
        AttributeData attribute = AttributeData.fromJson(
                JsonHelper.hasBoolean(obj, "multiply")
                        ? obj
                        : JsonHelper.getObject(obj, "attribute")
        );
        RevelationBuilder builder = new RevelationBuilder()
                .aspect(Aspect.fromJson(obj, id, KEY))
                .attribute(attribute)
                .affinity(Ingredient.fromJson(obj.get("affinity")))
                .synergy(Synergy.overridesFromJson(JsonHelper.getObject(obj, "synergy", null)))
                .abyssalite(AbyssaliteData.fromJson(JsonHelper.getObject(obj, "abyssalite", null)))
                .dyeColor(JsonHelper.getInt(obj, "dye_color", -1));
        MutableText rite = obj.has("rite")
                ? Text.Serializer.fromJson(obj.get("rite"))
                : Text.translatable(getIdKey("rite", id));
        return builder.rite(rite)
                .buildRevelation(id);
    }

    public static Revelation read(PacketByteBuf buf) {
        Aspect aspect = Aspect.read(buf);
        AttributeData attribute = AttributeData.read(buf);
        Ingredient affinity = Ingredient.fromPacket(buf);
        Map<Identifier, Synergy> synergy = buf.readBoolean() ? buf.readMap(PacketByteBuf::readIdentifier, Synergy::read) : null;
        AbyssaliteData abyssalite = buf.readBoolean() ? AbyssaliteData.read(buf) : null;
        MutableText rite = (MutableText) buf.readText();
        int dyeColor = buf.readInt();
        return new Revelation(aspect, attribute, affinity, synergy, abyssalite, rite, dyeColor);
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        attribute.write(buf);
        affinity.write(buf);
        buf.writeBoolean(synergy != null);
        if (synergy != null) {
            buf.writeMap(synergy, PacketByteBuf::writeIdentifier, Synergy::write);
        }
        buf.writeBoolean(abyssalite != null);
        if (abyssalite != null) {
            abyssalite.write(buf);
        }
        buf.writeText(rite);
        buf.writeInt(dyeColor);
    }
}
