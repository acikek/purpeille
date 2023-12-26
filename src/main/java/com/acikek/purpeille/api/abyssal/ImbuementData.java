package com.acikek.purpeille.api.abyssal;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import java.util.HashSet;
import java.util.Set;

public class ImbuementData {

    public static final String KEY = "Imbuements";

    public int energy;
    public int count;
    public Set<Item> itemsUsed;

    public ImbuementData(int energy, int count, Set<Item> itemsUsed) {
        this.energy = energy;
        this.count = count;
        this.itemsUsed = itemsUsed;
    }

    public static int readEnergy(NbtCompound nbt) {
        return nbt.getInt("SpiritualEnergy");
    }

    public static Set<Item> readItemsUsed(NbtCompound nbt) {
        NbtList items = nbt.getList("ItemsUsed", NbtElement.STRING_TYPE);
        Set<Item> itemsUsed = new HashSet<>();
        for (NbtElement element : items) {
            itemsUsed.add(Registries.ITEM.get(Identifier.tryParse(element.asString())));
        }
        return itemsUsed;
    }

    public static ImbuementData readNbt(NbtCompound nbt) {
        int energy = readEnergy(nbt);
        int count = nbt.getInt("Count");
        Set<Item> itemsUsed = readItemsUsed(nbt);
        return new ImbuementData(energy, count, itemsUsed);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("SpiritualEnergy", energy);
        nbt.putInt("Count", count);
        NbtList items = new NbtList();
        for (Item used : itemsUsed) {
            items.add(NbtString.of(Registries.ITEM.getId(used).toString()));
        }
        nbt.put("ItemsUsed", items);
    }
}
