package com.acikek.purpeille.block.ancient;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.item.core.EncasedCore;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class AncientMachineBlockEntity extends BlockEntity {

    public DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public AncientMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ItemStack getItem() {
        return items.get(0);
    }

    public boolean hasItem() {
        return !getItem().isEmpty();
    }

    public void setItem(ItemStack stack) {
        if (stack.getCount() > 1) {
            stack.setCount(1);
        }
        items.set(0, stack);
    }

    public void setItem(Item item) {
        setItem(new ItemStack(item));
    }

    public void removeItem() {
        items.set(0, ItemStack.EMPTY);
    }

    public EncasedCore getCore() {
        if (getItem().getItem() instanceof EncasedCore core) {
            return core;
        }
        return null;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
        super.writeNbt(nbt);
    }

    public static <T extends BlockEntity> BlockEntityType<T> build(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
        return Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                Purpeille.id(id),
                FabricBlockEntityTypeBuilder.create(factory, block)
                        .build(null)
        );
    }
}
