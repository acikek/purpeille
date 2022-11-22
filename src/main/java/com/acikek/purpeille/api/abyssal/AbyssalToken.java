package com.acikek.purpeille.api.abyssal;

import com.acikek.purpeille.attribute.ModAttributes;
import com.acikek.purpeille.warpath.Warpath;
import com.acikek.purpeille.warpath.WarpathData;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface AbyssalToken {

    Revelation getRevelation();

    void setRevelation(Revelation revelation);

    default boolean isAbyssalToken() {
        return getRevelation() != null;
    }
}
