package com.acikek.purpeille.command;

import com.acikek.purpeille.warpath.component.Type;
import com.acikek.purpeille.warpath.Warpath;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Component;
import com.acikek.purpeille.warpath.component.Revelation;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WarpathCommand {

    public static final String NAME = "warpath";

    public static final MutableText INVALID_WARPATH = getMessage("invalid.warpath", null);
    public static final DynamicCommandExceptionType INVALID_STACK = getException("invalid.stack");

    public static final String ADD_SUCCESS = "success.add";
    public static final String REMOVE_SUCCESS = "success.remove";

    public static int add(CommandContext<ServerCommandSource> context, boolean hasAspect) throws CommandSyntaxException {
        Revelation revelation = parseComponent(context, Type.REVELATION, Component.REVELATIONS);
        Aspect aspect = hasAspect ? parseComponent(context, Type.ASPECT, Component.ASPECTS) : null;
        ItemStack stack = getStack(context);
        Warpath.remove(stack);
        Warpath.add(stack, revelation, hasAspect ? aspect : null);
        context.getSource().sendFeedback(getMessage(ADD_SUCCESS, Warpath.getTooltip(revelation, aspect, false, false).get(0)), false);
        return 0;
    }

    public static int remove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ItemStack stack = getStack(context);
        List<Text> warpath = Warpath.getTooltip(stack, false, false);
        if (warpath == null) {
            throw new SimpleCommandExceptionType(INVALID_WARPATH).create();
        }
        Warpath.remove(stack);
        context.getSource().sendFeedback(getMessage(REMOVE_SUCCESS, warpath.get(0)), false);
        return 0;
    }

    public static <T extends Component> T parseComponent(CommandContext<ServerCommandSource> context, Type type, Map<Identifier, T> registry) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(context, type.translationKey);
        if (!registry.containsKey(id)) {
            throw type.exception.create(id.toString());
        }
        return registry.get(id);
    }

    public static ItemStack getStack(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        LivingEntity entity = (LivingEntity) EntityArgumentType.getEntity(context, "target");
        ItemStack stack = entity.getMainHandStack();
        if (stack.isEmpty()) {
            throw INVALID_STACK.create(entity.getName().getString());
        }
        return stack;
    }

    public static MutableText getMessage(String key, Object value) {
        return Text.translatable("command.purpeille.warpath." + key, value);
    }

    public static DynamicCommandExceptionType getException(String key) {
        return new DynamicCommandExceptionType(value -> getMessage(key, value));
    }

    public static <T extends Component> CompletableFuture<Suggestions> suggestRegistry(Map<Identifier, T> registry, SuggestionsBuilder builder) {
        for (Identifier id : registry.keySet()) {
            builder.suggest(id.toString());
        }
        return builder.buildFuture();
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal(NAME)
                .then(CommandManager.argument("target", EntityArgumentType.entity())
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("revelation", IdentifierArgumentType.identifier())
                                        .suggests((context, builder) -> suggestRegistry(Component.REVELATIONS, builder))
                                        .then(CommandManager.argument("aspect", IdentifierArgumentType.identifier())
                                                .suggests(((context, builder) -> suggestRegistry(Component.ASPECTS, builder)))
                                                .executes(context -> WarpathCommand.add(context, true)))
                                        .executes(context -> WarpathCommand.add(context, false))
                                ))
                        .then(CommandManager.literal("remove")
                                .executes(WarpathCommand::remove)))
                .requires(source -> source.hasPermissionLevel(4)));
    }
}
