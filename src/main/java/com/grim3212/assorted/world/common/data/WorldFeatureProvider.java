package com.grim3212.assorted.world.common.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.gen.WorldBiomeTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.world.level.biome.Biome;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddFeaturesBiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldFeatureProvider {

	public static final ResourceLocation RANDOMITE_BIOME_MODIFIER_NAME = new ResourceLocation(AssortedWorld.MODID, "add_randomite");
	public static final ResourceLocation GUNPOWDER_REEDS_BIOME_MODIFIER_NAME = new ResourceLocation(AssortedWorld.MODID, "add_gunpowder_reeds");
	public static final ResourceLocation RUINS_BIOME_MODIFIER_NAME = new ResourceLocation(AssortedWorld.MODID, "add_ruins");
	public static final ResourceLocation SPIRES_BIOME_MODIFIER_NAME = new ResourceLocation(AssortedWorld.MODID, "add_spires");

	private static final ResourceLocation RUIN_KEY = new ResourceLocation(AssortedWorld.MODID, "ruin");
	private static final ResourceLocation SPIRE_KEY = new ResourceLocation(AssortedWorld.MODID, "spire");
	private static final ResourceLocation RANDOMITE_KEY = new ResourceLocation(AssortedWorld.MODID, "ore_randomite");
	private static final ResourceLocation GUNPOWDER_REED_KEY = new ResourceLocation(AssortedWorld.MODID, "patch_gunpowder_reed");

	public static final ResourceKey<ConfiguredFeature<?, ?>> RUIN_RESOURCE_KEY = configuredFeatureResourceKey(RUIN_KEY);
	public static final ResourceKey<ConfiguredFeature<?, ?>> SPIRE_RESOURCE_KEY = configuredFeatureResourceKey(SPIRE_KEY);
	public static final ResourceKey<ConfiguredFeature<?, ?>> RANDOMITE_RESOURCE_KEY = configuredFeatureResourceKey(RANDOMITE_KEY);
	public static final ResourceKey<ConfiguredFeature<?, ?>> GUNPOWDER_REED_RESOURCE_KEY = configuredFeatureResourceKey(GUNPOWDER_REED_KEY);

	public static final ResourceKey<PlacedFeature> RUIN_PLACED_RESOURCE_KEY = placedFeatureResourceKey(RUIN_KEY);
	public static final ResourceKey<PlacedFeature> SPIRE_PLACED_RESOURCE_KEY = placedFeatureResourceKey(SPIRE_KEY);
	public static final ResourceKey<PlacedFeature> RANDOMITE_PLACED_RESOURCE_KEY = placedFeatureResourceKey(RANDOMITE_KEY);
	public static final ResourceKey<PlacedFeature> GUNPOWDER_PLACED_REED_RESOURCE_KEY = placedFeatureResourceKey(GUNPOWDER_REED_KEY);

	private static ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureResourceKey(ResourceLocation key) {
		return ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, key);
	}

	private static ResourceKey<PlacedFeature> placedFeatureResourceKey(ResourceLocation key) {
		return ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, key);
	}

	public static DataProvider getConfiguredFeatures(final DataGenerator generator, final ExistingFileHelper existingFileHelper, final RegistryAccess registries, final RegistryOps<JsonElement> ops) {
		return JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, AssortedWorld.MODID, ops, Registry.CONFIGURED_FEATURE_REGISTRY, getConfiguredFeatures(registries));
	}

	private static Map<ResourceLocation, ConfiguredFeature<?, ?>> getConfiguredFeatures(RegistryAccess registries) {
		Map<ResourceLocation, ConfiguredFeature<?, ?>> map = new HashMap<>();

		@SuppressWarnings("rawtypes")
		Registry<Feature> features = registries.registryOrThrow(Registry.FEATURE_REGISTRY);

		map.put(RUIN_KEY, new ConfiguredFeature<>(features.get(RUIN_KEY), NoneFeatureConfiguration.INSTANCE));
		map.put(SPIRE_KEY, new ConfiguredFeature<>(features.get(SPIRE_KEY), NoneFeatureConfiguration.INSTANCE));
		map.put(RANDOMITE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(OreFeatures.NATURAL_STONE, WorldBlocks.RANDOMITE_ORE.get().defaultBlockState(), 8)));
		map.put(GUNPOWDER_REED_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH, new RandomPatchConfiguration(20, 4, 0, PlacementUtils.inlinePlaced(Feature.BLOCK_COLUMN, BlockColumnConfiguration.simple(BiasedToBottomInt.of(2, 4), BlockStateProvider.simple(WorldBlocks.GUNPOWDER_REED.get())), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.wouldSurvive(WorldBlocks.GUNPOWDER_REED.get().defaultBlockState(), BlockPos.ZERO),
				BlockPredicate.anyOf(BlockPredicate.matchesFluids(new BlockPos(1, -1, 0), Fluids.WATER, Fluids.FLOWING_WATER), BlockPredicate.matchesFluids(new BlockPos(-1, -1, 0), Fluids.WATER, Fluids.FLOWING_WATER), BlockPredicate.matchesFluids(new BlockPos(0, -1, 1), Fluids.WATER, Fluids.FLOWING_WATER), BlockPredicate.matchesFluids(new BlockPos(0, -1, -1), Fluids.WATER, Fluids.FLOWING_WATER))))))));

		return map;
	}

	public static DataProvider getPlacedFeatures(final DataGenerator generator, final ExistingFileHelper existingFileHelper, final RegistryAccess registries, final RegistryOps<JsonElement> ops) {
		return JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, AssortedWorld.MODID, ops, Registry.PLACED_FEATURE_REGISTRY, getPlacedFeatures(registries));
	}

	private static Map<ResourceLocation, PlacedFeature> getPlacedFeatures(RegistryAccess registries) {
		Map<ResourceLocation, PlacedFeature> map = new HashMap<>();

		Registry<ConfiguredFeature<?, ?>> configuredFeatures = registries.registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);

		map.put(RUIN_KEY, new PlacedFeature(configuredFeatures.getOrCreateHolderOrThrow(RUIN_RESOURCE_KEY), heightmapPlacement(350)));
		map.put(SPIRE_KEY, new PlacedFeature(configuredFeatures.getOrCreateHolderOrThrow(SPIRE_RESOURCE_KEY), heightmapPlacement(350)));
		map.put(RANDOMITE_KEY, new PlacedFeature(configuredFeatures.getOrCreateHolderOrThrow(RANDOMITE_RESOURCE_KEY), commonOrePlacement(12, HeightRangePlacement.triangle(VerticalAnchor.BOTTOM, VerticalAnchor.TOP))));
		map.put(GUNPOWDER_REED_KEY, new PlacedFeature(configuredFeatures.getOrCreateHolderOrThrow(GUNPOWDER_REED_RESOURCE_KEY), heightmapPlacement(8)));

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

	public static DataProvider getBiomeModifiers(final DataGenerator generator, final ExistingFileHelper existingFileHelper, final RegistryAccess registries, final RegistryOps<JsonElement> ops) {
		final Map<ResourceLocation, BiomeModifier> entries = Maps.newHashMap();

		final HolderSet.Named<Biome> overworldTag = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_OVERWORLD);
		final HolderSet.Named<Biome> ruinTag = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), WorldBiomeTags.SUPPORTS_RUIN_GENERATION);
		final HolderSet.Named<Biome> spireTag = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), Tags.Biomes.IS_MOUNTAIN);

		Registry<PlacedFeature> placedFeatures = registries.registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);

		final BiomeModifier randomite = new AddFeaturesBiomeModifier(overworldTag, HolderSet.direct(placedFeatures.getOrCreateHolderOrThrow(RANDOMITE_PLACED_RESOURCE_KEY)), Decoration.UNDERGROUND_ORES);
		entries.put(RANDOMITE_BIOME_MODIFIER_NAME, randomite);
		final BiomeModifier gunpowderReeds = new AddFeaturesBiomeModifier(overworldTag, HolderSet.direct(placedFeatures.getOrCreateHolderOrThrow(GUNPOWDER_PLACED_REED_RESOURCE_KEY)), Decoration.VEGETAL_DECORATION);
		entries.put(GUNPOWDER_REEDS_BIOME_MODIFIER_NAME, gunpowderReeds);
		final BiomeModifier ruins = new AddFeaturesBiomeModifier(ruinTag, HolderSet.direct(placedFeatures.getOrCreateHolderOrThrow(RUIN_PLACED_RESOURCE_KEY)), Decoration.SURFACE_STRUCTURES);
		entries.put(RUINS_BIOME_MODIFIER_NAME, ruins);
		final BiomeModifier spires = new AddFeaturesBiomeModifier(spireTag, HolderSet.direct(placedFeatures.getOrCreateHolderOrThrow(SPIRE_PLACED_RESOURCE_KEY)), Decoration.SURFACE_STRUCTURES);
		entries.put(SPIRES_BIOME_MODIFIER_NAME, spires);

		return JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, AssortedWorld.MODID, ops, ForgeRegistries.Keys.BIOME_MODIFIERS, entries);
	}

}
