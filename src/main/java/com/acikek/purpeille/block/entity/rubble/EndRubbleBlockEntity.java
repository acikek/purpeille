package com.acikek.purpeille.block.entity.rubble;

import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.entity.ModBlockEntities;
import com.acikek.purpeille.sound.ModSoundEvents;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class EndRubbleBlockEntity extends LootableContainerBlockEntity {

    public static BlockEntityType<EndRubbleBlockEntity> BLOCK_ENTITY_TYPE;
    public static final MutableText NAME = Text.translatable("block.purpeille.end_rubble");

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(18, ItemStack.EMPTY);

    public ViewerCountManager stateManager = new ViewerCountManager() {

        @Override
        protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            playSound(world, pos, ModSoundEvents.RUBBLE_OPEN, state);
        }

        @Override
        protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            playSound(world, pos, ModSoundEvents.RUBBLE_CLOSE, state);
        }

        @Override
        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        }

        @Override
        protected boolean isPlayerViewing(PlayerEntity player) {
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler screenHandler) {
                return screenHandler.getInventory() == EndRubbleBlockEntity.this;
            }
            return false;
        }
    };

    protected EndRubbleBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BLOCK_ENTITY_TYPE, blockPos, blockState);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, inventory);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, inventory);
        }
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        inventory = list;
    }

    @Override
    protected Text getContainerName() {
        return NAME;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X2, syncId, playerInventory, this, 2);
    }

    @Override
    public int size() {
        return 18;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (!removed && !player.isSpectator()) {
            stateManager.openContainer(player, getWorld(), getPos(), getCachedState());
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!removed && !player.isSpectator()) {
            stateManager.closeContainer(player, getWorld(), getPos(), getCachedState());
        }
    }

    public void playSound(World world, BlockPos pos, SoundEvent event, BlockState state) {
        Vec3i vec3i = state.get(BarrelBlock.FACING).getVector();
        double x = (double) pos.getX() + 0.5 + (double) vec3i.getX() / 2.0;
        double y = (double) pos.getY() + 0.5 + (double) vec3i.getY() / 2.0;
        double z = (double) pos.getZ() + 0.5 + (double) vec3i.getZ() / 2.0;
        world.playSound(null, x, y, z, event, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.2f + 0.9f);
    }

    public static void tick(World world, BlockPos pos, BlockState state, EndRubbleBlockEntity blockEntity) {
        if (!blockEntity.removed) {
            blockEntity.stateManager.updateViewerCount(world, pos, state);
        }
    }

    public static void register() {
        BLOCK_ENTITY_TYPE = ModBlockEntities.build("end_rubble", EndRubbleBlockEntity::new, ModBlocks.END_RUBBLE);
    }
}
