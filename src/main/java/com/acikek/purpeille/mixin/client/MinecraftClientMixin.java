package com.acikek.purpeille.mixin.client;

import com.acikek.purpeille.util.ItemEntityTargeter;
import com.acikek.purpeille.api.warpath.Components;
import com.acikek.purpeille.impl.AmalgamatedSpyglassImpl;
import com.acikek.purpeille.warpath.AbyssaliteData;
import com.acikek.purpeille.warpath.component.Revelation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    private final Map<Item, Long> purpeille$materialMap = new HashMap<>();

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Final public GameRenderer gameRenderer;

    private Text getText(Revelation revelation, ItemStack stack) {
        if (stack.isOf(revelation.abyssalite.token)) {
            return Text.translatable("message.purpeille.token_observed", revelation.defaultText);
        }
        Pair<Float, AbyssaliteData.Effect> pair = revelation.abyssalite.getModifier(stack);
        if (pair == null) {
            return null;
        }
        return Text.translatable("message.purpeille.effect_observed", pair.getRight().text, revelation.defaultText);
    }

    private void sendObserved(Revelation revelation, ItemStack stack) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeItemStack(stack);
        buf.writeBoolean(stack.isOf(revelation.abyssalite.token));
        ClientPlayNetworking.send(AmalgamatedSpyglassImpl.CHANNEL, buf);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void purpeille$handleAmalgamatedSpyglass(CallbackInfo ci) {
        if (gameRenderer instanceof ItemEntityTargeter targeter && targeter.getItemEntity() != null) {
            ItemStack stack = targeter.getItemEntity().getStack();
            long time = System.currentTimeMillis();
            if (purpeille$materialMap.containsKey(stack.getItem()) && time - purpeille$materialMap.get(stack.getItem()) < 15000L) {
                return;
            }
            for (Revelation revelation : Components.getRevelations().values()) {
                if (revelation.abyssalite == null) {
                    continue;
                }
                Text text = getText(revelation, stack);
                if (text != null) {
                    player.sendMessage(text, true);
                    purpeille$materialMap.put(stack.getItem(), time);
                    sendObserved(revelation, stack);
                    return;
                }
            }
        }
    }
}
