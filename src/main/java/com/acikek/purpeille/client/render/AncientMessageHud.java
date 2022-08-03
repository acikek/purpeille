package com.acikek.purpeille.client.render;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.StringUtils;

public class AncientMessageHud implements HudRenderCallback {

    public static Text message;
    public static boolean started;
    public static int ticks;

    public float[] getShake(float length, Random random) {
        float angle = random.nextFloat() * MathHelper.TAU;
        return new float[] { MathHelper.cos(angle) * length, MathHelper.sin(angle) * length };
    }

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (started) {
            ticks = 80 + message.getString().length() * 4;
            started = false;
        }
        if (ticks > 0) {
            boolean paused = client.isPaused();
            InGameHud hud = client.inGameHud;
            matrixStack.push();
            float[] shake = getShake(1.0f, client.world.random);
            String content = message.getString();
            int newlines = StringUtils.countMatches(content, "\n");
            float entireHeight = ((newlines + 1) * client.textRenderer.fontHeight) + (newlines * 6);
            double y = (hud.scaledHeight / 2.0) + (paused ? 0.0f : shake[1]) - entireHeight;
            matrixStack.translate((hud.scaledWidth / 2.0) + (paused ? 0.0f : shake[0]), y, 0);
            matrixStack.scale(2.5f, 2.5f, 2.5f);
            String[] lines = content.split("\n");
            for (int i = 0; i < lines.length; i++) {
                int textWidth = client.textRenderer.getWidth(lines[i]);
                client.textRenderer.drawWithShadow(matrixStack, lines[i], -textWidth / 2.0f, (i * (7 + client.textRenderer.fontHeight / 2.0f)), 0xFFFFFFFF);
            }
            matrixStack.pop();
            if (!paused) {
                ticks--;
            }
        }
        else if (message != null) {
            message = null;
        }
    }

    public static class Listener implements ClientPlayNetworking.PlayChannelHandler {

        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            if (ticks == 0) {
                message = buf.readText();
                started = true;
            }
        }
    }
}
