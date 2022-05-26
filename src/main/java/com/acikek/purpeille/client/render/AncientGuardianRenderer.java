package com.acikek.purpeille.client.render;

import com.acikek.purpeille.block.ancient.AncientMachine;
import com.acikek.purpeille.block.ancient.guardian.AncientGuardian;
import com.acikek.purpeille.block.ancient.guardian.AncientGuardianBlockEntity;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class AncientGuardianRenderer implements BlockEntityRenderer<AncientGuardianBlockEntity> {

    public AncientGuardianRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    public void translate(MatrixStack matrices, boolean z, boolean offset) {
        double xVal = 0.5d, zVal = offset ? 0.55d : 0.45d;
        matrices.translate(z ? xVal : zVal, 0.5d, z ? zVal : xVal);
    }

    public static boolean isOffset(Direction direction) {
        return switch (direction) {
            case SOUTH, EAST -> true;
            default -> false;
        };
    }

    public float lastAngle = 0.0f;

    public float getAngle() {
        if (!MinecraftClient.getInstance().isPaused()) {
            lastAngle = MathHelper.sin((System.currentTimeMillis() % 6000L) / 6000.0f * MathHelper.TAU) * 145.0f;
        }
        return lastAngle;
    }

    @Override
    public void render(AncientGuardianBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        ItemStack stack = entity.getItem();
        if (!stack.isEmpty()) {
            int lightAbove = entity.getWorld() != null ? WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up()) : light;
            Direction direction = entity.getCachedState().get(AncientMachine.FACING);
            translate(matrices, AncientGuardian.isZ(direction), isOffset(direction));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(getAngle() + direction.asRotation()));
            matrices.scale(0.45f, 0.45f, 0.45f);
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers, 0);
        }
        matrices.pop();
    }

    public static void register() {
        BlockEntityRendererRegistry.register(AncientGuardianBlockEntity.BLOCK_ENTITY_TYPE, AncientGuardianRenderer::new);
    }
}
