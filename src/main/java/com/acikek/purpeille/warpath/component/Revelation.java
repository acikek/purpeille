package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.warpath.Synergy;
import com.acikek.purpeille.warpath.Tone;
import com.acikek.purpeille.warpath.Type;
import com.google.gson.JsonObject;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Revelation extends Component {

    public static UUID WARPATH_ID = UUID.fromString("2c67c058-5d5e-4b39-98e3-b3eb9965f7eb");
    public static int RITE_RGB = 13421772;

    public EntityAttribute attribute;
    public Ingredient affinity;
    public Map<Identifier, Synergy> synergy;
    public EntityAttributeModifier.Operation operation;
    public boolean forceInt;
    public MutableText rite;

    public Revelation(Identifier id, Tone tone, int color, Ingredient catalyst, int index, double modifier, boolean ignoreSlot, List<Identifier> whitelist, EntityAttribute attribute, Ingredient affinity, Map<Identifier, Synergy> synergy, boolean multiply, boolean forceInt) {
        super(id, tone, color, catalyst, index, modifier, ignoreSlot, whitelist);
        this.attribute = attribute;
        this.affinity = affinity;
        this.synergy = synergy;
        operation = multiply ? EntityAttributeModifier.Operation.MULTIPLY_TOTAL : EntityAttributeModifier.Operation.ADDITION;
        this.forceInt = forceInt;
        rite = new TranslatableText(getIdKey("rite", id)).styled(style -> style.withColor(RITE_RGB));
    }

    public Revelation(Aspect aspect, EntityAttribute attribute, Ingredient affinity, Map<Identifier, Synergy> synergy, boolean multiply, boolean forceInt) {
        this(aspect.id, aspect.tone, aspect.color, aspect.catalyst, aspect.index, aspect.modifier, aspect.ignoreSlot, aspect.whitelist, attribute, affinity, synergy, multiply, forceInt);
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

    public static Revelation fromJson(JsonObject obj, Identifier id) {
        Aspect aspect = Aspect.fromJson(obj, id);
        EntityAttribute attribute = Registry.ATTRIBUTE.get(Identifier.tryParse(JsonHelper.getString(obj, "attribute")));
        Ingredient affinity = Ingredient.fromJson(obj.get("affinity"));
        Map<Identifier, Synergy> synergy = Synergy.overridesFromJson(JsonHelper.getObject(obj, "synergy"));
        boolean multiply = JsonHelper.getBoolean(obj, "multiply");
        boolean forceInt = JsonHelper.getBoolean(obj, "force_int", false);
        return new Revelation(aspect, attribute, affinity, synergy, multiply, forceInt);
    }

    public static Revelation read(PacketByteBuf buf) {
        Aspect aspect = Aspect.read(buf);
        EntityAttribute attribute = Registry.ATTRIBUTE.get(buf.readIdentifier());
        Ingredient affinity = Ingredient.fromPacket(buf);
        Map<Identifier, Synergy> synergy = Synergy.readOverrides(buf);
        boolean multiply = buf.readBoolean();
        boolean forceInt = buf.readBoolean();
        return new Revelation(aspect, attribute, affinity, synergy, multiply, forceInt);
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeIdentifier(Registry.ATTRIBUTE.getId(attribute));
        affinity.write(buf);
        Synergy.writeOverrides(synergy, buf);
        buf.writeBoolean(operation == EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        buf.writeBoolean(forceInt);
    }
}
