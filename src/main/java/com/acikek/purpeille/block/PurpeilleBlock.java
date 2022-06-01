package com.acikek.purpeille.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class PurpeilleBlock extends Block {

    public static BooleanProperty GRAFFITI = BooleanProperty.of("graffiti");

    public static final Settings SETTINGS = QuiltBlockSettings.of(Material.METAL)
            .strength(8.0f)
            .sounds(BlockSoundGroup.METAL)
            .requiresTool();

    public PurpeilleBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(GRAFFITI, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.MAIN_HAND) {
            ItemStack handStack = player.getStackInHand(hand);
            if (handStack.isOf(Items.WHITE_DYE) && !state.get(GRAFFITI)) {
                if (!player.isCreative()) {
                    handStack.decrement(1);
                }
                world.setBlockState(pos, state.with(GRAFFITI, true));
                world.playSound(null, pos, SoundEvents.ITEM_GLOW_INK_SAC_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(GRAFFITI);
    }
}
