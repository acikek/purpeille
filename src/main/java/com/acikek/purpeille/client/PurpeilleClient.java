package com.acikek.purpeille.client;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.entity.ancient.guardian.AncientGuardian;
import com.acikek.purpeille.client.networking.AncientGuardianActivationListener;
import com.acikek.purpeille.client.networking.VacuousBlastListener;
import com.acikek.purpeille.client.particle.AncientGuardianParticle;
import com.acikek.purpeille.client.particle.ModParticleTypes;
import com.acikek.purpeille.client.render.AncientGuardianRenderer;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Component;
import com.acikek.purpeille.warpath.component.Revelation;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class PurpeilleClient implements ClientModInitializer {

    public static final ModelIdentifier MODEL = new ModelIdentifier("purpeille:ancient_guardian_in_hand#inventory");

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.END_RUBBLE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.END_RUBBLE, RenderLayer.getTranslucent());
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(MODEL));
        AncientGuardianRenderer.register();
        ModParticleTypes.register();
        AncientGuardianParticle.register();
        ClientTickEvents.START_WORLD_TICK.register(world -> AncientGuardianRenderer.tick());
        ClientPlayNetworking.registerGlobalReceiver(AncientGuardian.ANCIENT_GUARDIAN_ACTIVATED, new AncientGuardianActivationListener());
        ClientPlayNetworking.registerGlobalReceiver(AncientGuardian.VACUOUS_BLAST, new VacuousBlastListener());
        registerPacks();
        handleReload("revelations", Component.REVELATIONS, Revelation::read);
        handleReload("aspects", Component.ASPECTS, Aspect::read);
    }

    public static void registerPack(ModContainer mod, String key, String name, ResourcePackActivationType type) {
        ResourceManagerHelper.registerBuiltinResourcePack(Purpeille.id(key), mod, name, type);
    }

    public static void registerPacks() {
        FabricLoader.getInstance()
                .getModContainer(Purpeille.ID)
                .ifPresent(mod -> {
                    registerPack(mod, "old", "Purpeille Legacy", ResourcePackActivationType.NORMAL);
                    registerPack(mod, "theinar", "Theinar Language", ResourcePackActivationType.ALWAYS_ENABLED);
                });
    }

    public static <T extends Component> void handleReload(String key, Map<Identifier, T> registry, Function<PacketByteBuf, T> read) {
        ClientPlayNetworking.registerGlobalReceiver(Purpeille.id(key), (client, handler, buf, responseSender) -> {
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
