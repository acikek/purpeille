package com.acikek.purpeille.compat.emi;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.recipe.oven.AncientOvenRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmiAncientOvenRecipe implements EmiRecipe {

    public static final EmiTexture EMPTY_SMALL_ARROW = new EmiTexture(PurpeillePlugin.WIDGETS, 0, 0, 24, 9);
    public static final EmiTexture SMALL_ARROW = new EmiTexture(PurpeillePlugin.WIDGETS, 0, 9, 24, 10);

    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(
            Purpeille.id("ancient_oven"),
            EmiStack.of(ModBlocks.ANCIENT_OVEN),
            (ctx, x, y, delta) -> {
                ctx.drawTexture(PurpeillePlugin.WIDGETS, x, y, 240, 240, 16, 16, 256, 256);
            });

    public Identifier id;
    public EmiIngredient input;
    public List<EmiStack> outputs;
    public int cookTime;
    public int damage;
    boolean isRandom;

    public EmiAncientOvenRecipe(AncientOvenRecipe recipe) {
        id = recipe.getId();
        input = EmiIngredient.of(recipe.input());
        outputs = Arrays.stream(recipe.result()).map(EmiStack::of).collect(Collectors.toList());
        cookTime = recipe.cookTime();
        damage = recipe.damage();
        isRandom = outputs.size() != 1;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public boolean supportsRecipeTree() {
        return !isRandom;
    }

    @Override
    public int getDisplayWidth() {
        return 82;
    }

    @Override
    public int getDisplayHeight() {
        return isRandom ? (20 * outputs.size() + 12) : 38;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int inputY = (outputs.size() - 1) * 10 + (isRandom ? 0 : 4);
        List<TooltipComponent> arrowTooltip = List.of(TooltipComponent.of(Text.translatable("emi.cooking.time", cookTime / 20f).asOrderedText()));
        widgets.addSlot(input, 0, inputY);
        if (!isRandom) {
            widgets.addFillingArrow(24, inputY + 1, 50 * cookTime).tooltip((x, y) -> arrowTooltip);
            widgets.addSlot(outputs.get(0), 56, 0).large(true).recipeContext(this);
        }
        else {
            for (int i = 0; i < outputs.size(); i++) {
                int y = (i * 20) + 5;
                widgets.addTexture(EMPTY_SMALL_ARROW, 29, y);
                widgets.addAnimatedTexture(SMALL_ARROW, 29, y - 1, 50 * cookTime, true, false, false)
                        .tooltip((mouseX, mouseY) -> arrowTooltip);
                widgets.addSlot(outputs.get(i), 64, i * 20).recipeContext(this).appendTooltip(() -> PurpeillePlugin.getChanceTooltip(100.0 / outputs.size()));
            }
        }
        widgets.addText(Text.translatable("emi.purpeille.ancient_oven.damage", damage)
                        .formatted(damage > 0 ? Formatting.RED : Formatting.GREEN)
                        .asOrderedText(),
                1, widgets.getHeight() - 10, -1, true);
    }
}
