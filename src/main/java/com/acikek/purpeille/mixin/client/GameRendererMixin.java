package com.acikek.purpeille.mixin.client;

import com.acikek.purpeille.api.abyssal.AmalgamatedSpyglass;
import com.acikek.purpeille.util.ItemEntityTargeter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class GameRendererMixin implements ItemEntityTargeter {

    @Shadow @Final private MinecraftClient client;
    private ItemEntity purpeille$targetedItemEntity;

    @Override
    public ItemEntity getItemEntity() {
        return purpeille$targetedItemEntity;
    }

    @Inject(method = "updateTargetedEntity", locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"))
    private void purpeille$captureTargetedItemEntity(float tickDelta, CallbackInfo ci, Entity entity, double d, Vec3d vec3d, boolean bl, int i, double e, Vec3d vec3d2, Vec3d vec3d3, float f, Box box) {
        if (AmalgamatedSpyglass.isUsingAmalgamatedSpyglass(client.player)) {
            EntityHitResult itemEntityResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, check -> check instanceof ItemEntity, e);
            if (itemEntityResult != null) {
                double dist = itemEntityResult.getEntity().squaredDistanceTo(itemEntityResult.getPos());
                if (dist < e && itemEntityResult.getEntity() instanceof ItemEntity itemEntity) {
                    purpeille$targetedItemEntity = itemEntity;
                    return;
                }
            }
        }
        purpeille$targetedItemEntity = null;
    }
}
