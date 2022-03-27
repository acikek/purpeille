package com.acikek.purpeille.mixin;

import com.acikek.purpeille.warpath.Component;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "appendTooltip", at = @At(value = "TAIL"))
    private void appendWarpath(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        Component revelation = Component.Type.REVELATION.getFromNbt(stack);
        if (revelation != null) {
            Component aspect = Component.Type.ASPECT.getFromNbt(stack);
            tooltip.add(Component.getWarpath(revelation, aspect));
            if (aspect != null && revelation.getSynergized(aspect)) {
                tooltip.add(revelation.getRite());
            }
        }
    }
}
