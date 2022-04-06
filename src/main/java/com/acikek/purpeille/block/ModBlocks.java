package com.acikek.purpeille.block;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModBlocks {

    public static Block PURPUR_REMNANTS = new Block(PurpurRemnants.SETTINGS);
    public static Block MONOLITHIC_PURPUR = new Block(FabricBlockSettings.of(Material.STONE).strength(5.0f).sounds(BlockSoundGroup.BONE).requiresTool());
    public static Block REMNANT_BRICKS = new Block(FabricBlockSettings.of(Material.STONE).strength(5.0f).sounds(BlockSoundGroup.DEEPSLATE_BRICKS).requiresTool());
    public static Block PURPEILLE_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(8.0f).sounds(BlockSoundGroup.METAL).requiresTool());

    public static Map<String, Block> BLOCKS = new LinkedHashMap<>();

    static {
        BLOCKS.put("purpur_remnants", PURPUR_REMNANTS);
        BLOCKS.put("monolithic_purpur", MONOLITHIC_PURPUR);
        BLOCKS.put("remnant_bricks", REMNANT_BRICKS);
        BLOCKS.put("purpeille_block", PURPEILLE_BLOCK);
    }

    public static void register() {
        for (Map.Entry<String, Block> pair : BLOCKS.entrySet()) {
            Identifier id = Purpeille.id(pair.getKey());
            Registry.register(Registry.BLOCK, id, pair.getValue());
            Registry.register(Registry.ITEM, id, new BlockItem(pair.getValue(), ModItems.defaultSettings()));
        }
    }
}
