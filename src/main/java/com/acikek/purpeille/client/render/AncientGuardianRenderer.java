package com.acikek.purpeille.client.render;

import com.acikek.purpeille.block.entity.CommonBlockWithEntity;
import com.acikek.purpeille.block.entity.ancient.guardian.AncientGuardian;
import com.acikek.purpeille.block.entity.ancient.guardian.AncientGuardianBlockEntity;
import com.acikek.purpeille.client.PurpeilleClient;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class AncientGuardianRenderer implements SingleSlotRenderer<AncientGuardianBlockEntity> {

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

    @Override
    public boolean beforeCompletion(AncientGuardianBlockEntity entity, float tickDelta, ItemStack stack, int lightAbove, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction direction = entity.getCachedState().get(CommonBlockWithEntity.FACING);
        translate(matrices, AncientGuardian.isZ(direction), isOffset(direction));
        float angle = MathHelper.sin((PurpeilleClient.rotationTicks % 120 + tickDelta) / 120.0f * MathHelper.TAU) * 145.0f;
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(angle + direction.asRotation()));
        matrices.scale(0.45f, 0.45f, 0.45f);
        return true;
    }

    public static void register() {
        BlockEntityRendererRegistry.register(AncientGuardianBlockEntity.BLOCK_ENTITY_TYPE, AncientGuardianRenderer::new);
    }
}
