package com.acikek.purpeille.world.reload;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Component;
import com.acikek.purpeille.warpath.component.Revelation;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourceLoaderEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ReloadHandler {

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

    public static <T extends Component> void handleComponentReload(String key, Map<Identifier, T> registry, BiFunction<JsonObject, Identifier, T> fromJson) {
        ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(new ComponentReloader<>(key, registry, fromJson));
        Identifier componentId = Purpeille.id(key);
        ResourceLoaderEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (server == null) {
                return;
            }
            List<PacketByteBuf> bufs = getReloadBufs(registry);
            for (PacketByteBuf buf : bufs) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    ServerPlayNetworking.send(player, componentId, buf);
                }
            }
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            List<PacketByteBuf> bufs = getReloadBufs(registry);
            for (PacketByteBuf buf : bufs) {
                ServerPlayNetworking.send(handler.player, componentId, buf);
            }
        });
    }

    public static void register() {
        handleComponentReload("revelations", Component.REVELATIONS, Revelation::fromJson);
        handleComponentReload("aspects", Component.ASPECTS, Aspect::fromJson);
    }
}
