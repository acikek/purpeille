package com.acikek.purpeille.command;

import com.acikek.purpeille.api.amsg.AncientMessageData;
import com.acikek.purpeille.api.amsg.AncientMessages;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

// TODO: Create an argument type suited for list of AncientMessageData
public class AncientMessageCommand {

    public static final String NAME = "amsg";

    public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "targets");
        Text text = TextArgumentType.getTextArgument(context, "text");
        AncientMessages.message(players, List.of(new AncientMessageData(List.of(text, text, text))));
        return 0;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal(NAME)
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.argument("text", TextArgumentType.text())
                                .executes(AncientMessageCommand::execute)))
                .requires(source -> source.hasPermissionLevel(4)));
    }
}
