package com.grim3212.assorted.world.common.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Maps;
import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.gen.WorldBiomeTags;
import com.grim3212.assorted.world.common.gen.feature.WorldFeatures;
import com.grim3212.assorted.world.common.gen.feature.WorldTargets;
import com.grim3212.assorted.world.common.gen.structure.fountain.FountainStructure;
import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidStructure;
import com.grim3212.assorted.world.common.gen.structure.snowball.SnowballStructure;
import com.grim3212.assorted.world.common.gen.structure.waterdome.WaterDomeStructure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddFeaturesBiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldGenProvider {

	private static final ResourceLocation SNOWBALL_KEY = new ResourceLocation(AssortedWorld.MODID, "snowball");
	private static final ResourceLocation PYRAMID_KEY = new ResourceLocation(AssortedWorld.MODID, "pyramid");
	private static final ResourceLocation FOUNTAIN_KEY = new ResourceLocation(AssortedWorld.MODID, "fountain");
	private static final ResourceLocation WATER_DOME_KEY = new ResourceLocation(AssortedWorld.MODID, "water_dome");

	public static final ResourceKey<Structure> SNOWBALL_RESOURCE_KEY = structureResourceKey(SNOWBALL_KEY);
	public static final ResourceKey<Structure> PYRAMID_RESOURCE_KEY = structureResourceKey(PYRAMID_KEY);
	public static final ResourceKey<Structure> FOUNTAIN_RESOURCE_KEY = structureResourceKey(FOUNTAIN_KEY);
	public static final ResourceKey<Structure> WATER_DOME_RESOURCE_KEY = structureResourceKey(WATER_DOME_KEY);

	public static final ResourceLocation RANDOMITE_BIOME_MODIFIER_NAME = new ResourceLocation(AssortedWorld.MODID, "add_randomite");
	public static final ResourceLocation GUNPOWDER_REEDS_BIOME_MODIFIER_NAME = new ResourceLocation(AssortedWorld.MODID, "add_gunpowder_reeds");
	public static final ResourceLocation RUINS_BIOME_MODIFIER_NAME = new ResourceLocation(AssortedWorld.MODID, "add_ruins");
	public static final ResourceLocation SPIRES_BIOME_MODIFIER_NAME = new ResourceLocation(AssortedWorld.MODID, "add_spires");

	private static final ResourceLocation RUIN_KEY = new ResourceLocation(AssortedWorld.MODID, "ruin");
	private static final ResourceLocation SPIRE_KEY = new ResourceLocation(AssortedWorld.MODID, "spire");
	private static final ResourceLocation RANDOMITE_KEY = new ResourceLocation(AssortedWorld.MODID, "ore_randomite");
	private static final ResourceLocation GUNPOWDER_REED_KEY = new ResourceLocation(AssortedWorld.MODID, "patch_gunpowder_reed");

	private static ResourceKey<Structure> structureResourceKey(ResourceLocation key) {
		return ResourceKey.create(Registries.STRUCTURE, key);
	}

	private static ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureResourceKey(ResourceLocation key) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, key);
	}

	private static ResourceKey<PlacedFeature> placedFeatureResourceKey(ResourceLocation key) {
		return ResourceKey.create(Registries.PLACED_FEATURE, key);
	}

	public static DatapackBuiltinEntriesProvider datpackEntriesProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		RegistrySetBuilder coreBuilder = new RegistrySetBuilder();

		coreBuilder.add(Registries.STRUCTURE, context -> {
			getStructures(context).forEach((r, f) -> {
				context.register(r, f);
			});
		});

		coreBuilder.add(Registries.STRUCTURE_SET, context -> {
			getStructureSets(context).forEach((r, f) -> {
				context.register(ResourceKey.create(Registries.STRUCTURE_SET, r), f);
			});
		});

		coreBuilder.add(Registries.CONFIGURED_FEATURE, context -> {
			getConfiguredFeatures().forEach((r, f) -> {
				context.register(ResourceKey.create(Registries.CONFIGURED_FEATURE, r), f);
			});
		});

		coreBuilder.add(Registries.PLACED_FEATURE, context -> {
			getPlacedFeatures(context).forEach((r, f) -> {
				context.register(ResourceKey.create(Registries.PLACED_FEATURE, r), f);
			});
		});

		coreBuilder.add(ForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
			getBiomeModifiers(context).forEach((r, f) -> {
				context.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, r), f);
			});
		});

		return new DatapackBuiltinEntriesProvider(output, registries, coreBuilder, Set.of(AssortedWorld.MODID));
	}

	private static Map<ResourceKey<Structure>, Structure> getStructures(BootstapContext<Structure> context) {
		Map<ResourceKey<Structure>, Structure> map = new HashMap<>();

		HolderGetter<Biome> holderGetter = context.lookup(Registries.BIOME);

		map.put(SNOWBALL_RESOURCE_KEY, new SnowballStructure(new Structure.StructureSettings(holderGetter.getOrThrow(WorldBiomeTags.HAS_SNOWBALL), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
		map.put(PYRAMID_RESOURCE_KEY, new PyramidStructure(new Structure.StructureSettings(holderGetter.getOrThrow(WorldBiomeTags.HAS_PYRAMID), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
		map.put(FOUNTAIN_RESOURCE_KEY, new FountainStructure(new Structure.StructureSettings(holderGetter.getOrThrow(WorldBiomeTags.HAS_FOUNTAIN), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
		map.put(WATER_DOME_RESOURCE_KEY, new WaterDomeStructure(new Structure.StructureSettings(holderGetter.getOrThrow(WorldBiomeTags.HAS_WATER_DOME), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));

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

	private static List<PlacementModifier> orePlacement(PlacementModifier placement, PlacementModifier modifier) {
		return List.of(placement, InSquarePlacement.spread(), modifier, BiomeFilter.biome());
	}

	private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier modifier) {
		return orePlacement(CountPlacement.of(count), modifier);
	}

	private static List<PlacementModifier> heightmapPlacement(int rarity) {
		return List.of(RarityFilter.onAverageOnceEvery(rarity), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
	}

	public static Map<ResourceLocation, BiomeModifier> getBiomeModifiers(BootstapContext<BiomeModifier> context) {
		final Map<ResourceLocation, BiomeModifier> entries = Maps.newHashMap();

		HolderGetter<PlacedFeature> holdergetter = context.lookup(Registries.PLACED_FEATURE);

		final HolderSet.Named<Biome> overworldTag = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD);
		final HolderSet.Named<Biome> ruinTag = context.lookup(Registries.BIOME).getOrThrow(WorldBiomeTags.SUPPORTS_RUIN_GENERATION);
		final HolderSet.Named<Biome> spireTag = context.lookup(Registries.BIOME).getOrThrow(Tags.Biomes.IS_MOUNTAIN);

		final BiomeModifier randomite = new AddFeaturesBiomeModifier(overworldTag, HolderSet.direct(holdergetter.getOrThrow(placedFeatureResourceKey(RANDOMITE_KEY))), Decoration.UNDERGROUND_ORES);
		entries.put(RANDOMITE_BIOME_MODIFIER_NAME, randomite);
		final BiomeModifier gunpowderReeds = new AddFeaturesBiomeModifier(overworldTag, HolderSet.direct(holdergetter.getOrThrow(placedFeatureResourceKey(GUNPOWDER_REED_KEY))), Decoration.VEGETAL_DECORATION);
		entries.put(GUNPOWDER_REEDS_BIOME_MODIFIER_NAME, gunpowderReeds);
		final BiomeModifier ruins = new AddFeaturesBiomeModifier(ruinTag, HolderSet.direct(holdergetter.getOrThrow(placedFeatureResourceKey(RUIN_KEY))), Decoration.SURFACE_STRUCTURES);
		entries.put(RUINS_BIOME_MODIFIER_NAME, ruins);
		final BiomeModifier spires = new AddFeaturesBiomeModifier(spireTag, HolderSet.direct(holdergetter.getOrThrow(placedFeatureResourceKey(SPIRE_KEY))), Decoration.SURFACE_STRUCTURES);
		entries.put(SPIRES_BIOME_MODIFIER_NAME, spires);

		return entries;
	}

}
