package com.acikek.purpeille.client.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class VacuousBlastListener implements ClientPlayNetworking.PlayChannelHandler {

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Entity entity = AncientGuardianActivationListener.getEntity(client, buf);
        if (entity == null || client.world == null) {
            return;
        }
        client.world.playSoundAtBlockCenter(entity.getBlockPos(), SoundEvents.AMBIENT_NETHER_WASTES_MOOD.value(), SoundCategory.AMBIENT, 5.0f, 1.0f, false);
        for (int i = 0; i < 90; i++) {
            float angle = i / 90.0f * MathHelper.TAU;
            float x = MathHelper.cos(angle);
            float z = MathHelper.sin(angle);
            Vec3d pos = entity.getPos().add(x * -1.3, 1.0, z * -1.3);
            client.particleManager.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y, pos.z, x, 0, z);
        }
    }
}
