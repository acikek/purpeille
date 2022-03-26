package com.acikek.purpeille.mixin;

import com.acikek.purpeille.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public World world;

    public Vec3d generate(BlockPos point, Random random) {
        double angle = random.nextDouble(Math.PI * 2);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double newX = (point.getX() * cos) + (point.getZ() * sin);
        double newZ = (point.getX() * -sin) + (point.getZ() * cos);
        return new Vec3d(newX, point.getY(), newZ);
    }

    @Inject(method = "tickInVoid", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "HEAD"))
    private void dropPreservedDust(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof ItemEntity itemEntity) {
            if (itemEntity.getStack().getItem() == ModItems.IMPURE_PRESERVED_DUST && world.getServer() != null) {
                ServerWorld end = world.getServer().getWorld(World.END);
                if (end != null) {
                    BlockPos point = new BlockPos(5, 0, 0);
                    for (int i = 0; i < itemEntity.getStack().getCount(); i++) {
                        Vec3d pos = generate(point, world.random);
                        int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) pos.getX(), (int) pos.getY());
                        ItemEntity drop = new ItemEntity(end, pos.getX(), y, pos.getZ(), new ItemStack(ModItems.PRESERVED_DUST));
                        end.spawnEntity(drop);
                    }
                }
            }
        }
    }
}
