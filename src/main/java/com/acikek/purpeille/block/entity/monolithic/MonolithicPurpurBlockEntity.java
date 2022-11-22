package com.acikek.purpeille.block.entity.monolithic;

import com.acikek.purpeille.api.abyssal.AbyssalToken;
import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.block.entity.ModBlockEntities;
import com.acikek.purpeille.block.entity.SingleSlotBlockEntity;
import com.acikek.purpeille.sound.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MonolithicPurpurBlockEntity extends SingleSlotBlockEntity {

    public static BlockEntityType<MonolithicPurpurBlockEntity> BLOCK_ENTITY_TYPE;

    public enum AnimationMode {
        NONE,
        ADDING,
        REMOVING,
        IMBUING,
        FALLING
    }

    public AnimationMode animationMode;
    public int scaleEasing = -1;
    public int heightEasing = 0;
    public int transitionTicks = 10;
    public int removalTicks = 0;
    public int imbuingTicks = 0;
    public boolean hasToken;
    public int property = -1;
    public int propertyMode = 0;

    @Override
    public void onAddItem(ItemStack stack, boolean unset, PlayerEntity player) {
        super.onAddItem(stack, unset, player);
        animationMode = AnimationMode.ADDING;
        transitionTicks = 0;
        removalTicks = 0;
        if (stack.getItem() instanceof AbyssalToken token && token.isAbyssalToken()) {
            hasToken = true;
        }
    }

    public void queueRemoval() {
        animationMode = AnimationMode.REMOVING;
        transitionTicks = 10;
        removalTicks = 10;
        scaleEasing = 30;
    }

    public void queueImbuement() {
        animationMode = AnimationMode.IMBUING;
        imbuingTicks = 30;
    }

    @Override
    public void onRemoveItem(PlayerEntity player, boolean checkCreative, boolean copy, boolean remove) {
        super.onRemoveItem(player, checkCreative, true, false);
        queueRemoval();
        hasToken = false;
    }

    public MonolithicPurpurBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

    public boolean canRemove() {
        return animationMode == AnimationMode.NONE || (removalTicks == 0 && transitionTicks == 10);
    }
    
    public void ease() {
        if (animationMode == AnimationMode.ADDING || animationMode == AnimationMode.REMOVING) {
            if (scaleEasing < 30 && animationMode == AnimationMode.ADDING) {
                if (scaleEasing == -1) {
                    scaleEasing = 0;
                }
                scaleEasing++;
            }
            else if (scaleEasing > 0 && animationMode == AnimationMode.REMOVING) {
                scaleEasing--;
            }
            else if (scaleEasing == 0 || scaleEasing == 30) {
                animationMode = AnimationMode.NONE;
            }
        }
        else if (animationMode == AnimationMode.IMBUING || animationMode == AnimationMode.FALLING) {
            if (heightEasing < 90 && animationMode == AnimationMode.IMBUING) {
                heightEasing++;
            }
            else if (heightEasing > 0 && animationMode == AnimationMode.FALLING) {
                heightEasing--;
                if (heightEasing == 0) {
                    animationMode = AnimationMode.NONE;
                }
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

    public void finishImbuing(World world) {
        getItem().getOrCreateNbt().putInt("CustomModelData", 1);
        playSound(ModSoundEvents.IMBUE_COLLAPSE, 0.4f * world.random.nextFloat() + 0.8f);
        animationMode = AnimationMode.FALLING;
        heightEasing = 120;
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, MonolithicPurpurBlockEntity blockEntity) {
        if (blockEntity.transitionTicks < 10) {
            blockEntity.transitionTicks++;
            checkTransition(world, blockPos, state, blockEntity.transitionTicks);
            if (blockEntity.transitionTicks == 10) {
                blockEntity.animationMode = AnimationMode.NONE;
            }
        }
        if (blockEntity.removalTicks > 0) {
            blockEntity.removalTicks--;
            checkTransition(world, blockPos, state, blockEntity.removalTicks);
            if (blockEntity.removalTicks == 0) {
                blockEntity.removeItem();
                blockEntity.resetProperty();
                blockEntity.heightEasing = 0;
                blockEntity.animationMode = AnimationMode.NONE;
            }
        }
        if (blockEntity.imbuingTicks > 0) {
            blockEntity.imbuingTicks--;
            if (blockEntity.imbuingTicks == 0) {
                if (blockEntity.hasToken) {
                    blockEntity.finishImbuing(world);
                }
                else {
                    blockEntity.queueRemoval();
                }
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        String animationString = nbt.getString("AnimationMode");
        animationMode = !animationString.equals("") ? AnimationMode.valueOf(animationString) : null;
        transitionTicks = nbt.getInt("TransitionTicks");
        removalTicks = nbt.getInt("RemovalTicks");
        hasToken = nbt.getBoolean("HasToken");
        property = nbt.getInt("Property");
        propertyMode = nbt.getInt("PropertyMode");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if (animationMode != null) {
            nbt.putString("AnimationMode", animationMode.name());
        }
        nbt.putInt("TransitionTicks", transitionTicks);
        nbt.putInt("RemovalTicks", removalTicks);
        nbt.putBoolean("HasToken", hasToken);
        nbt.putInt("Property", property);
        nbt.putInt("PropertyMode", propertyMode);
        super.writeNbt(nbt);
    }

    public static void register() {
        BLOCK_ENTITY_TYPE = ModBlockEntities.build("monolithic_purpur_block_entity", MonolithicPurpurBlockEntity::new, ModBlocks.MONOLITHIC_PURPUR);
    }
}
