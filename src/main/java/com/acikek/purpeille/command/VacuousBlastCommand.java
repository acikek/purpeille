package com.acikek.purpeille.command;

import com.acikek.purpeille.block.ancient.guardian.AncientGuardianBlockEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class VacuousBlastCommand {

    public static final String NAME = "vblast";

    public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity entity = EntityArgumentType.getEntity(context, "target");
        PacketByteBuf buf = AncientGuardianBlockEntity.getBasePacket(entity);
        if (entity instanceof ServerPlayerEntity player) {
            AncientGuardianBlockEntity.sendActivation(player, buf, null, false, true);
        }
        AncientGuardianBlockEntity.sendActivationNearby(entity, buf, null, false, true);
        return 0;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal(NAME)
                .then(CommandManager.argument("target", EntityArgumentType.entity())
                        .executes(VacuousBlastCommand::execute))
                .requires(source -> source.hasPermissionLevel(4)));
    }
}
