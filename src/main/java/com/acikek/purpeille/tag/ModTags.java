package com.acikek.purpeille.tag;

import com.acikek.purpeille.Purpeille;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public class ModTags {

    public static final TagKey<Item> WARPATH_BASE = TagKey.of(Registry.ITEM_KEY, Purpeille.id("warpath_base"));
    public static final TagKey<Block> MINING_EXPERIENCE = TagKey.of(Registry.BLOCK_KEY, Purpeille.id("mining_experience"));
}
