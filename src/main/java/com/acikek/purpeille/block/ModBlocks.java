package com.acikek.purpeille.block;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.ancient.AncientMachine;
import com.acikek.purpeille.block.ancient.gateway.AncientGateway;
import com.acikek.purpeille.block.ancient.oven.AncientOven;
import com.acikek.purpeille.block.ancient.oven.Damage;
import com.acikek.purpeille.item.ModItems;
import lib.BlockItemProvider;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModBlocks {

    public static Block PURPUR_REMNANTS = new OreBlock(PurpurRemnants.SETTINGS, UniformIntProvider.create(2, 3));
    public static Block MONOLITHIC_PURPUR = new PillarBlock(BlockSettings.MONOLITHIC_PURPUR);
    public static Block REMNANT_BRICKS = new Block(BlockSettings.REMNANT_BRICKS);
    public static Block PURPEILLE_BLOCK = new Block(BlockSettings.PURPEILLE_BLOCK);
    public static Block ANCIENT_MECHANICAL_BRICKS = new Block(AncientMachine.SETTINGS);

    // Machines
    public static AncientGateway ANCIENT_GATEWAY = new AncientGateway(AncientGateway.SETTINGS);
    public static AncientOven ANCIENT_OVEN = Damage.NONE.createBlock();
    public static AncientOven ANCIENT_OVEN_DIM = Damage.DIM.createBlock();
    public static AncientOven ANCIENT_OVEN_VERY_DIM = Damage.VERY_DIM.createBlock();

    public static Map<String, Block> BLOCKS = new LinkedHashMap<>();

    static {
        BLOCKS.put("purpur_remnants", PURPUR_REMNANTS);
        BLOCKS.put("monolithic_purpur", MONOLITHIC_PURPUR);
        BLOCKS.put("remnant_bricks", REMNANT_BRICKS);
        BLOCKS.put("purpeille_block", PURPEILLE_BLOCK);
        BLOCKS.put("ancient_mechanical_bricks", ANCIENT_MECHANICAL_BRICKS);
        BLOCKS.put("ancient_gateway", ANCIENT_GATEWAY);
        BLOCKS.put("ancient_oven", ANCIENT_OVEN);
        BLOCKS.put("ancient_oven_dim", ANCIENT_OVEN_DIM);
        BLOCKS.put("ancient_oven_very_dim", ANCIENT_OVEN_VERY_DIM);
    }

    public static void register() {
        for (Map.Entry<String, Block> pair : BLOCKS.entrySet()) {
            Identifier id = Purpeille.id(pair.getKey());
            Registry.register(Registry.BLOCK, id, pair.getValue());
            Registry.register(Registry.ITEM, id, BlockItemProvider.getBlockItem(pair.getValue(), ModItems.defaultSettings()));
        }
    }
}
