package com.acikek.purpeille.warpath.attribute;

import com.acikek.purpeille.api.warpath.Components;
import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.warpath.component.Revelation;
import com.google.gson.JsonObject;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.registry.Registries;
import org.apache.commons.lang3.EnumUtils;

public class AttributeData extends AttributeScalingData {

    public EntityAttribute value;
    public Identifier id;
    public EquipmentSlot slot;
    public EntityAttributeModifier.Operation operation;
    public boolean forceInt;

    public AttributeData(Identifier id, EquipmentSlot slot, boolean multiply, boolean forceInt, double scalingMultiplier, double scalingDropoff) {
        super(scalingMultiplier, scalingDropoff, ModAttributes.getEquipmentSlotUUIDMap(id.toString()));
        this.id = id;
        this.slot = slot;
        operation = multiply ? EntityAttributeModifier.Operation.MULTIPLY_TOTAL : EntityAttributeModifier.Operation.ADDITION;
        this.forceInt = forceInt;
        this.scalingMultiplier = scalingMultiplier;
        this.scalingDropoff = scalingDropoff;
    }

    public boolean finishReload() {
        value = Registries.ATTRIBUTE.get(id);
        return value != null;
    }

    public EntityAttributeModifier getModifier(EquipmentSlot slot, String name, double value) {
        double adjusted = forceInt ? (int) value : value;
        return new EntityAttributeModifier(uuidMap.get(slot), name, adjusted, operation);
    }

    public static AttributeData fromJson(JsonObject obj) {
        Identifier id = new Identifier(JsonHelper.getString(obj, JsonHelper.hasString(obj, "attribute") ? "attribute" : "id"));
        EquipmentSlot slot = EnumUtils.getEnumIgnoreCase(EquipmentSlot.class, JsonHelper.getString(obj, "slot", null));
        boolean multiply = JsonHelper.getBoolean(obj, "multiply");
        boolean forceInt = JsonHelper.getBoolean(obj, "force_int", false);
        double scalingMultiplier = JsonHelper.getDouble(obj, "scaling_multiplier", 1.2);
        double scalingDropoff = JsonHelper.getDouble(obj, "scaling_dropoff", 2.0);
        return new AttributeData(id, slot, multiply, forceInt, scalingMultiplier, scalingDropoff);
    }

    public static AttributeData read(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        EquipmentSlot slot = buf.readBoolean() ? buf.readEnumConstant(EquipmentSlot.class) : null;
        boolean operation = buf.readBoolean();
        boolean forceInt = buf.readBoolean();
        double scalingMultiplier = buf.readDouble();
        double scalingDropoff = buf.readDouble();
        return new AttributeData(id, slot, operation, forceInt, scalingMultiplier, scalingDropoff);
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(id);
        buf.writeBoolean(slot != null);
        if (slot != null) {
            buf.writeEnumConstant(slot);
        }
        buf.writeBoolean(operation == EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        buf.writeBoolean(forceInt);
        buf.writeDouble(scalingMultiplier);
        buf.writeDouble(scalingDropoff);
    }
}
