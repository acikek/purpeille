package com.acikek.purpeille.block.ancient.oven;

import com.acikek.purpeille.block.ancient.AncientMachineBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;

public class AncientOvenBlockItem extends AncientMachineBlockItem {

    public AncientOvenBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public String getTooltipKey() {
        return "tooltip.purpeille.ancient_oven";
    }

    @Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        boolean result = super.place(context, state);
        if (!context.getWorld().isClient()
                && context.getWorld().getBlockEntity(context.getBlockPos()) instanceof AncientOvenBlockEntity blockEntity
                && context.getStack().hasNbt()) {
            int durability = context.getStack().getOrCreateNbt().getInt("AncientOvenDurability");
            if (durability != 0) {
                blockEntity.durability = durability;
            }
        }
        return result;
    }
}
