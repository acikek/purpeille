package com.acikek.purpeille.block.ancient;

import com.acikek.purpeille.item.core.EncasedCore;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public abstract class CorePoweredAncientMachine<T extends CorePoweredAncientMachineBlockEntity> extends AncientMachine<T> {

    public Class<T> blockEntityClass;
    public boolean playerCheckCore;

    public CorePoweredAncientMachine(Settings settings, BlockEntityTicker<T> ticker, BiFunction<BlockPos, BlockState, T> supplier, Class<T> blockEntityClass, boolean playerCheckCore) {
        super(settings, ticker, supplier);
        this.blockEntityClass = blockEntityClass;
        this.playerCheckCore = playerCheckCore;
    }

    public boolean canPlayerUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, T blockEntity) {
        return true;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.MAIN_HAND && blockEntityClass.isInstance(world.getBlockEntity(pos))) {
            T blockEntity = blockEntityClass.cast(world.getBlockEntity(pos));
            if (blockEntity == null) {
                return ActionResult.PASS;
            }
            if (playerCheckCore && blockEntity.playerCheckCore(player, hand)) {
                return ActionResult.SUCCESS;
            }
            ItemStack handStack = player.getStackInHand(hand);
            if (!blockEntity.isEmpty()) {
                if (canPlayerUse(state, world, pos, player, hand, blockEntity)) {
                    blockEntity.removeCore(world, player, true, pos, state);
                    return ActionResult.SUCCESS;
                }
            }
            else if (handStack.getItem() instanceof EncasedCore) {
                blockEntity.addCore(world, handStack, true, player, pos, state);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}
