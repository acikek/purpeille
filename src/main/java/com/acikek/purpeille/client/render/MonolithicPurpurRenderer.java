package com.acikek.purpeille.client.render;

import com.acikek.purpeille.block.entity.monolithic.MonolithicPurpurBlockEntity;
import com.acikek.purpeille.client.PurpeilleClient;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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
        Item item = entity.getItem().getItem();
        boolean shouldScale = entity.scaleEasing != -1 && entity.scaleEasing != 30;
        float scale = 0.0f;
        if (shouldScale) {
            scale = 1.0f - (float) Math.pow(1.0 - (entity.scaleEasing + tickDelta) / 30.0, 5.0);
        }
        if (entity.heightEasing > 0) {
            float amount = (entity.heightEasing + tickDelta) / 90.0f;
            float offset = amount < 0.5f
                    ? 16.0f * amount * amount * amount * amount * amount
                    : 1.0f - (float) Math.pow(-2.0 * amount + 2.0, 5.0) / 2.0f;
            if (entity.hasToken) {
                offset /= 2.0f;
            }
            if (item instanceof BlockItem) {
                offset *= 1.15f;
            }
            matrices.translate(0.0, offset, 0.0);
        }
        if (item instanceof BlockItem) {
            if (shouldScale) {
                matrices.translate(0.3, 0.0, 0.0);
            }
            matrices.translate(-0.285f - scale * 0.3, -0.3f, 0.0f);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(45));
            matrices.scale(0.4f, 0.4f, 0.4f);
        }
        else {
            matrices.translate(0.0f, 0.1f - MathHelper.cos((PurpeilleClient.rotationTicks % 60 + tickDelta) / 60.0f * MathHelper.TAU) * 0.1f, 0.0f);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f - MinecraftClient.getInstance().cameraEntity.getYaw()));
        }
        if (shouldScale) {
            matrices.scale(scale, scale, scale);
        }
        boolean canRender = entity.scaleEasing != 0;
        if (canRender && item instanceof BlockItem blockItem) {
            BlockState state = blockItem.getBlock().getDefaultState();
            if (entity.property != -1) {
                state = entity.getModifiedState(state);
            }
            else if (blockItem.getBlock() instanceof TallPlantBlock) {
                state = state.with(TallPlantBlock.HALF, DoubleBlockHalf.UPPER);
            }
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, matrices, vertexConsumers, lightAbove, overlay);
            return false;
        }
        return canRender;
    }

    public static void register() {
        BlockEntityRendererRegistry.register(MonolithicPurpurBlockEntity.BLOCK_ENTITY_TYPE, MonolithicPurpurRenderer::new);
    }
}
