package com.acikek.purpeille.compat.rei;

import com.acikek.purpeille.block.ModBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;

public class AncientOvenCategory implements DisplayCategory<AncientOvenDisplay> {

    public static final TranslatableText TITLE = new TranslatableText("rei.purpeille.ancient_oven");
    public static final EntryStack<ItemStack> ICON = EntryStacks.of(ModBlocks.ANCIENT_OVEN);

    @Override
    public Renderer getIcon() {
        return ICON;
    }

    @Override
    public Text getTitle() {
        return TITLE;
    }

    @Override
    public CategoryIdentifier<? extends AncientOvenDisplay> getCategoryIdentifier() {
        return AncientOvenDisplay.IDENTIFIER;
    }

    @Override
    public List<Widget> setupDisplay(AncientOvenDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 64, bounds.getCenterY() - 16);
        Point outputPoint = new Point(startPoint.x + 84, startPoint.y + 8);
        Point textPoint = new Point(bounds.getCenterX(), bounds.getCenterY() + 16);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 50, startPoint.y + 7)).animationDurationTicks(display.cookTime));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 27, startPoint.y + 8)).entry(display.getInputEntries().get(0).get(0)).markInput());
        widgets.add(Widgets.createResultSlotBackground(outputPoint));
        widgets.add(Widgets.createSlot(outputPoint).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        widgets.add(Widgets.createLabel(textPoint, display.getDamageText()));
        return widgets;
    }
}
