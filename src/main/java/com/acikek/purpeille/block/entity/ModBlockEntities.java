package com.acikek.purpeille.block.entity;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.entity.ancient.gateway.AncientGatewayBlockEntity;
import com.acikek.purpeille.block.entity.ancient.guardian.AncientGuardianBlockEntity;
import com.acikek.purpeille.block.entity.ancient.oven.AncientOvenBlockEntity;
import com.acikek.purpeille.block.entity.monolithic.MonolithicPurpurBlockEntity;
import com.acikek.purpeille.block.entity.rubble.EndRubbleBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlockEntities {

    public static void register() {
        EndRubbleBlockEntity.register();
        AncientGatewayBlockEntity.register();
        AncientGuardianBlockEntity.register();
        AncientOvenBlockEntity.register();
        MonolithicPurpurBlockEntity.register();
    }

    public static <T extends BlockEntity> BlockEntityType<T> build(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Purpeille.id(id),
                FabricBlockEntityTypeBuilder.create(factory, blocks)
                        .build(null)
        );
    }
}
