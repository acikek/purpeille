package com.acikek.purpeille.client.networking;

import com.acikek.purpeille.client.particle.ModParticleTypes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

public class AncientGuardianActivationListener implements ClientPlayNetworking.PlayChannelHandler {

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.world == null) {
            return;
        }
        Entity entity = client.world.getEntityById(buf.readInt());
        if (client.player == entity) {
            client.gameRenderer.showFloatingItem(buf.readItemStack());
        }
        client.particleManager.addEmitter(entity, ModParticleTypes.ANCIENT_GUARDIAN, 30);
    }
}
