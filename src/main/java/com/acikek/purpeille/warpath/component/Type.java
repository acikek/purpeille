package com.acikek.purpeille.warpath.component;

import com.acikek.purpeille.command.WarpathCommand;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Map;

public enum Type {

    REVELATION("revelation"),
    ASPECT("aspect");

    public final String translationKey;
    public final DynamicCommandExceptionType exception;

    Type(String translationKey) {
        this.translationKey = translationKey;
        exception = WarpathCommand.getException("invalid." + translationKey);
    }
}
