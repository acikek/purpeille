package com.acikek.purpeille.mixin.client;

import com.acikek.purpeille.block.ModBlocks;
import com.acikek.purpeille.client.PurpeilleClient;
import com.acikek.purpeille.item.ModItems;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow @Final private ItemModels models;

    @ModifyVariable(method = "renderItem", at = @At("HEAD"), argsOnly = true)
    private BakedModel purpeille$modifyModel(BakedModel defaultModel, ItemStack stack, ModelTransformation.Mode renderMode) {
        if (renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.GROUND) {
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == ModBlocks.ANCIENT_GUARDIAN) {
                return models.getModelManager().getModel(PurpeilleClient.GUARDIAN_HAND_MODEL);
            }
            else if (stack.isOf(ModItems.AMALGAMATED_SPYGLASS)) {
                return models.getModelManager().getModel(PurpeilleClient.AMALGAMATED_SPYGLASS_HAND_MODEL);
            }
        }
        return defaultModel;
    }
}
