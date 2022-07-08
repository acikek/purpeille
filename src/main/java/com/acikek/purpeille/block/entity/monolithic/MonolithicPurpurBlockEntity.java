package com.acikek.purpeille.block.entity.monolithic;

import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.entity.ModBlockEntities;
import com.acikek.purpeille.block.entity.SingleSlotBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MonolithicPurpurBlockEntity extends SingleSlotBlockEntity {

    public static BlockEntityType<MonolithicPurpurBlockEntity> BLOCK_ENTITY_TYPE;

    public int easeMode = -1;
    public int easing = 0;
    public int removalTicks = 0;

    @Override
    public void onAddItem(ItemStack stack, boolean unset, PlayerEntity player) {
        super.onAddItem(stack, unset, player);
        easeMode = 0;
    }

    @Override
    public void onRemoveItem(PlayerEntity player, boolean checkCreative, boolean copy, boolean remove) {
        super.onRemoveItem(player, checkCreative, true, false);
        easeMode = 1;
        removalTicks = 10;
    }

    public MonolithicPurpurBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

    public boolean canRemove() {
        return easeMode != 1 || removalTicks == 0;
    }
    
    public void ease() {
        if (easeMode > -1) {
            if (easing < 30 && easeMode == 0) {
                easing++;
            }
            else if (easing > 0 && easeMode == 1) {
                easing--;
            }
            else if (easing == 0 || easing == 30) {
                easeMode = -1;
            }
        }
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, MonolithicPurpurBlockEntity blockEntity) {
        if (blockEntity.removalTicks > 0) {
            blockEntity.removalTicks--;
            if (blockEntity.removalTicks == 0) {
                blockEntity.removeItem();
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        easeMode = nbt.getInt("EaseMode");
        removalTicks = nbt.getInt("RemovalTicks");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("EaseMode", easeMode);
        nbt.putInt("RemovalTicks", removalTicks);
        super.writeNbt(nbt);
    }

    public static void register() {
        BLOCK_ENTITY_TYPE = ModBlockEntities.build("monolithic_purpur_block_entity", MonolithicPurpurBlockEntity::new, ModBlocks.MONOLITHIC_PURPUR);
    }
}
