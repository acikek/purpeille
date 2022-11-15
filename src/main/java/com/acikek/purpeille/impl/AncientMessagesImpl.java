package com.acikek.purpeille.impl;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.api.amsg.AncientMessageData;
import com.acikek.purpeille.api.amsg.AncientMessages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;

public class AncientMessagesImpl implements ServerPlayNetworking.PlayChannelHandler {

    public static final Identifier CHANNEL = Purpeille.id("ancient_message");
    public static final Identifier COMPLETED_CHANNEL = Purpeille.id("ancient_message_completed");

    public static void message(Collection<ServerPlayerEntity> players, List<AncientMessageData> list, Identifier seriesId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(seriesId);
        buf.writeInt(list.size());
        for (AncientMessageData data : list) {
            data.write(buf);
        }
        for (ServerPlayerEntity player : players) {
            ServerPlayNetworking.send(player, CHANNEL, buf);
        }
    }

    public static AncientMessageData.Builder singleLang(String key, Object... args) {
        return new AncientMessageData.Builder()
                .lines(List.of(Text.translatable(key, args)));
    }

    public static AncientMessageData.Builder singleLang(String key) {
        return singleLang(key, new Object[] { });
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        AncientMessages.SERIES_COMPLETED.invoker().onSeriesCompleted(player, buf.readIdentifier());
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(COMPLETED_CHANNEL, new AncientMessagesImpl());
    }
}
