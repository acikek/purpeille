package com.acikek.purpeille.client;

import com.acikek.purpeille.Purpeille;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

public class PurpeilleClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance()
                .getModContainer(Purpeille.ID)
                .ifPresent(mod -> ResourceManagerHelper.registerBuiltinResourcePack(
                        Purpeille.id("theinar"),
                        mod,
                        ResourcePackActivationType.ALWAYS_ENABLED
                ));
    }
}
