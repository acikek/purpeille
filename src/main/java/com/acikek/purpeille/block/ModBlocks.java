package com.acikek.purpeille.block;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.ancient.gateway.AncientGateway;
import com.acikek.purpeille.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModBlocks {

    public static Block PURPUR_REMNANTS = new OreBlock(PurpurRemnants.SETTINGS, UniformIntProvider.create(2, 3));
    public static Block MONOLITHIC_PURPUR = new Block(BlockSettings.MONOLITHIC_PURPUR);
    public static Block REMNANT_BRICKS = new Block(BlockSettings.REMNANT_BRICKS);
    public static Block PURPEILLE_BLOCK = new Block(BlockSettings.PURPEILLE_BLOCK);
    public static Block ANCIENT_MECHANICAL_BRICKS = new Block(BlockSettings.ANCIENT_MECHANICAL_BRICKS);
    public static AncientGateway ANCIENT_GATEWAY = new AncientGateway(AncientGateway.SETTINGS);

    public static Map<String, Block> BLOCKS = new LinkedHashMap<>();

    static {
        BLOCKS.put("purpur_remnants", PURPUR_REMNANTS);
        BLOCKS.put("monolithic_purpur", MONOLITHIC_PURPUR);
        BLOCKS.put("remnant_bricks", REMNANT_BRICKS);
        BLOCKS.put("purpeille_block", PURPEILLE_BLOCK);
        BLOCKS.put("ancient_mechanical_bricks", ANCIENT_MECHANICAL_BRICKS);
        BLOCKS.put("ancient_gateway", ANCIENT_GATEWAY);
    }

    public static void register() {
        for (Map.Entry<String, Block> pair : BLOCKS.entrySet()) {
            Identifier id = Purpeille.id(pair.getKey());
            Registry.register(Registry.BLOCK, id, pair.getValue());
            Registry.register(Registry.ITEM, id, new BlockItem(pair.getValue(), ModItems.defaultSettings()));
        }
    }
}
