package com.acikek.purpeille.block.entity.monolithic;

import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.entity.ModBlockEntities;
import com.acikek.purpeille.block.entity.SingleSlotBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MonolithicPurpurBlockEntity extends SingleSlotBlockEntity {

    public static BlockEntityType<MonolithicPurpurBlockEntity> BLOCK_ENTITY_TYPE;

    public int easeMode = -1;
    public int easing = 0;
    public int transitionTicks = 10;
    public int removalTicks = 0;
    public int property = -1;
    public int propertyMode = 0;

    @Override
    public void onAddItem(ItemStack stack, boolean unset, PlayerEntity player) {
        super.onAddItem(stack, unset, player);
        easeMode = 0;
        transitionTicks = 0;
        removalTicks = 0;
    }

    @Override
    public void onRemoveItem(PlayerEntity player, boolean checkCreative, boolean copy, boolean remove) {
        super.onRemoveItem(player, checkCreative, true, false);
        easeMode = 1;
        transitionTicks = 10;
        removalTicks = 10;
    }

    public MonolithicPurpurBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

    public boolean canRemove() {
        return easeMode == -1 || (removalTicks == 0 && transitionTicks == 10);
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

    public void cycleProperty() {
        propertyMode++;
        if (propertyMode == MonolithicPurpur.SUPPORTED_PROPERTIES[property].getValues().size()) {
            propertyMode = 0;
        }
        markDirty();
    }

    public void resetProperty() {
        property = -1;
        propertyMode = 0;
        markDirty();
    }

    public <V extends Enum<V> & StringIdentifiable> V getPropertyValue(EnumProperty<V> property) {
        return property.getValues().stream().toList().get(propertyMode);
    }

    public BlockState getModifiedState(BlockState state) {
        if (!state.contains(MonolithicPurpur.SUPPORTED_PROPERTIES[property])) {
            resetProperty();
            return state;
        }
        return switch (property) {
            case 0 -> state.with(Properties.AXIS, getPropertyValue(Properties.AXIS));
            case 1 -> state.with(Properties.HORIZONTAL_AXIS, getPropertyValue(Properties.HORIZONTAL_AXIS));
            case 2 -> state.with(Properties.FACING, getPropertyValue(Properties.FACING));
            case 3 -> state.with(Properties.HORIZONTAL_FACING, getPropertyValue(Properties.HORIZONTAL_FACING));
            default -> state;
        };
    }

    public static void checkTransition(World world, BlockPos pos, BlockState state, int ticks) {
        if (!world.isClient() && ticks % 2 == 0) {
            world.setBlockState(pos, state.with(MonolithicPurpur.TRANSITION, ticks / 2));
        }
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, MonolithicPurpurBlockEntity blockEntity) {
        if (blockEntity.transitionTicks < 10) {
            blockEntity.transitionTicks++;
            checkTransition(world, blockPos, state, blockEntity.transitionTicks);
        }
        if (blockEntity.removalTicks > 0) {
            blockEntity.removalTicks--;
            checkTransition(world, blockPos, state, blockEntity.removalTicks);
            if (blockEntity.removalTicks == 0) {
                blockEntity.removeItem();
                blockEntity.resetProperty();
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        easeMode = nbt.getInt("EaseMode");
        transitionTicks = nbt.getInt("TransitionTicks");
        removalTicks = nbt.getInt("RemovalTicks");
        property = nbt.getInt("Property");
        propertyMode = nbt.getInt("PropertyMode");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("EaseMode", easeMode);
        nbt.putInt("TransitionTicks", transitionTicks);
        nbt.putInt("RemovalTicks", removalTicks);
        nbt.putInt("Property", property);
        nbt.putInt("PropertyMode", propertyMode);
        super.writeNbt(nbt);
    }

    public static void register() {
        BLOCK_ENTITY_TYPE = ModBlockEntities.build("monolithic_purpur_block_entity", MonolithicPurpurBlockEntity::new, ModBlocks.MONOLITHIC_PURPUR);
    }
}
