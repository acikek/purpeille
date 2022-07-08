package com.acikek.purpeille.client.render;

import com.acikek.purpeille.block.entity.SingleSlotBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public interface SingleSlotRenderer<T extends SingleSlotBlockEntity> extends BlockEntityRenderer<T> {

    @Override
    default void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        ItemStack stack = entity.getItem();
        if (!stack.isEmpty()) {
            int lightAbove = entity.getWorld() != null ? WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up()) : light;
            if (beforeCompletion(entity, tickDelta, stack, lightAbove, matrices, vertexConsumers, light, overlay)) {
                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers, 0);
            }
        }
        matrices.pop();
    }

    boolean beforeCompletion(T entity, float tickDelta, ItemStack stack, int lightAbove, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);
}
