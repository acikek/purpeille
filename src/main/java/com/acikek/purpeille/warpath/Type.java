package com.acikek.purpeille.warpath;

import com.acikek.purpeille.command.WarpathCommand;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;

import java.util.function.Function;

public enum Type {

    REVELATION("revelation", "Revelation"),
    ASPECT("aspect", "Aspect");

    public String translationKey;
    public String nbtKey;
    public MutableText exceptionMessage;

    Type(String translationKey, String nbtSuffix) {
        this.translationKey = translationKey;
        nbtKey = "Warpath" + nbtSuffix;
        exceptionMessage = WarpathCommand.getMessage("invalid." + translationKey);
    }

    public boolean hasNbt(ItemStack stack) {
        return stack.hasNbt() && stack.getOrCreateNbt().contains(nbtKey);
    }

    public <T extends Enum<T>> T getFromNbt(ItemStack stack, T[] values) {
        return hasNbt(stack)
                ? values[stack.getOrCreateNbt().getInt(nbtKey)]
                : null;
    }

    public void addNbt(ItemStack stack, int index) {
        stack.getOrCreateNbt().putInt(nbtKey, index);
    }

    public void removeNbt(ItemStack stack) {
        stack.getOrCreateNbt().remove(nbtKey);
    }

    public <T extends Enum<T>> T parseArgument(CommandContext<ServerCommandSource> ctx, Function<String, T> valueOf) {
        try {
            return valueOf.apply(StringArgumentType.getString(ctx, translationKey).toUpperCase());
        }
        catch (Exception e) {
            return null;
        }
    }
}
