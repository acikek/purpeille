package com.acikek.purpeille.tag;

import com.acikek.purpeille.Purpeille;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public class ModTags {

    public static final TagKey<Item> WARPATH_BASE = TagKey.of(Registry.ITEM_KEY, Purpeille.id("warpath_base"));
    public static final TagKey<Item> ASPECT_CATALYST = TagKey.of(Registry.ITEM_KEY, Purpeille.id("aspect_catalyst"));
    public static final TagKey<Item> REVELATION_CATALYST = TagKey.of(Registry.ITEM_KEY, Purpeille.id("revelation_catalyst"));

    public static final TagKey<Block> MINING_EXPERIENCE = TagKey.of(Registry.BLOCK_KEY, Purpeille.id("mining_experience"));
    public static final TagKey<Block> PURPEILLE_PICKAXE_HASTENERS = TagKey.of(Registry.BLOCK_KEY, Purpeille.id("purpeille_pickaxe_hasteners"));
}
