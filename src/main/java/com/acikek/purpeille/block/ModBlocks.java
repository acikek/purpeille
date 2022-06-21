package com.acikek.purpeille.block;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.block.entity.ancient.gateway.AncientGateway;
import com.acikek.purpeille.block.entity.ancient.guardian.AncientGuardian;
import com.acikek.purpeille.block.entity.ancient.oven.AncientOven;
import com.acikek.purpeille.block.entity.ancient.oven.Damage;
import com.acikek.purpeille.block.entity.rubble.EndRubble;
import com.acikek.purpeille.item.ModItems;
import lib.BlockItemProvider;
import net.minecraft.block.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModBlocks {

    // Blocks
    public static final Block PURPUR_REMNANTS = new OreBlock(PurpurRemnants.SETTINGS, UniformIntProvider.create(2, 3));
    public static final Block REMNANT_BRICKS = new Block(BlockSettings.REMNANT_BRICKS);
    public static final Block ANCIENT_MECHANICAL_BRICKS = new ChorusInfestedBlocks.InfestedBlock(BlockSettings.ANCIENT_MACHINE);
    public static final EndRubble END_RUBBLE = new EndRubble(EndRubble.SETTINGS);

    // Machines
    public static final AncientGateway ANCIENT_GATEWAY = new AncientGateway(AncientGateway.SETTINGS);
    public static final AncientGuardian ANCIENT_GUARDIAN = new AncientGuardian(AncientGuardian.SETTINGS);
    public static final AncientOven ANCIENT_OVEN = Damage.NONE.createBlock();
    public static final AncientOven ANCIENT_OVEN_DIM = Damage.DIM.createBlock();
    public static final AncientOven ANCIENT_OVEN_VERY_DIM = Damage.VERY_DIM.createBlock();

    // Decorations
    public static final MonolithicPurpur MONOLITHIC_PURPUR = new MonolithicPurpur(MonolithicPurpur.SETTINGS);
    public static final SlabBlock REMNANT_BRICK_SLAB = new SlabBlock(BlockSettings.REMNANT_BRICKS);
    public static final StairsBlock REMNANT_BRICK_STAIRS = new StairsBlock(REMNANT_BRICKS.getDefaultState(), BlockSettings.REMNANT_BRICKS);
    public static final WallBlock REMNANT_BRICK_WALL = new WallBlock(BlockSettings.REMNANT_BRICKS);
    public static final SlabBlock ANCIENT_MECHANICAL_BRICK_SLAB = new ChorusInfestedBlocks.Slab(BlockSettings.ANCIENT_MACHINE);
    public static final StairsBlock ANCIENT_MECHANICAL_BRICK_STAIRS = new ChorusInfestedBlocks.Stairs(ANCIENT_MECHANICAL_BRICKS.getDefaultState(), BlockSettings.ANCIENT_MACHINE);
    public static final WallBlock ANCIENT_MECHANICAL_BRICK_WALL = new ChorusInfestedBlocks.Wall(BlockSettings.ANCIENT_MACHINE);
    public static final Block RUINED_MECHANICAL_BRICKS = new Block(BlockSettings.ANCIENT_MACHINE);
    public static final SlabBlock RUINED_MECHANICAL_BRICK_SLAB = new SlabBlock(BlockSettings.ANCIENT_MACHINE);
    public static final StairsBlock RUINED_MECHANICAL_BRICK_STAIRS = new StairsBlock(RUINED_MECHANICAL_BRICKS.getDefaultState(), BlockSettings.ANCIENT_MACHINE);
    public static final WallBlock RUINED_MECHANICAL_BRICK_WALL = new WallBlock(BlockSettings.ANCIENT_MACHINE);
    public static final Block DEMOLISHED_MECHANICAL_BRICKS = new Block(BlockSettings.ANCIENT_MACHINE);
    public static final SlabBlock DEMOLISHED_MECHANICAL_BRICK_SLAB = new SlabBlock(BlockSettings.ANCIENT_MACHINE);
    public static final StairsBlock DEMOLISHED_MECHANICAL_BRICK_STAIRS = new StairsBlock(DEMOLISHED_MECHANICAL_BRICKS.getDefaultState(), BlockSettings.ANCIENT_MACHINE);
    public static final WallBlock DEMOLISHED_MECHANICAL_BRICK_WALL = new WallBlock(BlockSettings.ANCIENT_MACHINE);
    public static final Block CHISELED_MECHANICAL_BRICKS = new Block(BlockSettings.ANCIENT_MACHINE);
    public static final Block CHORUS_INFESTED_MECHANICAL_BRICKS = new ChorusInfestedBlocks.InfestedBlock(BlockSettings.INFESTED_BLOCK);
    public static final SlabBlock CHORUS_INFESTED_MECHANICAL_BRICK_SLAB = new ChorusInfestedBlocks.Slab(BlockSettings.INFESTED_BLOCK);
    public static final StairsBlock CHORUS_INFESTED_MECHANICAL_BRICK_STAIRS = new ChorusInfestedBlocks.Stairs(CHORUS_INFESTED_MECHANICAL_BRICKS.getDefaultState(), BlockSettings.INFESTED_BLOCK);
    public static final WallBlock CHORUS_INFESTED_MECHANICAL_BRICK_WALL = new ChorusInfestedBlocks.Wall(BlockSettings.INFESTED_BLOCK);
    public static final Block CHORAL_BLOOM_INFESTED_MECHANICAL_BRICKS = new Block(BlockSettings.INFESTED_BLOCK);
    public static final SlabBlock CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_SLAB = new SlabBlock(BlockSettings.INFESTED_BLOCK);
    public static final StairsBlock CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_STAIRS = new StairsBlock(CHORAL_BLOOM_INFESTED_MECHANICAL_BRICKS.getDefaultState(), BlockSettings.INFESTED_BLOCK);
    public static final WallBlock CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_WALL = new WallBlock(BlockSettings.INFESTED_BLOCK);
    public static final PurpeilleBlock PURPEILLE_BLOCK = new PurpeilleBlock(PurpeilleBlock.SETTINGS);
    public static final SlabBlock PURPEILLE_SLAB = new SlabBlock(PurpeilleBlock.SETTINGS);
    public static final StairsBlock PURPEILLE_STAIRS = new StairsBlock(PURPEILLE_BLOCK.getDefaultState(), PurpeilleBlock.SETTINGS);
    public static final Block SMOLDERED_PURPEILLE_BLOCK = new Block(PurpeilleBlock.SETTINGS);
    public static final SlabBlock SMOLDERED_PURPEILLE_SLAB = new SlabBlock(PurpeilleBlock.SETTINGS);
    public static final StairsBlock SMOLDERED_PURPEILLE_STAIRS = new StairsBlock(SMOLDERED_PURPEILLE_BLOCK.getDefaultState(), PurpeilleBlock.SETTINGS);
    public static final Block ULTRAVIOLET_COMPLEX = new UltravioletComplex(UltravioletComplex.SETTINGS, 9);
    public static final Block POLISHED_ULTRAVIOLET_COMPLEX = new UltravioletComplex(UltravioletComplex.POLISHED_SETTINGS, 7);
    public static final Block LIGHTNING_BLOCK = new Block(BlockSettings.LIGHTNING_BLOCK);
    public static final SlabBlock LIGHTNING_SLAB = new SlabBlock(BlockSettings.LIGHTNING_BLOCK);
    public static final StairsBlock LIGHTNING_STAIRS = new StairsBlock(LIGHTNING_BLOCK.getDefaultState(), BlockSettings.LIGHTNING_BLOCK);
    public static final Block CUT_LIGHTNING_BLOCK = new Block(BlockSettings.LIGHTNING_BLOCK);
    public static final SlabBlock CUT_LIGHTNING_SLAB = new SlabBlock(BlockSettings.LIGHTNING_BLOCK);
    public static final StairsBlock CUT_LIGHTNING_STAIRS = new StairsBlock(CUT_LIGHTNING_BLOCK.getDefaultState(), BlockSettings.LIGHTNING_BLOCK);

    public static Map<String, Block> BLOCKS = new LinkedHashMap<>();
    public static Map<String, Block> DECORATIONS = new LinkedHashMap<>();

    static {
        BLOCKS.put("purpur_remnants", PURPUR_REMNANTS);
        BLOCKS.put("remnant_bricks", REMNANT_BRICKS);
        BLOCKS.put("ancient_mechanical_bricks", ANCIENT_MECHANICAL_BRICKS);
        BLOCKS.put("end_rubble", END_RUBBLE);
        BLOCKS.put("ancient_gateway", ANCIENT_GATEWAY);
        BLOCKS.put("ancient_guardian", ANCIENT_GUARDIAN);
        BLOCKS.put("ancient_oven", ANCIENT_OVEN);
        BLOCKS.put("ancient_oven_dim", ANCIENT_OVEN_DIM);
        BLOCKS.put("ancient_oven_very_dim", ANCIENT_OVEN_VERY_DIM);
        DECORATIONS.put("monolithic_purpur", MONOLITHIC_PURPUR);
        DECORATIONS.put("remnant_brick_slab", REMNANT_BRICK_SLAB);
        DECORATIONS.put("remnant_brick_stairs", REMNANT_BRICK_STAIRS);
        DECORATIONS.put("remnant_brick_wall", REMNANT_BRICK_WALL);
        DECORATIONS.put("ancient_mechanical_brick_slab", ANCIENT_MECHANICAL_BRICK_SLAB);
        DECORATIONS.put("ancient_mechanical_brick_stairs", ANCIENT_MECHANICAL_BRICK_STAIRS);
        DECORATIONS.put("ancient_mechanical_brick_wall", ANCIENT_MECHANICAL_BRICK_WALL);
        DECORATIONS.put("ruined_mechanical_bricks", RUINED_MECHANICAL_BRICKS);
        DECORATIONS.put("ruined_mechanical_brick_slab", RUINED_MECHANICAL_BRICK_SLAB);
        DECORATIONS.put("ruined_mechanical_brick_stairs", RUINED_MECHANICAL_BRICK_STAIRS);
        DECORATIONS.put("ruined_mechanical_brick_wall", RUINED_MECHANICAL_BRICK_WALL);
        DECORATIONS.put("demolished_mechanical_bricks", DEMOLISHED_MECHANICAL_BRICKS);
        DECORATIONS.put("demolished_mechanical_brick_slab", DEMOLISHED_MECHANICAL_BRICK_SLAB);
        DECORATIONS.put("demolished_mechanical_brick_stairs", DEMOLISHED_MECHANICAL_BRICK_STAIRS);
        DECORATIONS.put("demolished_mechanical_brick_wall", DEMOLISHED_MECHANICAL_BRICK_WALL);
        DECORATIONS.put("chiseled_mechanical_bricks", CHISELED_MECHANICAL_BRICKS);
        DECORATIONS.put("chorus_infested_mechanical_bricks", CHORUS_INFESTED_MECHANICAL_BRICKS);
        DECORATIONS.put("chorus_infested_mechanical_brick_slab", CHORUS_INFESTED_MECHANICAL_BRICK_SLAB);
        DECORATIONS.put("chorus_infested_mechanical_brick_stairs", CHORUS_INFESTED_MECHANICAL_BRICK_STAIRS);
        DECORATIONS.put("chorus_infested_mechanical_brick_wall", CHORUS_INFESTED_MECHANICAL_BRICK_WALL);
        DECORATIONS.put("choral_bloom_infested_mechanical_bricks", CHORAL_BLOOM_INFESTED_MECHANICAL_BRICKS);
        DECORATIONS.put("choral_bloom_infested_mechanical_brick_slab", CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_SLAB);
        DECORATIONS.put("choral_bloom_infested_mechanical_brick_stairs", CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_STAIRS);
        DECORATIONS.put("choral_bloom_infested_mechanical_brick_wall", CHORAL_BLOOM_INFESTED_MECHANICAL_BRICK_WALL);
        DECORATIONS.put("purpeille_block", PURPEILLE_BLOCK);
        DECORATIONS.put("purpeille_slab", PURPEILLE_SLAB);
        DECORATIONS.put("purpeille_stairs", PURPEILLE_STAIRS);
        DECORATIONS.put("smoldered_purpeille_block", SMOLDERED_PURPEILLE_BLOCK);
        DECORATIONS.put("smoldered_purpeille_slab", SMOLDERED_PURPEILLE_SLAB);
        DECORATIONS.put("smoldered_purpeille_stairs", SMOLDERED_PURPEILLE_STAIRS);
        DECORATIONS.put("ultraviolet_complex", ULTRAVIOLET_COMPLEX);
        DECORATIONS.put("polished_ultraviolet_complex", POLISHED_ULTRAVIOLET_COMPLEX);
        DECORATIONS.put("lightning_block", LIGHTNING_BLOCK);
        DECORATIONS.put("lightning_slab", LIGHTNING_SLAB);
        DECORATIONS.put("lightning_stairs", LIGHTNING_STAIRS);
        DECORATIONS.put("cut_lightning_block", CUT_LIGHTNING_BLOCK);
        DECORATIONS.put("cut_lightning_slab", CUT_LIGHTNING_SLAB);
        DECORATIONS.put("cut_lightning_stairs", CUT_LIGHTNING_STAIRS);
    }

    public static void register(Map<String, Block> blocks) {
        for (Map.Entry<String, Block> pair : blocks.entrySet()) {
            Identifier id = Purpeille.id(pair.getKey());
            Registry.register(Registry.BLOCK, id, pair.getValue());
            Registry.register(Registry.ITEM, id, BlockItemProvider.getBlockItem(pair.getValue(), ModItems.defaultSettings()));
        }
    }
}
