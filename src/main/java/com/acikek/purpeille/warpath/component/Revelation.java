package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.api.AbyssalToken;
import com.acikek.purpeille.warpath.Abyssalite;
import com.acikek.purpeille.warpath.ClampedColor;
import com.acikek.purpeille.warpath.Synergy;
import com.acikek.purpeille.warpath.Tone;
import com.google.gson.JsonObject;
import net.minecraft.entity.attribute.EntityAttribute;
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

    public EntityAttribute attribute;
    public final Identifier attributeId;
    public Ingredient affinity;
    public Map<Identifier, Synergy> synergy;
    public Abyssalite abyssalite;
    public EntityAttributeModifier.Operation operation;
    public int dyeColor;
    public boolean forceInt;
    public MutableText rite;

    public Revelation(Identifier id, Tone tone, int color, Ingredient catalyst, int index, double modifier, boolean ignoreSlot, List<Identifier> whitelist, Identifier attributeId, Ingredient affinity, Map<Identifier, Synergy> synergy, Abyssalite abyssalite, int dyeColor, boolean multiply, boolean forceInt) {
        super(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist);
        this.attributeId = attributeId;
        this.affinity = affinity;
        this.synergy = synergy;
        this.abyssalite = abyssalite;
        operation = multiply ? EntityAttributeModifier.Operation.MULTIPLY_TOTAL : EntityAttributeModifier.Operation.ADDITION;
        this.dyeColor = dyeColor == -1 ? getClosestDyeColor(waveColor) : dyeColor;
        this.forceInt = forceInt;
        rite = Text.translatable(getIdKey("rite", id)).styled(style -> style.withColor(RITE_RGB));
    }

    public Revelation(Aspect aspect, Identifier attributeId, Ingredient affinity, Map<Identifier, Synergy> synergy, Abyssalite abyssalite, int dyeColor, boolean multiply, boolean forceInt) {
        this(aspect.id, aspect.tone, aspect.color, aspect.catalyst, aspect.index, aspect.modifier, aspect.ignoreSlot, aspect.whitelist, attributeId, affinity, synergy, abyssalite, dyeColor, multiply, forceInt);
    }

    public void updateAttribute() {
        attribute = Registry.ATTRIBUTE.get(attributeId);
    }

    public static void finishAbyssaliteReload(boolean log) {
        AbyssalToken.clearTokens();
        List<Map.Entry<Identifier, Revelation>> hasAbyssalite = Component.REVELATIONS.entrySet().stream()
                .filter(pair -> pair.getValue().abyssalite != null)
                .toList();
        Set<Item> uniques = new HashSet<>();
        List<Item> duplicates = hasAbyssalite.stream()
                .filter(pair -> !uniques.add(pair.getValue().abyssalite.token()))
                .map(pair -> pair.getValue().abyssalite.token())
                .toList();
        for (Map.Entry<Identifier, Revelation> pair : hasAbyssalite) {
            if (duplicates.contains(pair.getValue().abyssalite.token())) {
                if (log) {
                    Identifier tokenId = Registry.ITEM.getId(pair.getValue().abyssalite.token());
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
            pair.getValue().updateAttribute();
            if (pair.getValue().attribute == null) {
                if (log) {
                    Purpeille.LOGGER.error("Revelation '" + pair.getKey() + "' has an invalid attribute ID: '" + pair.getValue().attributeId + "'");
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
        return new EntityAttributeModifier(WARPATH_ID, "Warpath modifier", adjusted, operation);
    }

    public static Revelation fromNbt(NbtCompound nbt) {
        return Type.REVELATION.getFromNbt(nbt, REVELATIONS);
    }

    public static class Builder extends Aspect.Builder {

        Identifier attributeId;
        Ingredient affinity;
        Map<Identifier, Synergy> synergy;
        Abyssalite abyssalite;
        int dyeColor = -1;
        boolean multiply;
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

        public Builder attribute(Identifier attributeId) {
            this.attributeId = attributeId;
            return this;
        }

        public Builder attribute(EntityAttribute attribute) {
            return attribute(Registry.ATTRIBUTE.getId(attribute));
        }

        public Builder affinity(Ingredient affinity) {
            this.affinity = affinity;
            return this;
        }

        public Builder synergy(Map<Identifier, Synergy> synergy) {
            this.synergy = synergy;
            return this;
        }

        public Builder abyssalite(Abyssalite abyssalite) {
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

        public Builder multiply(boolean multiply) {
            this.multiply = multiply;
            return this;
        }

        public Builder forceInt(boolean forceInt) {
            this.forceInt = forceInt;
            return this;
        }

        @Override
        boolean isValid(Identifier id) {
            Objects.requireNonNull(attributeId);
            Objects.requireNonNull(affinity);
            if (dyeColor < -1 || dyeColor >= DyeColor.values().length) {
                throw new IllegalStateException("'dyeColor' must be a valid dye color ID or -1");
            }
            return super.isValid(id);
        }

        public Revelation buildRevelation(Identifier id) {
            if (isValid(id)) {
                return new Revelation(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist, attributeId, affinity, synergy, abyssalite, dyeColor, multiply, forceInt);
            }
            return null;
        }
    }

    public static Revelation fromJson(JsonObject obj, Identifier id) {
        return new Builder()
                .aspect(Aspect.fromJson(obj, id))
                .attribute(Identifier.tryParse(JsonHelper.getString(obj, "attribute")))
                .affinity(Ingredient.fromJson(obj.get("affinity")))
                .synergy(Synergy.overridesFromJson(JsonHelper.getObject(obj, "synergy", null)))
                .abyssalite(Abyssalite.fromJson(JsonHelper.getObject(obj, "abyssalite", null)))
                .dyeColor(JsonHelper.getInt(obj, "dye_color", -1))
                .multiply(JsonHelper.getBoolean(obj, "multiply"))
                .forceInt(JsonHelper.getBoolean(obj, "force_int", false))
                .buildRevelation(id);
    }

    public static Revelation read(PacketByteBuf buf) {
        Aspect aspect = Aspect.read(buf);
        Identifier attributeId = buf.readIdentifier();
        Ingredient affinity = Ingredient.fromPacket(buf);
        Map<Identifier, Synergy> synergy = buf.readBoolean() ? buf.readMap(PacketByteBuf::readIdentifier, Synergy::read) : null;
        Abyssalite abyssalite = buf.readBoolean() ? Abyssalite.read(buf) : null;
        int dyeColor = buf.readInt();
        boolean multiply = buf.readBoolean();
        boolean forceInt = buf.readBoolean();
        return new Revelation(aspect, attributeId, affinity, synergy, abyssalite, dyeColor, multiply, forceInt);
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeIdentifier(attributeId);
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
        buf.writeBoolean(operation == EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        buf.writeBoolean(forceInt);
    }
}
