package com.acikek.purpeille.client;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.ancient.guardian.AncientGuardian;
import com.acikek.purpeille.client.networking.AncientGuardianActivationListener;
import com.acikek.purpeille.client.networking.VacuousBlastListener;
import com.acikek.purpeille.client.particle.AncientGuardianParticle;
import com.acikek.purpeille.client.particle.ModParticleTypes;
import com.acikek.purpeille.client.render.AncientGuardianRenderer;
import com.acikek.purpeille.warpath.component.Aspect;
import com.acikek.purpeille.warpath.component.Component;
import com.acikek.purpeille.warpath.component.Revelation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientWorldTickEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourcePackActivationType;

import java.util.Map;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class PurpeilleClient implements ClientModInitializer {

    public static final ModelIdentifier MODEL = new ModelIdentifier("purpeille:ancient_guardian_in_hand#inventory");

    @Override
    public void onInitializeClient(ModContainer mod) {
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(MODEL));
        AncientGuardianRenderer.register();
        ModParticleTypes.register();
        AncientGuardianParticle.register();
        ClientWorldTickEvents.START.register((client, world) -> AncientGuardianRenderer.tick());
        ClientPlayNetworking.registerGlobalReceiver(AncientGuardian.ANCIENT_GUARDIAN_ACTIVATED, new AncientGuardianActivationListener());
        ClientPlayNetworking.registerGlobalReceiver(AncientGuardian.VACUOUS_BLAST, new VacuousBlastListener());
        registerPacks();
        handleReload("revelations", Component.REVELATIONS, Revelation::read);
        handleReload("aspects", Component.ASPECTS, Aspect::read);
    }

    public static void registerPack(ModContainer mod, String key, ResourcePackActivationType type) {
        ResourceLoader.registerBuiltinResourcePack(Purpeille.id(key), mod, type, new TranslatableText("pack.purpeille." + key));
    }

    public static void registerPacks() {
        QuiltLoader.getModContainer(Purpeille.ID)
                .ifPresent(mod -> {
                    registerPack(mod, "old", ResourcePackActivationType.NORMAL);
                    registerPack(mod, "theinar", ResourcePackActivationType.ALWAYS_ENABLED);
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
