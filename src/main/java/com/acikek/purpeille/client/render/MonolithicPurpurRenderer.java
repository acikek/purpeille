package com.acikek.purpeille.client.render;

import com.acikek.purpeille.block.entity.monolithic.MonolithicPurpurBlockEntity;
import com.acikek.purpeille.client.PurpeilleClient;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class MonolithicPurpurRenderer implements SingleSlotRenderer<MonolithicPurpurBlockEntity> {

    public MonolithicPurpurRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public boolean beforeCompletion(MonolithicPurpurBlockEntity entity, float tickDelta, ItemStack stack, int lightAbove, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        entity.ease();
        matrices.translate(0.5,  1.3, 0.5);
        if (entity.getItem().getItem() instanceof BlockItem) {
            matrices.translate(0.0f, -0.4f, 0.0f);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(45));
            matrices.scale(1.5f, 1.5f, 1.5f);
        }
        else {
            matrices.translate(0.0f, 0.1f - MathHelper.cos((PurpeilleClient.rotationTicks % 60 + tickDelta) / 60.0f * MathHelper.TAU) * 0.1f, 0.0f);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-MinecraftClient.getInstance().cameraEntity.getYaw()));
        }
        if (entity.easing != 30) {
            float scale = 1.0f - (float) Math.pow(1.0 - (entity.easing + tickDelta) / 30.0, 5);
            matrices.scale(scale, scale, scale);
        }
        return entity.easing > 0;
    }

    public static void register() {
        BlockEntityRendererRegistry.register(MonolithicPurpurBlockEntity.BLOCK_ENTITY_TYPE, MonolithicPurpurRenderer::new);
    }
}
