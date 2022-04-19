package com.acikek.purpeille.block.ancient;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AncientMachineBlockItem extends BlockItem {

    public AncientMachineBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    public String getTooltipKey() {
        return Util.createTranslationKey("tooltip", Registry.BLOCK.getId(getBlock()));
    }

    public String getTooltipKey(int i) {
        return getTooltipKey() + "_" + i;
    }

    public List<MutableText> getTooltipText() {
        List<MutableText> text = new ArrayList<>();
        int i = 0;
        String key = getTooltipKey(i);
        while (I18n.hasTranslation(key)) {
            text.add(new TranslatableText(key));
            key = getTooltipKey(++i);
        }
        return text;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (Screen.hasShiftDown()) {
            for (MutableText text : getTooltipText()) {
                tooltip.add(text.formatted(Formatting.GRAY));
            }
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
