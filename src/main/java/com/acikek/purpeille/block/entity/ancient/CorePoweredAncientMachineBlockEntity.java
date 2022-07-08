package com.acikek.purpeille.block.entity.ancient;

import com.acikek.purpeille.block.entity.SingleSlotBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CorePoweredAncientMachineBlockEntity extends SingleSlotBlockEntity {

    public CorePoweredAncientMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addCore(World world, ItemStack stack, boolean unset, PlayerEntity player, BlockPos pos, BlockState state) {
        onAddItem(stack, unset, player);
    }

    public void removeCore(World world, PlayerEntity player, boolean remove, BlockPos pos, BlockState state) {
        onRemoveItem(player, true, remove);
    }
}
