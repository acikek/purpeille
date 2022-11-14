package com.acikek.purpeille.mixin.attribute;

import com.acikek.purpeille.api.AbyssallyAllegiantEntity;
import com.acikek.purpeille.attribute.ModAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class AbyssalAllegianceLivingEntityMixin implements AbyssallyAllegiantEntity {

    private int purpeille$cyclicAllegiance;
    private long purpeille$lastAllegiantTime;
    private int purpeille$fulfilledAllegiance;

    @Override
    public int getCyclicAllegiance() {
        return purpeille$cyclicAllegiance;
    }

    @Override
    public long getLastAllegiantTime() {
        return purpeille$lastAllegiantTime;
    }

    @Override
    public void setLastAllegiantTime(long time) {
        purpeille$lastAllegiantTime = time;
    }

    @Override
    public int getFulfilledAllegiance() {
        return purpeille$fulfilledAllegiance;
    }

    @Override
    public void setFulfilledAllegiance(int allegiance) {
        purpeille$fulfilledAllegiance = allegiance;
    }

    @Shadow @Nullable public abstract EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    @Inject(method = "getEquipmentChanges", at = @At("TAIL"))
    private void purpeille$updateCyclicAllegiance(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        EntityAttributeInstance instance = getAttributeInstance(ModAttributes.GENERIC_ABYSSAL_ALLEGIANCE);
        if (instance == null || instance.getValue() == 0.0) {
            return;
        }
        if (instance.getValue() > purpeille$cyclicAllegiance) {
            World world = ((Entity) (Object) this).world;
            if (purpeille$cyclicAllegiance == 0 && world != null) {
                purpeille$lastAllegiantTime = world.getTime();
            }
            purpeille$cyclicAllegiance = (int) instance.getValue();
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void purpeille$toNbt(NbtCompound nbt, CallbackInfo ci) {
        if (purpeille$cyclicAllegiance != 0) {
            nbt.putInt("CyclicAllegiance", purpeille$cyclicAllegiance);
        }
        if (purpeille$lastAllegiantTime != 0L) {
            nbt.putLong("LastAllegiantTime", purpeille$lastAllegiantTime);
        }
        if (purpeille$fulfilledAllegiance != 0) {
            nbt.putInt("FulfilledAllegiance", purpeille$fulfilledAllegiance);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void purpeille$readNbt(NbtCompound nbt, CallbackInfo ci) {
        purpeille$cyclicAllegiance = nbt.getInt("CyclicAllegiance");
        purpeille$lastAllegiantTime = nbt.getLong("LastAllegiantTime");
        purpeille$fulfilledAllegiance = nbt.getInt("FulfilledAllegiance");
    }
}
