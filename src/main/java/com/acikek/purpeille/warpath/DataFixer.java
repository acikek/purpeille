package com.acikek.purpeille.warpath;

import com.acikek.purpeille.Purpeille;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class DataFixer {

    public static final String REVELATION_KEY = "WarpathRevelation";
    public static final String ASPECT_KEY = "WarpathAspect";

    public static final String[] REVELATIONS = {
        "spirit",
        "vigor",
        "totality",
        "avarice",
        "malaise",
        "terror",
        "bound",
        "pace",
        "immersion"
    };

    public static final String[] ASPECTS = {
        "heroic",
        "excess",
        "virtuous",
        "terran",
        "shocking",
        "deathly",
        "limitless",
        "tranquil",
        "unrivaled"
    };

    public static Identifier getId(String[] values, int index) {
        return Purpeille.id(values[MathHelper.clamp(index, 0, values.length - 1)]);
    }

    public static boolean hasOldData(ItemStack stack) {
        return stack.hasNbt() && stack.getOrCreateNbt().contains(REVELATION_KEY);
    }

    public static void fixData(NbtCompound nbt) {
        Identifier revelationId = getId(REVELATIONS, nbt.getInt(REVELATION_KEY));
        boolean hasAspect = nbt.contains(ASPECT_KEY);
        Identifier aspectId = hasAspect ? getId(ASPECTS, nbt.getInt(ASPECT_KEY)) : null;
        nbt.remove(REVELATION_KEY);
        if (hasAspect) {
            nbt.remove(ASPECT_KEY);
        }
        NbtCompound data = new NbtCompound();
        Warpath.addData(data, revelationId, aspectId);
        nbt.put(Warpath.NBT_KEY, data);
    }
}
