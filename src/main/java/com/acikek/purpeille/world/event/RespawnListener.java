package com.acikek.purpeille.world.event;

import com.acikek.purpeille.block.ancient.guardian.AncientGuardianBlockEntity;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class RespawnListener implements ServerPlayerEvents.AfterRespawn {

    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (newPlayer.getSpawnPointPosition() != null && newPlayer.world.getBlockEntity(newPlayer.getSpawnPointPosition()) instanceof AncientGuardianBlockEntity blockEntity) {
            System.out.println(blockEntity.linkedPlayer);
        }
    }
}
