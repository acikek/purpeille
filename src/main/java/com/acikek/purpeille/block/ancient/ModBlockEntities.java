package com.acikek.purpeille.block.ancient;

import com.acikek.purpeille.block.ancient.gateway.AncientGatewayBlockEntity;
import com.acikek.purpeille.block.ancient.guardian.AncientGuardianBlockEntity;
import com.acikek.purpeille.block.ancient.oven.AncientOvenBlockEntity;

public class ModBlockEntities {

    public static void register() {
        AncientGatewayBlockEntity.register();
        AncientGuardianBlockEntity.register();
        AncientOvenBlockEntity.register();
    }
}
