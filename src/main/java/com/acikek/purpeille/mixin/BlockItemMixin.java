package com.acikek.purpeille.mixin;

import com.acikek.purpeille.block.entity.monolithic.MonolithicPurpur;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
    private void protectMonolithicPurpur(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos().down());
        if (state.getBlock() instanceof MonolithicPurpur && state.get(MonolithicPurpur.FULL)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
}
