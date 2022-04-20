package com.acikek.purpeille.item.core;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreativeCore extends EncasedCore {

    public static final String MODIFIER_KEY = "CoreModifier";
    public static final int MODIFIER_MAX = 4;

    public CreativeCore(Settings settings) {
        super(settings, Type.CREATIVE);
    }

    public static void addModifier(NbtCompound nbt) {
        if (!nbt.contains(MODIFIER_KEY)) {
            nbt.putInt(MODIFIER_KEY, MODIFIER_MAX);
        }
    }

    public static int getNbtModifier(NbtCompound nbt) {
        addModifier(nbt);
        return nbt.getInt(MODIFIER_KEY);
    }

    public static int getNextModifier(NbtCompound nbt) {
        int current = getNbtModifier(nbt);
        if (current >= MODIFIER_MAX) {
            return 1;
        }
        return current + 1;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);
        if (!world.isClient()) {
            NbtCompound nbt = handStack.getOrCreateNbt();
            int modifier = getNextModifier(nbt);
            nbt.putInt(MODIFIER_KEY, modifier);
            user.sendMessage(new TranslatableText("use.purpeille.creative_core", modifier), false);
        }
        return TypedActionResult.pass(handStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int modifier = getNbtModifier(stack.getOrCreateNbt());
        Formatting formatting = Rarity.values()[MathHelper.clamp(modifier, 1, 4) - 1].formatting;
        MutableText text = new TranslatableText("tooltip.purpeille.creative_core")
                .formatted(Formatting.GRAY)
                .append(new LiteralText(String.valueOf(modifier)).formatted(formatting));
        tooltip.add(text);
        super.appendTooltip(stack, world, tooltip, context);
    }
}
