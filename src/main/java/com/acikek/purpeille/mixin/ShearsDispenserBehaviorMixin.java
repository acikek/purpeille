package com.acikek.purpeille.mixin;

import com.acikek.purpeille.block.ChorusInfestedBlocks;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsDispenserBehavior.class)
public class ShearsDispenserBehaviorMixin {

    @Inject(method = "tryShearBlock", cancellable = true, at = @At("HEAD"))
    private static void shearInfestedBlock(ServerWorld world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ChorusInfestedBlocks.shearInfestedBlock(world, pos, world.getBlockState(pos), null, null)) {
            cir.setReturnValue(true);
        }
    }
}
