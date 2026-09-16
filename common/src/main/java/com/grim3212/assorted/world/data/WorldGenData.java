package com.grim3212.assorted.world.data;

import com.google.common.collect.Lists;
import com.grim3212.assorted.lib.data.LibDatapackRegistryProvider;
import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.api.WorldTags;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.gen.feature.WorldFeatures;
import com.grim3212.assorted.world.common.gen.feature.WorldTargets;
import com.grim3212.assorted.world.common.gen.placement.ConfigRarityFilter;
import com.grim3212.assorted.world.common.gen.placement.WorldPlacements;
import com.grim3212.assorted.world.common.gen.structure.fountain.FountainStructure;
import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidStructure;
import com.grim3212.assorted.world.common.gen.structure.snowball.SnowballStructure;
import com.grim3212.assorted.world.common.gen.structure.waterdome.WaterDomeStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
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

public class WorldGenData extends LibDatapackRegistryProvider {

    private static final Identifier SNOWBALL_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "snowball");
    private static final Identifier PYRAMID_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pyramid");
    private static final Identifier FOUNTAIN_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "fountain");
    private static final Identifier WATER_DOME_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "water_dome");

    private static final ResourceKey<Structure> SNOWBALL_RESOURCE_KEY = structureResourceKey(SNOWBALL_KEY);
    private static final ResourceKey<Structure> PYRAMID_RESOURCE_KEY = structureResourceKey(PYRAMID_KEY);
    private static final ResourceKey<Structure> FOUNTAIN_RESOURCE_KEY = structureResourceKey(FOUNTAIN_KEY);
    private static final ResourceKey<Structure> WATER_DOME_RESOURCE_KEY = structureResourceKey(WATER_DOME_KEY);

    public static final Identifier RUIN_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "ruin");
    public static final Identifier SPIRE_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "spire");
    public static final Identifier RANDOMITE_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "ore_randomite");
    public static final Identifier GUNPOWDER_REED_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "patch_gunpowder_reed");

    public static final Identifier FLOATING_ISLAND_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "floating_island");
    public static final Identifier DESERT_WELL_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "desert_well");
    public static final Identifier CROP_FIELD_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "crop_field");
    public static final Identifier CACTUS_FIELD_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "cactus_field");
    public static final Identifier SAND_PILLAR_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sand_pillar");
    public static final Identifier SAND_PIT_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sand_pit");
    public static final Identifier SAPLING_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "patch_saplings");
    public static final Identifier TREE_STUMP_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "tree_stumps");
    public static final Identifier MELON_KEY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "patch_melons");

    private static ResourceKey<Structure> structureResourceKey(Identifier key) {
        return ResourceKey.create(Registries.STRUCTURE, key);
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureResourceKey(Identifier key) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, key);
    }

    private static Map<ResourceKey<Structure>, Structure> getStructures(BootstrapContext<Structure> context) {
        Map<ResourceKey<Structure>, Structure> map = new HashMap<>();

        HolderGetter<Biome> holderGetter = context.lookup(Registries.BIOME);

        map.put(SNOWBALL_RESOURCE_KEY, new SnowballStructure(new Structure.StructureSettings(holderGetter.getOrThrow(WorldTags.Biomes.HAS_SNOWBALL), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
        map.put(PYRAMID_RESOURCE_KEY, new PyramidStructure(new Structure.StructureSettings(holderGetter.getOrThrow(WorldTags.Biomes.HAS_PYRAMID), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
        map.put(FOUNTAIN_RESOURCE_KEY, new FountainStructure(new Structure.StructureSettings(holderGetter.getOrThrow(WorldTags.Biomes.HAS_FOUNTAIN), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
        map.put(WATER_DOME_RESOURCE_KEY, new WaterDomeStructure(new Structure.StructureSettings(holderGetter.getOrThrow(WorldTags.Biomes.HAS_WATER_DOME), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));

        return map;
    }

    private static Map<Identifier, StructureSet> getStructureSets(BootstrapContext<StructureSet> context) {
        Map<Identifier, StructureSet> map = new HashMap<>();

        HolderGetter<Structure> holderGetter = context.lookup(Registries.STRUCTURE);

        map.put(SNOWBALL_KEY, new StructureSet(holderGetter.getOrThrow(SNOWBALL_RESOURCE_KEY), new RandomSpreadStructurePlacement(36, 21, RandomSpreadType.LINEAR, 737462782)));
        map.put(PYRAMID_KEY, new StructureSet(holderGetter.getOrThrow(PYRAMID_RESOURCE_KEY), new RandomSpreadStructurePlacement(36, 10, RandomSpreadType.LINEAR, 827612344)));
        map.put(FOUNTAIN_KEY, new StructureSet(holderGetter.getOrThrow(FOUNTAIN_RESOURCE_KEY), new RandomSpreadStructurePlacement(32, 10, RandomSpreadType.LINEAR, 983497234)));
        map.put(WATER_DOME_KEY, new StructureSet(holderGetter.getOrThrow(WATER_DOME_RESOURCE_KEY), new RandomSpreadStructurePlacement(32, 14, RandomSpreadType.LINEAR, 432432568)));

        return map;
    }

    private static Map<Identifier, ConfiguredFeature<?, ?>> getConfiguredFeatures() {
        Map<Identifier, ConfiguredFeature<?, ?>> map = new HashMap<>();

        map.put(RUIN_KEY, new ConfiguredFeature<>(WorldFeatures.RUIN_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
        map.put(SPIRE_KEY, new ConfiguredFeature<>(WorldFeatures.SPIRE_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
        map.put(RANDOMITE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(WorldTargets.ORE_RANDOMITE_TARGET_LIST, 8)));
        // Feature.RANDOM_PATCH and RandomPatchConfiguration are gone: the "try N times around this
        // spot" behaviour is expressed with placement modifiers now (see the placed feature below),
        // so the configured feature is just the block column that used to be wrapped by the patch.
        map.put(GUNPOWDER_REED_KEY, new ConfiguredFeature<>(Feature.BLOCK_COLUMN, BlockColumnConfiguration.simple(BiasedToBottomInt.of(2, 4), BlockStateProvider.simple(WorldBlocks.GUNPOWDER_REED.get()))));

        map.put(FLOATING_ISLAND_KEY, new ConfiguredFeature<>(WorldFeatures.FLOATING_ISLAND_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
        map.put(DESERT_WELL_KEY, new ConfiguredFeature<>(WorldFeatures.DESERT_WELL_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
        map.put(CROP_FIELD_KEY, new ConfiguredFeature<>(WorldFeatures.CROP_FIELD_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
        map.put(CACTUS_FIELD_KEY, new ConfiguredFeature<>(WorldFeatures.CACTUS_FIELD_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
        map.put(SAND_PILLAR_KEY, new ConfiguredFeature<>(WorldFeatures.SAND_PILLAR_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
        map.put(SAND_PIT_KEY, new ConfiguredFeature<>(WorldFeatures.SAND_PIT_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));

        // Saplings and stumps pick their wood from the biome they land in, which a state provider
        // cannot do, so each is its own feature; the placement below is what makes it a patch.
        map.put(SAPLING_KEY, new ConfiguredFeature<>(WorldFeatures.SAPLING_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
        map.put(TREE_STUMP_KEY, new ConfiguredFeature<>(WorldFeatures.TREE_STUMP_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));
        map.put(MELON_KEY, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.MELON))));

        return map;
    }

    private static Map<Identifier, PlacedFeature> getPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        Map<Identifier, PlacedFeature> map = new HashMap<>();

        HolderGetter<ConfiguredFeature<?, ?>> holderGetter = context.lookup(Registries.CONFIGURED_FEATURE);

        map.put(RUIN_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(RUIN_KEY)), heightmapPlacement(350)));
        map.put(SPIRE_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(SPIRE_KEY)), heightmapPlacement(350)));
        map.put(RANDOMITE_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(RANDOMITE_KEY)), commonOrePlacement(12, HeightRangePlacement.triangle(VerticalAnchor.BOTTOM, VerticalAnchor.TOP))));
        map.put(GUNPOWDER_REED_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(GUNPOWDER_REED_KEY)), reedPatchPlacement(8)));

        map.put(FLOATING_ISLAND_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(FLOATING_ISLAND_KEY)), surfacePlacement(WorldPlacements.Parts.FLOATING_ISLAND)));
        map.put(DESERT_WELL_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(DESERT_WELL_KEY)), surfacePlacement(WorldPlacements.Parts.DESERT_WELL)));
        map.put(CROP_FIELD_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(CROP_FIELD_KEY)), surfacePlacement(WorldPlacements.Parts.CROP_FIELD)));
        map.put(CACTUS_FIELD_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(CACTUS_FIELD_KEY)), surfacePlacement(WorldPlacements.Parts.CACTUS_FIELD)));
        map.put(SAND_PILLAR_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(SAND_PILLAR_KEY)), surfacePlacement(WorldPlacements.Parts.SANDSTONE_PILLAR)));
        map.put(SAND_PIT_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(SAND_PIT_KEY)), surfacePlacement(WorldPlacements.Parts.SAND_PIT)));

        map.put(SAPLING_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(SAPLING_KEY)), scatteredOnGrass(WorldPlacements.Parts.SAPLING, 16)));
        map.put(TREE_STUMP_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(TREE_STUMP_KEY)), scatteredOnGrass(WorldPlacements.Parts.TREE_STUMP, 12)));
        map.put(MELON_KEY, new PlacedFeature(holderGetter.getOrThrow(configuredFeatureResourceKey(MELON_KEY)), pumpkinStylePatch(WorldPlacements.Parts.MELON, 24)));

        return map;
    }

    @Override
    public void addEntries(RegistrySetBuilder builder) {
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

    /**
     * One attempt on the surface of a chunk the config's rarity picked out. The feature itself
     * decides whether the spot will do, since each of these shapes its own footprint.
     */
    private static List<PlacementModifier> surfacePlacement(String part) {
        return List.of(ConfigRarityFilter.onAverageOnceEvery(part), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
    }

    /**
     * A scatter of single blocks over the forest floor. The offset comes before the heightmap so
     * every attempt gets its own ground height, which is what lets a patch follow a slope.
     */
    private static List<PlacementModifier> scatteredOnGrass(String part, int count) {
        return List.of(ConfigRarityFilter.onAverageOnceEvery(part), InSquarePlacement.spread(), CountPlacement.of(count), RandomOffsetPlacement.ofTriangle(6, 0), PlacementUtils.HEIGHTMAP_NO_LEAVES,
                BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.replaceable(), BlockPredicate.matchesFluids(Fluids.EMPTY),
                        BlockPredicate.matchesTag(new BlockPos(0, -1, 0), BlockTags.SUPPORTS_VEGETATION))), BiomeFilter.biome());
    }

    /**
     * Vanilla's pumpkin patch shape with the config's rarity in place of the baked in one: tries
     * spread over a wide trapezoid, so the melons come up in a loose clump rather than an even
     * dusting. The count is well under vanilla's 96 - a patch of melons is worth more than a patch
     * of pumpkins, so a smaller one goes further.
     */
    private static List<PlacementModifier> pumpkinStylePatch(String part, int count) {
        return List.of(ConfigRarityFilter.onAverageOnceEvery(part), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome(), CountPlacement.of(count),
                RandomOffsetPlacement.ofTriangle(7, 3),
                BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesBlocks(new BlockPos(0, -1, 0), Blocks.GRASS_BLOCK))));
    }

    private static List<PlacementModifier> heightmapPlacement(int rarity) {
        return List.of(RarityFilter.onAverageOnceEvery(rarity), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
    }

    /**
     * Twenty attempts over a 4 block horizontal radius, keeping only spots that can hold a reed
     * beside water, like vanilla's sugar cane patch.
     */
    private static List<PlacementModifier> reedPatchPlacement(int rarity) {
        return List.of(RarityFilter.onAverageOnceEvery(rarity), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome(), CountPlacement.of(20), RandomOffsetPlacement.ofTriangle(4, 0),
                BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.wouldSurvive(WorldBlocks.GUNPOWDER_REED.get().defaultBlockState(), BlockPos.ZERO),
                        BlockPredicate.anyOf(BlockPredicate.matchesFluids(new BlockPos(1, -1, 0), Fluids.WATER, Fluids.FLOWING_WATER), BlockPredicate.matchesFluids(new BlockPos(-1, -1, 0), Fluids.WATER, Fluids.FLOWING_WATER), BlockPredicate.matchesFluids(new BlockPos(0, -1, 1), Fluids.WATER, Fluids.FLOWING_WATER),
                                BlockPredicate.matchesFluids(new BlockPos(0, -1, -1), Fluids.WATER, Fluids.FLOWING_WATER)))));
    }
}
