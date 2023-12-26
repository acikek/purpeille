package com.acikek.purpeille.mixin;

import com.acikek.purpeille.advancement.ModCriteria;
import com.acikek.purpeille.api.abyssal.AbyssalToken;
import com.acikek.purpeille.api.abyssal.AbyssalTokens;
import com.acikek.purpeille.warpath.Warpath;
import com.acikek.purpeille.warpath.WarpathData;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingScreenHandler.class)
public class SmithingScreenHandlerMixin {

    private boolean purpeille$isOutputUpgraded;
    private int purpeille$positive;
    private int purpeille$negative;

    @Inject(method = "canTakeOutput", cancellable = true, at = @At("HEAD"))
    private void purpeille$setCanTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        if (((ForgingScreenHandler) (Object) this).input.getStack(1).getItem() instanceof AbyssalToken token && token.isAbyssalToken()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isValidIngredient", cancellable = true, at = @At("HEAD"))
    private void purpeille$setAbyssalTokensUsable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof AbyssalToken token && token.isAbyssalToken()) {
            Inventory input = ((ForgingScreenHandler) (Object) this).input;
            WarpathData data = Warpath.getData(input.getStack(0));
            // The base item must have an existing warpath but no token must be applied to it
            if (data == null || data.appliedToken != null) {
                return;
            }
            Revelation revelation = data.getRevelation();
            // If the base item's revelation matches the token's revelation, the recipe works
            cir.setReturnValue(token.getRevelation().id.equals(revelation.id));
        }
    }

    @Inject(method = "updateResult", cancellable = true, at = @At("HEAD"))
    private void purpeille$applyAbyssalToken(CallbackInfo ci) {
        ForgingScreenHandler screen = ((ForgingScreenHandler) (Object) this);
        if (screen.input.getStack(0).isEmpty()) {
            return;
        }
        if (screen.input.getStack(1).getItem() instanceof AbyssalToken token && token.isAbyssalToken()) {
            ItemStack baseStack = screen.input.getStack(0);
            ItemStack outputStack = baseStack.copy();
            int[] values = AbyssalTokens.apply(outputStack, screen.input.getStack(1));
            System.out.println("applied");
            screen.output.setStack(0, outputStack);
            screen.output.setLastRecipe(null);
            purpeille$isOutputUpgraded = true;
            purpeille$positive = values[0];
            purpeille$negative = values[1];
            ci.cancel();
        }
    }

    @Inject(method = "onTakeOutput", at = @At("TAIL"))
    private void purpeille$output(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (purpeille$isOutputUpgraded) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ModCriteria.triggerWarpathUpgraded(serverPlayer, purpeille$positive, purpeille$negative);
            }
            purpeille$isOutputUpgraded = false;
        }
    }
}
