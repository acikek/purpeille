package com.acikek.purpeille.block.entity;

import com.acikek.purpeille.item.core.EncasedCore;
import com.acikek.purpeille.util.ImplementedInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class SingleSlotBlockEntity extends BlockEntity implements ImplementedInventory, SidedInventory {

    public DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public SingleSlotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ItemStack getItem() {
        return getStack(0);
    }

    /**
     * Sets the singular item within the machine.
     * This modifies the inventory directly and should be used for player actions.
     * To handle transfer interactions, override {@link net.minecraft.inventory.Inventory#setStack(int, ItemStack)}.
     * @param stack The stack to add. Will not be modified.
     */
    public void setItem(ItemStack stack) {
        ItemStack newStack = stack.copy();
        if (newStack.getCount() > 1) {
            newStack.setCount(1);
        }
        items.set(0, newStack);
    }

    public void setItem(Item item) {
        setItem(item.getDefaultStack());
    }

    /**
     * Removes the singular item within the machine.
     * This modifies the inventory directly and should be used for player actions.
     * To handle transfer interactions, override {@link net.minecraft.inventory.Inventory#removeStack(int, int)}.
     */
    public void removeItem() {
        setItem(ItemStack.EMPTY);
    }

    /**
     * @return The core item, or null if there is none.
     */
    public EncasedCore getCore() {
        if (getItem().getItem() instanceof EncasedCore core) {
            return core;
        }
        return null;
    }

    /**
     * Damages the core item, if any, by a certain amount.
     * If the core's durability is then  {@code 0}, removes the item.
     * @param damage The amount of damage the core should take.
     * @param random A random source used for damage calculation.
     * @return Whether the core was removed.
     */
    public boolean damageCore(int damage, Random random) {
        EncasedCore core = getCore();
        if (core != null) {
            if (core.type == EncasedCore.Type.CREATIVE) {
                return false;
            }
            getItem().damage(damage, random, null);
            if (getItem().getDamage() >= core.type.durability) {
                removeItem();
                return true;
            }
        }
        return false;
    }

    /**
     * Sends a core name and durability indicator to the player if they have an empty hand.
     * <p>The intended behavior is for the player to be able to check the core inside the machine by shift-right-clicking,
     * in which case nothing else should follow.</p>
     * @return Whether the player checked the core.
     */
    public boolean playerCheckCore(PlayerEntity player, ItemStack handStack) {
        if (handStack.isEmpty()) {
            EncasedCore core = getCore();
            if (core != null) {
                MutableText text = Text.translatable(core.getTranslationKey()).formatted(core.type.rarity.formatting);
                if (core.type != EncasedCore.Type.CREATIVE) {
                    text.append(" ").append(core.getDurabilityText(getItem()));
                }
                player.sendMessage(text, true);
            }
            return true;
        }
        return false;
    }

    /**
     * Adds an item from an optional player source.
     * @param unset Whether the item is still unset. For example, this should be {@code false} when calling after hopper transfer.
     * @param player The player source. If non-null and {@link PlayerEntity#isCreative()} returns false, decrements {@code stack} by {@code 1}.
     */
    public void onAddItem(ItemStack stack, boolean unset, PlayerEntity player) {
        if (unset) {
            setItem(stack.copy());
        }
        if (player != null && !player.isCreative()) {
            stack.decrement(1);
        }
    }

    /**
     * Removes an item from an optional player source.
     * @param checkCreative Whether the player shouldn't receive the item if in creative mode.
     */
    public void onRemoveItem(PlayerEntity player, boolean checkCreative, boolean copy, boolean remove) {
        if (player != null && (!checkCreative || !player.isCreative())) {
            player.getInventory().offerOrDrop(copy ? getItem().copy() : getItem());
        }
        if (remove) {
            removeItem();
        }
    }

    /**
     * If {@link BlockEntity#world} is non-null, plays a sound at the block entity's location and with the specified pitch.
     */
    public void playSound(SoundEvent event, float pitch) {
        if (world != null) {
            world.playSound(null, pos, event, SoundCategory.BLOCKS, 1.0f, pitch);
        }
    }

    /**
     * Calls {@link SingleSlotBlockEntity#playSound(SoundEvent, float)} with a pitch of {@code 1.0f}.
     */
    public void playSound(SoundEvent event) {
        playSound(event, 1.0f);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(pos);
        }
        else if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.REDRAW_ON_MAIN_THREAD);
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[1];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
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

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
