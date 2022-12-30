package com.acikek.purpeille.impl;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.advancement.ModCriteria;
import com.acikek.purpeille.api.abyssal.AmalgamatedSpyglass;
import com.acikek.purpeille.item.ModItems;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AmalgamatedSpyglassImpl implements ServerPlayNetworking.PlayChannelHandler {

    public static final Identifier CHANNEL = Purpeille.id("amalgamated_spyglass_used");

    public static boolean isUsingAmalgamatedSpyglass(LivingEntity livingEntity) {
        return livingEntity.isUsingItem() && livingEntity.getActiveItem().isOf(ModItems.AMALGAMATED_SPYGLASS);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        AmalgamatedSpyglass.ITEM_OBSERVED.invoker().onObserved(player, buf.readItemStack(), buf.readBoolean());
    }

    public static void register() {
        AmalgamatedSpyglassImpl impl = new AmalgamatedSpyglassImpl();
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL, impl);
        AmalgamatedSpyglass.ITEM_OBSERVED.register(ModCriteria::triggerAmalgamatedSpyglassUsed);
    }
}
