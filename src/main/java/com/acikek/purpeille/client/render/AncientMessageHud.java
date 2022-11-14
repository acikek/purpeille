package com.acikek.purpeille.client.render;

import com.acikek.purpeille.api.amsg.AncientMessageData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class AncientMessageHud implements HudRenderCallback {

    public static boolean started;
    public static List<AncientMessageData> messages = new ArrayList<>();
    public static List<Integer> ticks = new ArrayList<>();
    public static int dividerTicks;

    public float[] getShake(float length, Random random) {
        float angle = random.nextFloat() * MathHelper.TAU;
        return new float[] { MathHelper.cos(angle) * length, MathHelper.sin(angle) * length };
    }

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) {
            return;
        }
        // If the process has just started, initialize tick counts for each message
        if (started) {
            for (AncientMessageData data : messages) {
                ticks.add(data.getTicks());
            }
            started = false;
        }
        boolean paused = client.isPaused();
        // If there are divider ticks present, decrement and return
        if (dividerTicks > 0) {
            if (!paused) {
                dividerTicks--;
            }
            return;
        }
        if (!ticks.isEmpty()) {
            AncientMessageData data = messages.get(0);
            // If this is the first tick of the message, play its sound if it isn't null
            if (!data.acknowledged) {
                if (data.soundEvent != null) {
                    client.world.playSound(client.player, client.player.getBlockPos(), data.soundEvent, SoundCategory.MASTER, 1.0f, 1.0f);
                }
                data.acknowledged = true;
            }
            InGameHud hud = client.inGameHud;
            // Retrieve state for rendering
            float[] shake = getShake(1.0f, client.world.random);
            // Calculate absolute y based on the amount of newlines and the HUD state
            int newlines = data.lines.size() - 1;
            float entireHeight = ((newlines + 1) * client.textRenderer.fontHeight) + (newlines * 6);
            double y = (hud.scaledHeight / 2.0) + (paused ? 0.0f : shake[1]) - entireHeight;
            // Render text
            matrixStack.push();
            matrixStack.translate((hud.scaledWidth / 2.0) + (paused ? 0.0f : shake[0]), y, 0);
            matrixStack.scale(2.5f, 2.5f, 2.5f);
            for (int i = 0; i < data.lines.size(); i++) {
                int textWidth = client.textRenderer.getWidth(data.lines.get(i));
                client.textRenderer.drawWithShadow(matrixStack, data.lines.get(i), -textWidth / 2.0f, (i * (7 + client.textRenderer.fontHeight / 2.0f)), 0xFFFFFFFF);
            }
            matrixStack.pop();
            // If not paused, tick down
            if (!paused) {
                ticks.set(0, ticks.get(0) - 1);
                // If final tick, remove the first tick count and message data
                if (ticks.get(0) == 0) {
                    ticks.remove(0);
                    messages.remove(0);
                    // Set divider ticks if there are still messages left in the queue
                    if (!ticks.isEmpty()) {
                        dividerTicks = 40;
                    }
                }
            }
        }
    }

    public static class Listener implements ClientPlayNetworking.PlayChannelHandler {

        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            if (ticks.isEmpty()) {
                int size = buf.readInt();
                for (int i = 0; i < size; i++) {
                    messages.add(AncientMessageData.read(buf));
                }
                started = true;
            }
        }
    }
}
