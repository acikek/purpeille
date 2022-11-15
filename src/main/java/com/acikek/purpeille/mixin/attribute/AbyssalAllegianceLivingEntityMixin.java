package com.acikek.purpeille.mixin.attribute;

import com.acikek.purpeille.api.allegiance.AbyssallyAllegiantEntity;
import com.acikek.purpeille.api.allegiance.AllegianceData;
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

    private AllegianceData purpeille$allegianceData;

    @Override
    public AllegianceData getAllegianceData() {
        return purpeille$allegianceData;
    }

    @Shadow @Nullable public abstract EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    @Inject(method = "getEquipmentChanges", at = @At("TAIL"))
    private void purpeille$updateCyclicAllegiance(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        EntityAttributeInstance instance = getAttributeInstance(ModAttributes.GENERIC_ABYSSAL_ALLEGIANCE);
        if (instance == null || instance.getValue() == 0.0) {
            return;
        }
        if (purpeille$allegianceData == null) {
            purpeille$allegianceData = new AllegianceData(0, 0, 0, 0L);
        }
        if (instance.getValue() > purpeille$allegianceData.cyclic) {
            World world = ((Entity) (Object) this).world;
            if (purpeille$allegianceData.cyclic == 0 && world != null) {
                purpeille$allegianceData.initialTime = world.getTime();
            }
            purpeille$allegianceData.cyclic = (int) instance.getValue();
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void purpeille$toNbt(NbtCompound nbt, CallbackInfo ci) {
        if (purpeille$allegianceData == null) {
            return;
        }
        NbtCompound data = new NbtCompound();
        purpeille$allegianceData.writeNbt(data);
        if (!data.isEmpty()) {
            nbt.put(AllegianceData.KEY, data);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void purpeille$readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(AllegianceData.KEY)) {
            purpeille$allegianceData = AllegianceData.readNbt(nbt.getCompound(AllegianceData.KEY));
        }
    }
}
