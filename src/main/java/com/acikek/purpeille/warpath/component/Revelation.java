package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.api.AbyssalToken;
import com.acikek.purpeille.warpath.*;
import com.google.gson.JsonObject;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class Revelation extends Component {

    public static UUID WARPATH_ID = UUID.fromString("2c67c058-5d5e-4b39-98e3-b3eb9965f7eb");
    public static int RITE_RGB = 13421772;
    public static Identifier FINISH_RELOAD = Purpeille.id("revelation_finish_reload");

    public AttributeData attribute;
    public Ingredient affinity;
    public Map<Identifier, Synergy> synergy;
    public AbyssaliteData abyssalite;
    public int dyeColor;
    public boolean forceInt;
    public MutableText rite;

    public Revelation(Identifier id, Tone tone, int color, Ingredient catalyst, int index, double modifier, boolean ignoreSlot, List<Identifier> whitelist, AttributeData attribute, Ingredient affinity, Map<Identifier, Synergy> synergy, AbyssaliteData abyssalite, int dyeColor, boolean forceInt) {
        super(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist);
        this.attribute = attribute;
        this.affinity = affinity;
        this.synergy = synergy;
        this.abyssalite = abyssalite;
        this.dyeColor = dyeColor == -1 ? getClosestDyeColor(waveColor) : dyeColor;
        this.forceInt = forceInt;
        rite = Text.translatable(getIdKey("rite", id)).styled(style -> style.withColor(RITE_RGB));
    }

    public Revelation(Aspect aspect, AttributeData attribute, Ingredient affinity, Map<Identifier, Synergy> synergy, AbyssaliteData abyssalite, int dyeColor, boolean forceInt) {
        this(aspect.id, aspect.tone, aspect.color, aspect.catalyst, aspect.index, aspect.modifier, aspect.ignoreSlot, aspect.whitelist, attribute, affinity, synergy, abyssalite, dyeColor, forceInt);
    }

    public static void finishAbyssaliteReload(boolean log) {
        AbyssalToken.clearTokens();
        List<Map.Entry<Identifier, Revelation>> hasAbyssalite = Component.REVELATIONS.entrySet().stream()
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
        for (Map.Entry<Identifier, Revelation> pair : Component.REVELATIONS.entrySet()) {
            Revelation revelation = pair.getValue();
            if (!revelation.attribute.finishReload() || (revelation.abyssalite != null && !revelation.abyssalite.attribute.finishReload())) {
                if (log) {
                    Purpeille.LOGGER.error("Revelation '" + pair.getKey() + "' has an invalid attribute ID: '" + pair.getValue().attribute.id + "'");
                }
                toRemove.add(pair.getKey());
            }
        }
        for (Identifier id : toRemove) {
            Component.REVELATIONS.remove(id);
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

    @Override
    public Type getType() {
        return Type.REVELATION;
    }

    public double getModifierValue(ItemStack stack, Aspect aspect) {
        double value = affinity.test(stack) ? modifier * 1.2 : modifier;
        if (aspect == null) {
            return value;
        }
        return value * aspect.modifier * Synergy.getSynergy(this, aspect).modifier;
    }

    public EntityAttributeModifier getModifier(ItemStack stack, Aspect aspect) {
        double value = getModifierValue(stack, aspect);
        double adjusted = forceInt ? (int) value : value;
        return new EntityAttributeModifier(WARPATH_ID, "Warpath modifier", adjusted, attribute.operation);
    }

    public static class Builder extends Aspect.Builder {

        AttributeData attribute;
        Ingredient affinity;
        Map<Identifier, Synergy> synergy;
        AbyssaliteData abyssalite;
        int dyeColor = -1;
        boolean forceInt = false;

        public Builder aspect(Aspect aspect) {
            tone = aspect.tone;
            color = aspect.color;
            catalyst = aspect.catalyst;
            index = aspect.index;
            modifier = aspect.modifier;
            ignoreSlot = aspect.ignoreSlot;
            whitelist = aspect.whitelist;
            return this;
        }

        public Builder attribute(AttributeData attribute) {
            this.attribute = attribute;
            return this;
        }

        public Builder affinity(Ingredient affinity) {
            this.affinity = affinity;
            return this;
        }

        public Builder synergy(Map<Identifier, Synergy> synergy) {
            this.synergy = synergy;
            return this;
        }

        public Builder abyssalite(AbyssaliteData abyssalite) {
            this.abyssalite = abyssalite;
            return this;
        }

        public Builder dyeColor(int dyeColor) {
            this.dyeColor = dyeColor;
            return this;
        }

        public Builder dyeColor(DyeColor color) {
            return dyeColor(color.getId());
        }

        public Builder forceInt(boolean forceInt) {
            this.forceInt = forceInt;
            return this;
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
                return new Revelation(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist, attribute, affinity, synergy, abyssalite, dyeColor, forceInt);
            }
            return null;
        }
    }

    public static Revelation fromJson(JsonObject obj, Identifier id) {
        AttributeData attribute = JsonHelper.hasBoolean(obj, "multiply")
                ? new AttributeData(Identifier.tryParse(JsonHelper.getString(obj, "attribute")), JsonHelper.getBoolean(obj, "multiply"))
                : AttributeData.fromJson(JsonHelper.getObject(obj, "attribute"));
        return new Builder()
                .aspect(Aspect.fromJson(obj, id))
                .attribute(attribute)
                .affinity(Ingredient.fromJson(obj.get("affinity")))
                .synergy(Synergy.overridesFromJson(JsonHelper.getObject(obj, "synergy", null)))
                .abyssalite(AbyssaliteData.fromJson(JsonHelper.getObject(obj, "abyssalite", null)))
                .dyeColor(JsonHelper.getInt(obj, "dye_color", -1))
                .forceInt(JsonHelper.getBoolean(obj, "force_int", false))
                .buildRevelation(id);
    }

    public static Revelation read(PacketByteBuf buf) {
        Aspect aspect = Aspect.read(buf);
        AttributeData attribute = AttributeData.read(buf);
        Ingredient affinity = Ingredient.fromPacket(buf);
        Map<Identifier, Synergy> synergy = buf.readBoolean() ? buf.readMap(PacketByteBuf::readIdentifier, Synergy::read) : null;
        AbyssaliteData abyssalite = buf.readBoolean() ? AbyssaliteData.read(buf) : null;
        int dyeColor = buf.readInt();
        boolean forceInt = buf.readBoolean();
        return new Revelation(aspect, attribute, affinity, synergy, abyssalite, dyeColor, forceInt);
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
        buf.writeInt(dyeColor);
        buf.writeBoolean(forceInt);
    }
}
