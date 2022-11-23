package com.acikek.purpeille.api.abyssal;

import com.acikek.purpeille.impl.AmalgamatedSpyglassImpl;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class AmalgamatedSpyglass {

    public static final Event<ItemObserved> ITEM_OBSERVED = EventFactory.createArrayBacked(
            ItemObserved.class,
            listeners -> (player, observed, token) -> {
                for (ItemObserved listener : listeners) {
                    listener.onObserved(player, observed, token);
                }
            });

    public static boolean isUsingAmalgamatedSpyglass(LivingEntity livingEntity) {
        return AmalgamatedSpyglassImpl.isUsingAmalgamatedSpyglass(livingEntity);
    }

    @FunctionalInterface
    public interface ItemObserved {
        void onObserved(ServerPlayerEntity player, ItemStack observed, boolean token);
    }
}
