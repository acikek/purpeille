package com.acikek.purpeille.structure;

import com.acikek.purpeille.Purpeille;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class EndLandStructure extends Structure {

    public static StructureType<EndLandStructure> TYPE;

    public static final Codec<EndLandStructure> CODEC = RecordCodecBuilder.<EndLandStructure>mapCodec(instance ->
            instance.group(EndLandStructure.configCodecBuilder(instance),
                    StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, EndLandStructure::new)).codec();

    public RegistryEntry<StructurePool> startPool;
    public Optional<Identifier> startJigsawName;
    public int size;
    public HeightProvider startHeight;
    public Optional<Heightmap.Type> projectStartToHeightmap;
    public int maxDistanceFromCenter;

    public EndLandStructure(Config config, RegistryEntry<StructurePool> startPool, Optional<Identifier> startJigsawName, int size, HeightProvider startHeight, Optional<Heightmap.Type> projectStartToHeightmap, int maxDistanceFromCenter) {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    @Override
    public Optional<StructurePosition> getStructurePosition(Context context) {
        ChunkPos chunkPos = context.chunkPos();
        int height = context.chunkGenerator().getHeightInGround(
                chunkPos.getStartX(),
                chunkPos.getStartZ(),
                Heightmap.Type.WORLD_SURFACE_WG,
                context.world(),
                context.noiseConfig()
        );
        if (height < 40) {
            return Optional.empty();
        }
        int startY = startHeight.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), startY, chunkPos.getStartZ());
        return StructurePoolBasedGenerator.generate(
                context,
                startPool,
                startJigsawName,
                size,
                blockPos,
                false,
                projectStartToHeightmap,
                maxDistanceFromCenter
        );
    }

    @Override
    public StructureType<?> getType() {
        return null;
    }

    public static void register() {
        TYPE = Registry.register(Registries.STRUCTURE_TYPE, Purpeille.id("end_land"), () -> CODEC);
    }
}
