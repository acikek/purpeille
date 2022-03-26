package com.acikek.purpeille.world.gen;

import com.acikek.purpeille.Purpeille;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.StructureSetKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

import java.util.Random;
import java.util.stream.Stream;

public class EndCityProximityPlacementModifier extends PlacementModifier {

    public static final Codec<EndCityProximityPlacementModifier> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    IntProvider.createValidatingCodec(1, 8).fieldOf("distance").forGetter(m -> m.distance)
            ).apply(instance, EndCityProximityPlacementModifier::new)
    );

    public static PlacementModifierType<EndCityProximityPlacementModifier> END_CITY_PROXIMITY;

    public IntProvider distance;

    public EndCityProximityPlacementModifier(IntProvider distance) {
        this.distance = distance;
    }

    public EndCityProximityPlacementModifier(int distance) {
        this(ConstantIntProvider.create(distance));
    }

    @Override
    public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
        ChunkPos chunk = new ChunkPos(pos);
        boolean withinEndCity = context.getChunkGenerator().method_41053(StructureSetKeys.END_CITIES, context.getWorld().getSeed(), chunk.x, chunk.z, distance.get(context.getWorld().getRandom()));
        return withinEndCity ? Stream.of(pos) : Stream.empty();
    }

    @Override
    public PlacementModifierType<?> getType() {
        return END_CITY_PROXIMITY;
    }

    public static void register() {
        END_CITY_PROXIMITY = Registry.register(Registry.PLACEMENT_MODIFIER_TYPE, Purpeille.id("end_city_proximity"), () -> CODEC);
    }
}
