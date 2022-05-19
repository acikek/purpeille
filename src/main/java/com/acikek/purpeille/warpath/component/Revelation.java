package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.warpath.Synergy;
import com.acikek.purpeille.warpath.Tone;
import com.acikek.purpeille.warpath.Type;
import com.google.gson.JsonObject;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.UUID;

public class Revelation extends Component {

    public static UUID WARPATH_ID = UUID.fromString("2c67c058-5d5e-4b39-98e3-b3eb9965f7eb");
    public static int RITE_RGB = 13421772;

    public EntityAttribute attribute;
    public Item affinity;
    public Map<Identifier, Synergy> synergy;
    public EntityAttributeModifier.Operation operation;
    public MutableText rite;

    public Revelation(Identifier id, Tone tone, int color, Item catalyst, int index, double modifier, boolean ignoreSlot, EntityAttribute attribute, Item affinity, Map<Identifier, Synergy> synergy, boolean multiply) {
        super(id, tone, color, catalyst, index, modifier, ignoreSlot);
        this.attribute = attribute;
        this.affinity = affinity;
        this.synergy = synergy;
        operation = multiply ? EntityAttributeModifier.Operation.MULTIPLY_TOTAL : EntityAttributeModifier.Operation.ADDITION;
        rite = new TranslatableText("rite.purpeille." + id.getPath()).styled(style -> style.withColor(RITE_RGB));
    }

    public Revelation(Aspect aspect, EntityAttribute attribute, Item affinity, Map<Identifier, Synergy> synergy, boolean multiply) {
        this(aspect.id, aspect.tone, aspect.color, aspect.catalyst, aspect.index, aspect.modifier, aspect.ignoreSlot, attribute, affinity, synergy, multiply);
    }

    @Override
    public Type getType() {
        return Type.REVELATION;
    }

    public double getModifierValue(ItemStack stack, Aspect aspect) {
        double value = stack.isOf(affinity) ? modifier * 1.2 : modifier;
        if (aspect == null) {
            return value;
        }
        return value * aspect.modifier * Synergy.getSynergy(this, aspect).modifier;
    }

    public EntityAttributeModifier getModifier(ItemStack stack, Aspect aspect) {
        return new EntityAttributeModifier(WARPATH_ID, "Warpath modifier", getModifierValue(stack, aspect), operation);
    }

    public static Revelation fromNbt(NbtCompound nbt) {
        return Type.REVELATION.getFromNbt(nbt, REVELATIONS);
    }

    public static Revelation fromJson(JsonObject obj, Identifier id) {
        Aspect aspect = Aspect.fromJson(obj, id);
        EntityAttribute attribute = Registry.ATTRIBUTE.get(Identifier.tryParse(JsonHelper.getString(obj, "attribute")));
        Item affinity = ShapedRecipe.getItem(JsonHelper.getObject(obj, "affinity"));
        Map<Identifier, Synergy> synergy = Synergy.overridesFromJson(JsonHelper.getObject(obj, "synergy"));
        boolean multiply = JsonHelper.getBoolean(obj, "multiply");
        return new Revelation(aspect, attribute, affinity, synergy, multiply);
    }

    public static Revelation read(PacketByteBuf buf) {
        Aspect aspect = Aspect.read(buf);
        EntityAttribute attribute = Registry.ATTRIBUTE.get(buf.readIdentifier());
        Item affinity = Registry.ITEM.get(buf.readIdentifier());
        Map<Identifier, Synergy> synergy = Synergy.readOverrides(buf);
        boolean multiply = buf.readBoolean();
        return new Revelation(aspect, attribute, affinity, synergy, multiply);
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeIdentifier(Registry.ATTRIBUTE.getId(attribute));
        buf.writeIdentifier(Registry.ITEM.getId(affinity));
        Synergy.writeOverrides(synergy, buf);
        buf.writeBoolean(operation == EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
