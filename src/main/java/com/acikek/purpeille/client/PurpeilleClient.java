package com.acikek.purpeille.client;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Component;
import com.acikek.purpeille.warpath.component.Revelation;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Function;

public class PurpeilleClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance()
                .getModContainer(Purpeille.ID)
                .ifPresent(mod -> {
                    registerPack(mod, "old", ResourcePackActivationType.NORMAL);
                    registerPack(mod, "theinar", ResourcePackActivationType.ALWAYS_ENABLED);
                });
        handleReload(Component.REVELATIONS, Revelation::read);
        handleReload(Component.ASPECTS, Aspect::read);
    }

    public static void registerPack(ModContainer mod, String key, ResourcePackActivationType type) {
        ResourceManagerHelper.registerBuiltinResourcePack(Purpeille.id(key), mod, type);
    }

    public static <T extends Component> void handleReload(Map<Identifier, T> registry, Function<PacketByteBuf, T> read) {
        ClientPlayNetworking.registerGlobalReceiver(Purpeille.id("aspects"), (client, handler, buf, responseSender) -> {
            if (client.isInSingleplayer()) {
                return;
            }
            if (buf.readBoolean()) {
                registry.clear();
            }
            registry.put(buf.readIdentifier(), read.apply(buf));
        });
    }
}
