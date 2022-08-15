package com.acikek.purpeille.world.reload;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Component;
import com.acikek.purpeille.warpath.component.Revelation;
import com.github.clevernucleus.dataattributes.api.event.AttributesReloadedEvent;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ReloadHandler {

    public static boolean DATA_ATTRIBUTES;

    public static <T extends Component> List<PacketByteBuf> getReloadBufs(Map<Identifier, T> registry) {
        boolean start = true;
        List<PacketByteBuf> bufs = new ArrayList<>();
        for (Map.Entry<Identifier, T> entry : registry.entrySet()) {
            T component = entry.getValue();
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(start);
            if (start) {
                start = false;
            }
            buf.writeIdentifier(entry.getKey());
            component.write(buf);
            bufs.add(buf);
        }
        return bufs;
    }

    public static <T extends Component> void handleComponentReload(String key, Map<Identifier, T> registry, BiFunction<JsonObject, Identifier, T> fromJson, boolean revelation) {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ComponentReloader<>(key, registry, fromJson));
        Identifier componentId = Purpeille.id(key);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (server == null) {
                return;
            }
            List<PacketByteBuf> bufs = getReloadBufs(registry);
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            for (PacketByteBuf buf : bufs) {
                for (ServerPlayerEntity player : players) {
                    ServerPlayNetworking.send(player, componentId, buf);
                }
            }
            if (revelation && !DATA_ATTRIBUTES) {
                for (ServerPlayerEntity player : players) {
                    ServerPlayNetworking.send(player, Revelation.FINISH_RELOAD, PacketByteBufs.empty());
                }
                Revelation.finishReload(true);
            }
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            List<PacketByteBuf> bufs = getReloadBufs(registry);
            for (PacketByteBuf buf : bufs) {
                ServerPlayNetworking.send(handler.player, componentId, buf);
            }
            if (revelation) {
                ServerPlayNetworking.send(handler.player, Revelation.FINISH_RELOAD, PacketByteBufs.empty());
            }
        });
    }

    public static void register() {
        DATA_ATTRIBUTES = FabricLoader.getInstance().isModLoaded("dataattributes");
        handleComponentReload("revelations", Component.REVELATIONS, Revelation::fromJson, true);
        handleComponentReload("aspects", Component.ASPECTS, Aspect::fromJson, false);
        if (DATA_ATTRIBUTES) {
            AttributesReloadedEvent.EVENT.register(() -> {
                Purpeille.LOGGER.info("Updating revelations");
                Revelation.finishReload(true);
            });
        }
    }
}
