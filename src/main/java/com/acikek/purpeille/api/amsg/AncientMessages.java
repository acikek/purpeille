package com.acikek.purpeille.api.amsg;

import com.acikek.purpeille.Purpeille;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;

public class AncientMessages {

    public static final Identifier CHANNEL = Purpeille.id("ancient_message");

    public static void message(Collection<ServerPlayerEntity> players, List<AncientMessageData> list) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(list.size());
        for (AncientMessageData data : list) {
            data.write(buf);
        }
        for (ServerPlayerEntity player : players) {
            ServerPlayNetworking.send(player, CHANNEL, buf);
        }
    }
}
