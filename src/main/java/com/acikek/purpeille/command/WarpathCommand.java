package com.acikek.purpeille.command;

import com.acikek.purpeille.warpath.Aspects;
import com.acikek.purpeille.warpath.Revelations;
import com.acikek.purpeille.warpath.Type;
import com.acikek.purpeille.warpath.Warpath;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class WarpathCommand {

    public static final String NAME = "warpath";

    public static final DynamicCommandExceptionType INVALID_STACK = getException("invalid.stack");

    public static final String ADD_SUCCESS = "success.add";
    public static final String REMOVE_SUCCESS = "success.remove";

    public static int add(CommandContext<ServerCommandSource> context, boolean hasAspect) throws CommandSyntaxException {
        Revelations revelation = parseComponent(context, Type.REVELATION, Revelations::valueOf);
        Aspects aspect = hasAspect ? parseComponent(context, Type.ASPECT, Aspects::valueOf) : null;
        ItemStack stack = getStack(context);
        Warpath.remove(stack);
        Warpath.add(stack, revelation.value, hasAspect ? aspect.value : null);
        context.getSource().sendFeedback(getMessage(ADD_SUCCESS, Warpath.getWarpath(revelation, aspect)), false);
        return 0;
    }

    public static int remove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ItemStack stack = getStack(context);
        Text warpath = Warpath.getWarpath(stack);
        Warpath.remove(stack);
        context.getSource().sendFeedback(getMessage(REMOVE_SUCCESS, warpath), false);
        return 0;
    }

    public static <T extends Enum<T>> T parseComponent(CommandContext<ServerCommandSource> context, Type type, Function<String, T> valueOf) throws CommandSyntaxException {
        String input = StringArgumentType.getString(context, type.translationKey);
        try {
            return valueOf.apply(input.toUpperCase());
        }
        catch (Exception e) {
            throw type.exception.create(input.toLowerCase());
        }
    }

    public static ItemStack getStack(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        LivingEntity entity = (LivingEntity) EntityArgumentType.getEntity(context, "targets");
        ItemStack stack = entity.getMainHandStack();
        if (stack.isEmpty()) {
            throw INVALID_STACK.create(entity.getName().getString());
        }
        return stack;
    }

    public static MutableText getMessage(String key, Object value) {
        return new TranslatableText("command.purpeille.warpath." + key, value);
    }

    public static DynamicCommandExceptionType getException(String key) {
        return new DynamicCommandExceptionType(value -> getMessage(key, value));
    }

    public static <T extends Enum<?>> CompletableFuture<Suggestions> suggestEnum(T[] values, SuggestionsBuilder builder) {
        for (T value : values) {
            builder.suggest(value.name().toLowerCase());
        }
        return builder.buildFuture();
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal(NAME)
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("revelation", StringArgumentType.string())
                                        .suggests((context, builder) -> suggestEnum(Revelations.values(), builder))
                                        .then(CommandManager.argument("aspect", StringArgumentType.string())
                                                .suggests(((context, builder) -> suggestEnum(Aspects.values(), builder)))
                                                .executes(context -> WarpathCommand.add(context, true)))
                                        .executes(context -> WarpathCommand.add(context, false))
                                ))
                        .then(CommandManager.literal("remove")
                                .executes(WarpathCommand::remove))));
    }
}
