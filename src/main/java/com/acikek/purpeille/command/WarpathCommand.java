package com.acikek.purpeille.command;

import com.acikek.purpeille.warpath.Aspects;
import com.acikek.purpeille.warpath.Revelations;
import com.acikek.purpeille.warpath.Type;
import com.acikek.purpeille.warpath.Warpath;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

import java.util.concurrent.CompletableFuture;

public class WarpathCommand {

    public static final String NAME = "warpath";

    public static final MutableText INVALID_STACK = getMessage("invalid.stack");
    public static final MutableText ADD_SUCCESS = getMessage("success.add");
    public static final MutableText REMOVE_SUCCESS = getMessage("success.remove");

    public static int add(CommandContext<ServerCommandSource> context, boolean hasAspect) throws CommandSyntaxException {
        Revelations revelation = Type.REVELATION.parseArgument(context, Revelations::valueOf);
        if (revelation == null) {
            throw new SimpleCommandExceptionType(Type.REVELATION.exceptionMessage).create();
        }
        Aspects aspect = hasAspect ? Type.ASPECT.parseArgument(context, Aspects::valueOf) : null;
        if (hasAspect && aspect == null) {
            throw new SimpleCommandExceptionType(Type.ASPECT.exceptionMessage).create();
        }
        ItemStack stack = getStack(context);
        Warpath.remove(stack);
        Warpath.add(stack, revelation.value, hasAspect ? aspect.value : null);
        context.getSource().sendFeedback(ADD_SUCCESS, false);
        return 0;
    }

    public static int remove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Warpath.remove(getStack(context));
        context.getSource().sendFeedback(REMOVE_SUCCESS, false);
        return 0;
    }

    public static ItemStack getStack(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ItemStack stack = context.getSource().getPlayer().getMainHandStack();
        if (stack.isEmpty()) {
            throw new SimpleCommandExceptionType(INVALID_STACK).create();
        }
        return stack;
    }

    public static MutableText getMessage(String key) {
        return new TranslatableText("command.purpeille.warpath." + key);
    }

    public static <T extends Enum<?>> CompletableFuture<Suggestions> suggestEnum(T[] values, SuggestionsBuilder builder) {
        for (T value : values) {
            builder.suggest(value.name().toLowerCase());
        }
        return builder.buildFuture();
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal(NAME)
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("revelation", StringArgumentType.string())
                                .suggests((context, builder) -> suggestEnum(Revelations.values(), builder))
                                .then(CommandManager.argument("aspect", StringArgumentType.string())
                                        .suggests(((context, builder) -> suggestEnum(Aspects.values(), builder)))
                                        .executes(context -> WarpathCommand.add(context, true)))
                                .executes(context -> WarpathCommand.add(context, false))
                        ))
                .then(CommandManager.literal("remove")
                        .executes(WarpathCommand::remove)));
    }
}
