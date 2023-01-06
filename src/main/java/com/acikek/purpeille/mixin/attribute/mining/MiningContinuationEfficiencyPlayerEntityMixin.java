package com.acikek.purpeille.mixin.attribute.mining;

import com.acikek.purpeille.attribute.MiningContinuationEfficiency;
import com.acikek.purpeille.attribute.ModAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class MiningContinuationEfficiencyPlayerEntityMixin implements MiningContinuationEfficiency.DowntimeTracker {

    @Override
    public void setIsNotMining() {
        Entity entity = (Entity) (Object) this;
        entity.getDataTracker().set(MiningContinuationEfficiency.IS_NOT_MINING, true);
        entity.getDataTracker().set(MiningContinuationEfficiency.NOT_MINING_TICKS, 10);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void purpeille$addTrackedData(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        entity.getDataTracker().startTracking(MiningContinuationEfficiency.CONTINUOUS_BLOCKS_MINED, 0);
        entity.getDataTracker().startTracking(MiningContinuationEfficiency.IS_NOT_MINING, false);
        entity.getDataTracker().startTracking(MiningContinuationEfficiency.NOT_MINING_TICKS, 0);
    }

    private boolean getIsNotMining() {
        Entity entity = (Entity) (Object) this;
        return entity.getDataTracker().get(MiningContinuationEfficiency.IS_NOT_MINING);
    }

    private int getNotMiningTicks() {
        Entity entity = (Entity) (Object) this;
        return entity.getDataTracker().get(MiningContinuationEfficiency.NOT_MINING_TICKS);
    }

    private int getContinuousBlocksMined() {
        Entity entity = (Entity) (Object) this;
        return entity.getDataTracker().get(MiningContinuationEfficiency.CONTINUOUS_BLOCKS_MINED);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void purpeille$tickCooldown(CallbackInfo ci) {
        if (getIsNotMining()) {
            Entity entity = (Entity) (Object) this;
            entity.getDataTracker().set(MiningContinuationEfficiency.NOT_MINING_TICKS, getNotMiningTicks() - 1);
            if (getNotMiningTicks() <= 0) {
                entity.getDataTracker().set(MiningContinuationEfficiency.IS_NOT_MINING, false);
                entity.getDataTracker().set(MiningContinuationEfficiency.CONTINUOUS_BLOCKS_MINED, 0);
            }
        }
    }

    @ModifyVariable(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"), index = 2)
    private float purpeille$applyMiningContinuationEfficiency(float i) {
        LivingEntity entity = (LivingEntity) (Object) this;
        EntityAttributeInstance instance = entity.getAttributeInstance(ModAttributes.GENERIC_MINING_CONTINUATION_EFFICIENCY);
        if (instance == null || instance.getValue() == 0.0) {
            return i;
        }
        double level = (instance.getValue() / 64.0) * getContinuousBlocksMined();
        return i + 1.0f * (float) level * 0.2f;
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void purpeille$readNbt(NbtCompound nbt, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        NbtCompound compound = nbt.getCompound("MiningContinuationEfficiency");
        entity.getDataTracker().set(MiningContinuationEfficiency.CONTINUOUS_BLOCKS_MINED, compound.getInt("ContinuousBlocksMined"));
        entity.getDataTracker().set(MiningContinuationEfficiency.IS_NOT_MINING, compound.getBoolean("IsNotMining"));
        entity.getDataTracker().set(MiningContinuationEfficiency.NOT_MINING_TICKS, compound.getInt("NotMiningTicks"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void purpeille$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound compound = new NbtCompound();
        compound.putInt("ContinuousBlocksMined", getContinuousBlocksMined());
        compound.putBoolean("IsNotMining", getIsNotMining());
        compound.putInt("NotMiningTicks", getNotMiningTicks());
        nbt.put("MiningContinuationEfficiency", compound);
    }
}
