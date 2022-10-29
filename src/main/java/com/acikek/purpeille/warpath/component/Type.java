package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.command.WarpathCommand;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Map;

public enum Type {

    REVELATION("revelation", "Revelation"),
    ASPECT("aspect", "Aspect");

    public final String translationKey;
    public final String nbtKey;
    public final DynamicCommandExceptionType exception;

    Type(String translationKey, String nbtKey) {
        this.translationKey = translationKey;
        this.nbtKey = nbtKey;
        exception = WarpathCommand.getException("invalid." + translationKey);
    }


    public void addNbt(NbtCompound nbt, Identifier id) {
        nbt.putString(nbtKey, id.toString());
    }
}
