package com.acikek.purpeille.item.core;

import com.acikek.purpeille.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.MathHelper;

public class EncasedCore extends Item {

    public enum Type {

        NORMAL(1024, 1, Rarity.UNCOMMON),
        ENERGETIC(2048, 2, Rarity.RARE),
        OVERCLOCKED(4096, 4, Rarity.RARE),
        VACUOUS(512, -1, Rarity.RARE),
        CREATIVE(0, 4, Rarity.EPIC);

        public int durability;
        public int modifier;
        public Rarity rarity;

        Type(int durability, int modifier, Rarity rarity) {
            this.durability = durability;
            this.modifier = modifier;
            this.rarity = rarity;
        }

        public Settings getSettings(Settings settings) {
            Settings newSettings = this == Type.CREATIVE ? settings : settings.maxDamage(durability);
            return newSettings.rarity(rarity);
        }

        public EncasedCore getCore() {
            return new EncasedCore(ModItems.defaultSettings(), this);
        }
    }

    public Type type;

    public EncasedCore(Settings settings, Type type) {
        super(type.getSettings(settings));
        this.type = type;
    }

    public static Formatting[] DURABILITY_LEVELS = {
            Formatting.RED,
            Formatting.YELLOW,
            Formatting.GREEN
    };

    public Formatting getDurabilityFormatting(int durability) {
        int index = (int) (((double) durability / type.durability) * 3.0);
        return DURABILITY_LEVELS[MathHelper.clamp(index, 0, 2)];
    }

    public MutableText getDurabilityText(ItemStack stack) {
        int durability = type.durability - stack.getDamage();
        return new LiteralText("(" + durability + "/" + type.durability + ")")
                .formatted(getDurabilityFormatting(durability));
    }

    public static int getModifier(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof EncasedCore encasedCore) {
            if (item instanceof CreativeCore) {
                return CreativeCore.getNbtModifier(stack.getOrCreateNbt());
            }
            return encasedCore.type.modifier;
        }
        return 1;
    }
}
