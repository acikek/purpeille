package com.acikek.purpeille.client.networking;

import com.acikek.purpeille.client.particle.ModParticleTypes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

public class AncientGuardianActivationListener implements ClientPlayNetworking.PlayChannelHandler {

    public static Entity getEntity(MinecraftClient client, PacketByteBuf buf) {
        if (client.world == null) {
            return null;
        }
        return client.world.getEntityById(buf.readInt());
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Entity entity = getEntity(client, buf);
        if (entity == null) {
            return;
        }
        if (client.player == entity) {
            client.gameRenderer.showFloatingItem(buf.readItemStack());
        }
        client.particleManager.addEmitter(entity, ModParticleTypes.ANCIENT_GUARDIAN, 30);
    }
}
