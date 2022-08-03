package com.acikek.purpeille.command;

import com.acikek.purpeille.Purpeille;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class AncientMessageCommand {

    public static final Identifier CHANNEL = Purpeille.id("ancient_message");
    public static final String NAME = "amsg";

    public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "targets");
        Text text = TextArgumentType.getTextArgument(context, "text");
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeText(text);
        for (ServerPlayerEntity player : players) {
            ServerPlayNetworking.send(player, CHANNEL, buf);
        }
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
