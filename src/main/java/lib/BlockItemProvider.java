package lib;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import java.util.function.BiFunction;

public interface BlockItemProvider {

    BiFunction<Block, Item.Settings, BlockItem> getBlockItem();

    static BlockItem getBlockItem(Block block, Item.Settings settings) {
        return block instanceof BlockItemProvider provider
                ? provider.getBlockItem().apply(block, settings)
                : new BlockItem(block, settings);
    }
}
