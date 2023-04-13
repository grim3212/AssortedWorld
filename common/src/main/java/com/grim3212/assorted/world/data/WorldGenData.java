package com.grim3212.assorted.world.data;

import com.google.common.collect.Lists;
import com.grim3212.assorted.lib.data.LibWorldGenProvider;
import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.api.WorldTags;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.gen.feature.WorldFeatures;
import com.grim3212.assorted.world.common.gen.feature.WorldTargets;
import com.grim3212.assorted.world.common.gen.structure.fountain.FountainStructure;
import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidStructure;
import com.grim3212.assorted.world.common.gen.structure.snowball.SnowballStructure;
import com.grim3212.assorted.world.common.gen.structure.waterdome.WaterDomeStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.material.Fluids;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldGenData extends LibWorldGenProvider {

    private static final ResourceLocation SNOWBALL_KEY = new ResourceLocation(Constants.MOD_ID, "snowball");
    private static final ResourceLocation PYRAMID_KEY = new ResourceLocation(Constants.MOD_ID, "pyramid");
    private static final ResourceLocation FOUNTAIN_KEY = new ResourceLocation(Constants.MOD_ID, "fountain");
    private static final ResourceLocation WATER_DOME_KEY = new ResourceLocation(Constants.MOD_ID, "water_dome");

    private static final ResourceKey<Structure> SNOWBALL_RESOURCE_KEY = structureResourceKey(SNOWBALL_KEY);
    private static final ResourceKey<Structure> PYRAMID_RESOURCE_KEY = structureResourceKey(PYRAMID_KEY);
    private static final ResourceKey<Structure> FOUNTAIN_RESOURCE_KEY = structureResourceKey(FOUNTAIN_KEY);
    private static final ResourceKey<Structure> WATER_DOME_RESOURCE_KEY = structureResourceKey(WATER_DOME_KEY);

    public static final ResourceLocation RUIN_KEY = new ResourceLocation(Constants.MOD_ID, "ruin");
    public static final ResourceLocation SPIRE_KEY = new ResourceLocation(Constants.MOD_ID, "spire");
    public static final ResourceLocation RANDOMITE_KEY = new ResourceLocation(Constants.MOD_ID, "ore_randomite");
    public static final ResourceLocation GUNPOWDER_REED_KEY = new ResourceLocation(Constants.MOD_ID, "patch_gunpowder_reed");

    private static ResourceKey<Structure> structureResourceKey(ResourceLocation key) {
        return ResourceKey.create(Registries.STRUCTURE, key);
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureResourceKey(ResourceLocation key) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, key);
    }

    private static Map<ResourceKey<Structure>, Structure> getStructures(BootstapContext<Structure> context) {
        Map<ResourceKey<Structure>, Structure> map = new HashMap<>();

        HolderGetter<Biome> holderGetter = context.lookup(Registries.BIOME);

        map.put(SNOWBALL_RESOURCE_KEY, new SnowballStructure(new Structure.StructureSettings(holderGetter.getOrThrow(WorldTags.Biomes.HAS_SNOWBALL), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
        map.put(PYRAMID_RESOURCE_KEY, new PyramidStructure(new Structure.StructureSettings(holderGetter.getOrThrow(WorldTags.Biomes.HAS_PYRAMID), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
        map.put(FOUNTAIN_RESOURCE_KEY, new FountainStructure(new Structure.StructureSettings(holderGetter.getOrThrow(WorldTags.Biomes.HAS_FOUNTAIN), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
        map.put(WATER_DOME_RESOURCE_KEY, new WaterDomeStructure(new Structure.StructureSettings(holderGetter.getOrThrow(WorldTags.Biomes.HAS_WATER_DOME), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));

        return map;
    }

    private static Map<ResourceLocation, StructureSet> getStructureSets(BootstapContext<StructureSet> context) {
        Map<ResourceLocation, StructureSet> map = new HashMap<>();

        HolderGetter<Structure> holderGetter = context.lookup(Registries.STRUCTURE);

        map.put(SNOWBALL_KEY, new StructureSet(holderGetter.getOrThrow(SNOWBALL_RESOURCE_KEY), new RandomSpreadStructurePlacement(36, 21, RandomSpreadType.LINEAR, 737462782)));
        map.put(PYRAMID_KEY, new StructureSet(holderGetter.getOrThrow(PYRAMID_RESOURCE_KEY), new RandomSpreadStructurePlacement(36, 10, RandomSpreadType.LINEAR, 827612344)));
        map.put(FOUNTAIN_KEY, new StructureSet(holderGetter.getOrThrow(FOUNTAIN_RESOURCE_KEY), new RandomSpreadStructurePlacement(32, 10, RandomSpreadType.LINEAR, 983497234)));
        map.put(WATER_DOME_KEY, new StructureSet(holderGetter.getOrThrow(WATER_DOME_RESOURCE_KEY), new RandomSpreadStructurePlacement(32, 14, RandomSpreadType.LINEAR, 432432568)));

        return map;
    }

    private static Map<ResourceLocation, ConfiguredFeature<?, ?>> getConfiguredFeatures() {
        Map<ResourceLocation, ConfiguredFeature<?, ?>> map = new HashMap<>();

        map.put(RUIN_KEY, new ConfiguredFeature<>(WorldFeatures.RUIN_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
        map.put(SPIRE_KEY, new ConfiguredFeature<>(WorldFeatures.SPIRE_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
        map.put(RANDOMITE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(WorldTargets.ORE_RANDOMITE_TARGET_LIST, 8)));
        map.put(GUNPOWDER_REED_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH, new RandomPatchConfiguration(20, 4, 0, PlacementUtils.inlinePlaced(Feature.BLOCK_COLUMN, BlockColumnConfiguration.simple(BiasedToBottomInt.of(2, 4), BlockStateProvider.simple(WorldBlocks.GUNPOWDER_REED.get())), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.wouldSurvive(WorldBlocks.GUNPOWDER_REED.get().defaultBlockState(), BlockPos.ZERO),
                BlockPredicate.anyOf(BlockPredicate.matchesFluids(new BlockPos(1, -1, 0), Fluids.WATER, Fluids.FLOWING_WATER), BlockPredicate.matchesFluids(new BlockPos(-1, -1, 0), Fluids.WATER, Fluids.FLOWING_WATER), BlockPredicate.matchesFluids(new BlockPos(0, -1, 1), Fluids.WATER, Fluids.FLOWING_WATER), BlockPredicate.matchesFluids(new BlockPos(0, -1, -1), Fluids.WATER, Fluids.FLOWING_WATER))))))));

        return map;
    }

    private static Map<ResourceLocation, PlacedFeature> getPlacedFeatures(BootstapContext<PlacedFeature> context) {
        Map<ResourceLocation, PlacedFeature> map = new HashMap<>();

        HolderGetter<ConfiguredFeature<?, ?>> holderGetter = context.lookup(Registries.CONFIGURED_FEATURE);

        map.put(RUIN_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(RUIN_KEY)), heightmapPlacement(350)));
        map.put(SPIRE_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(SPIRE_KEY)), heightmapPlacement(350)));
        map.put(RANDOMITE_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(RANDOMITE_KEY)), commonOrePlacement(12, HeightRangePlacement.triangle(VerticalAnchor.BOTTOM, VerticalAnchor.TOP))));
        map.put(GUNPOWDER_REED_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(GUNPOWDER_REED_KEY)), heightmapPlacement(8)));

        return map;
    }

    @Override
    public void addToWorldGem(RegistrySetBuilder builder) {
        builder.add(Registries.STRUCTURE, context -> {
            WorldGenData.getStructures(context).forEach((r, f) -> {
                context.register(r, f);
            });
        });

        builder.add(Registries.STRUCTURE_SET, context -> {
            WorldGenData.getStructureSets(context).forEach((r, f) -> {
                context.register(ResourceKey.create(Registries.STRUCTURE_SET, r), f);
            });
        });

        builder.add(Registries.CONFIGURED_FEATURE, context -> {
            WorldGenData.getConfiguredFeatures().forEach((r, f) -> {
                context.register(ResourceKey.create(Registries.CONFIGURED_FEATURE, r), f);
            });
        });

        builder.add(Registries.PLACED_FEATURE, context -> {
            WorldGenData.getPlacedFeatures(context).forEach((r, f) -> {
                context.register(ResourceKey.create(Registries.PLACED_FEATURE, r), f);
            });
        });
    }

    @Override
    public List<ResourceKey<? extends Registry<?>>> registries() {
        return Lists.newArrayList(Registries.STRUCTURE, Registries.STRUCTURE_SET, Registries.CONFIGURED_FEATURE, Registries.PLACED_FEATURE);
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier placement, PlacementModifier modifier) {
        return List.of(placement, InSquarePlacement.spread(), modifier, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier modifier) {
        return orePlacement(CountPlacement.of(count), modifier);
    }

    private static List<PlacementModifier> heightmapPlacement(int rarity) {
        return List.of(RarityFilter.onAverageOnceEvery(rarity), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
    }
}
