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

import java.util.Map;
import java.util.UUID;

public class AttributeData {

    public EntityAttribute value;
    public Identifier id;
    public EntityAttributeModifier.Operation operation;
    public Map<EquipmentSlot, UUID> uuids;

    public AttributeData(Identifier id, boolean multiply) {
        this.id = id;
        operation = multiply ? EntityAttributeModifier.Operation.MULTIPLY_TOTAL : EntityAttributeModifier.Operation.ADDITION;
        uuids = ModAttributes.getEquipmentSlotUUIDMap(id.toString());
    }

    public boolean finishReload() {
        value = Registry.ATTRIBUTE.get(id);
        return value != null;
    }

    public EntityAttributeModifier getModifier(EquipmentSlot slot, String name, double value) {
        return new EntityAttributeModifier(uuids.get(slot), name, value, operation);
    }

    public static AttributeData fromJson(JsonObject obj) {
        Identifier id = Identifier.tryParse(JsonHelper.getString(obj, "id"));
        boolean multiply = JsonHelper.getBoolean(obj, "multiply");
        return new AttributeData(id, multiply);
    }

    public static AttributeData read(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        boolean operation = buf.readBoolean();
        return new AttributeData(id, operation);
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(id);
        buf.writeBoolean(operation == EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
