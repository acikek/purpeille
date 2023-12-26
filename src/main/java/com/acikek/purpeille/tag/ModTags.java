package com.acikek.purpeille.tag;

import com.acikek.purpeille.Purpeille;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModTags {

    public static final TagKey<Item> WARPATH_BASE = TagKey.of(RegistryKeys.ITEM, Purpeille.id("warpath_base"));
    public static final TagKey<Item> ASPECT_CATALYST = TagKey.of(RegistryKeys.ITEM, Purpeille.id("aspect_catalyst"));
    public static final TagKey<Item> REVELATION_CATALYST = TagKey.of(RegistryKeys.ITEM, Purpeille.id("revelation_catalyst"));

    public static final TagKey<Block> MINING_EXPERIENCE = TagKey.of(RegistryKeys.BLOCK, Purpeille.id("mining_experience"));
    public static final TagKey<Block> PURPEILLE_PICKAXE_HASTENERS = TagKey.of(RegistryKeys.BLOCK, Purpeille.id("purpeille_pickaxe_hasteners"));
}
