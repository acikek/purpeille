package com.acikek.purpeille.warpath;

import com.acikek.purpeille.attribute.ModAttributes;
import com.google.gson.JsonObject;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.EnumUtils;

import java.util.Map;
import java.util.UUID;

public class AttributeData {

    public EntityAttribute value;
    public Identifier id;
    public EquipmentSlot slot;
    public EntityAttributeModifier.Operation operation;
    public boolean forceInt;
    public Map<EquipmentSlot, UUID> uuids;

    public AttributeData(Identifier id, EquipmentSlot slot, boolean multiply, boolean forceInt) {
        this.id = id;
        this.slot = slot;
        operation = multiply ? EntityAttributeModifier.Operation.MULTIPLY_TOTAL : EntityAttributeModifier.Operation.ADDITION;
        this.forceInt = forceInt;
        uuids = ModAttributes.getEquipmentSlotUUIDMap(id.toString());
    }

    public boolean finishReload() {
        value = Registry.ATTRIBUTE.get(id);
        return value != null;
    }

    public EntityAttributeModifier getModifier(EquipmentSlot slot, String name, double value) {
        double adjusted = forceInt ? (int) value : value;
        return new EntityAttributeModifier(uuids.get(slot), name, adjusted, operation);
    }

    public static AttributeData fromJson(JsonObject obj) {
        Identifier id = new Identifier(JsonHelper.getString(obj, JsonHelper.hasString(obj, "attribute") ? "attribute" : "id"));
        EquipmentSlot slot = EnumUtils.getEnumIgnoreCase(EquipmentSlot.class, JsonHelper.getString(obj, "slot", null));
        boolean multiply = JsonHelper.getBoolean(obj, "multiply");
        boolean forceInt = JsonHelper.getBoolean(obj, "force_int", false);
        return new AttributeData(id, slot, multiply, forceInt);
    }

    public static AttributeData read(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        EquipmentSlot slot = buf.readBoolean() ? buf.readEnumConstant(EquipmentSlot.class) : null;
        boolean operation = buf.readBoolean();
        boolean forceInt = buf.readBoolean();
        return new AttributeData(id, slot, operation, forceInt);
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(id);
        buf.writeBoolean(slot != null);
        if (slot != null) {
            buf.writeEnumConstant(slot);
        }
        buf.writeBoolean(operation == EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        buf.writeBoolean(forceInt);
    }
}
