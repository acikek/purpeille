package com.acikek.purpeille.client;

import com.acikek.purpeille.Purpeille;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class PurpeilleClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance()
                .getModContainer(Purpeille.ID)
                .ifPresent(mod -> {
                    registerPack(mod, "old", ResourcePackActivationType.NORMAL);
                    registerPack(mod, "theinar", ResourcePackActivationType.ALWAYS_ENABLED);
                });
    }

    public void registerPack(ModContainer mod, String key, ResourcePackActivationType type) {
        ResourceManagerHelper.registerBuiltinResourcePack(Purpeille.id(key), mod, type);
    }
}
