package com.acikek.purpeille.mixin;

import com.acikek.purpeille.api.AbyssalToken;
import com.acikek.purpeille.api.ImbuementData;
import com.acikek.purpeille.warpath.component.Revelation;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public class AbyssalTokenItemMixin implements AbyssalToken {

    private Revelation purpeille$revelation;

    @Override
    public Revelation getRevelation() {
        return purpeille$revelation;
    }

    @Override
    public void setRevelation(Revelation revelation) {
        purpeille$revelation = revelation;
    }

    public Formatting getEnergyFormatting(int energy) {
        if (energy < 20 || energy > 86) {
            return Formatting.RED;
        }
        if (energy < 40 || energy > 80) {
            return Formatting.YELLOW;
        }
        if (energy < 58 || energy > 70) {
            return Formatting.GREEN;
        }
        return Formatting.AQUA;
    }

    @Inject(method = "appendTooltip", at = @At(value = "TAIL"))
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (purpeille$revelation == null) {
            return;
        }
        if (stack.hasNbt()) {
            int energy = ImbuementData.readEnergy(stack.getOrCreateNbt().getCompound(ImbuementData.KEY));
            Text energyLevel = Text.literal(String.valueOf(energy)).formatted(getEnergyFormatting(energy))
                    .append(Text.literal("/").formatted(Formatting.DARK_GRAY))
                    .append(Text.literal(String.valueOf(MAX_ENERGY)).formatted(Formatting.GRAY));
            Text text = Text.translatable("tooltip.purpeille.abyssal_token")
                    .formatted(Formatting.GRAY)
                    .append(energyLevel);
            tooltip.add(text);
        }
    }
}
