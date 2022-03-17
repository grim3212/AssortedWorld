package com.grim3212.assorted.world.common.gen.feature;

import java.util.List;

import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.handler.WorldConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.material.Fluids;

public class WorldConfiguredFeatures {

	public static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> RUIN = FeatureUtils.register(WorldFeaturePlacements.prefix("ruin"), WorldFeatures.RUIN.get(), NoneFeatureConfiguration.INSTANCE);
	public static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> SPIRE = FeatureUtils.register(WorldFeaturePlacements.prefix("spire"), WorldFeatures.SPIRE.get(), NoneFeatureConfiguration.INSTANCE);
	public static Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_RANDOMITE = FeatureUtils.register(WorldFeaturePlacements.prefix("ore_randomite"), Feature.ORE, new OreConfiguration(OreFeatures.NATURAL_STONE, WorldBlocks.RANDOMITE_ORE.get().defaultBlockState(), WorldConfig.COMMON.randomiteSize.get()));
	public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_GUNPOWDER_REED = FeatureUtils.register(WorldFeaturePlacements.prefix("patch_gunpowder_reed"), Feature.RANDOM_PATCH,
			new RandomPatchConfiguration(WorldConfig.COMMON.gunpowderReedsTries.get(), WorldConfig.COMMON.gunpowderReedsXZSpread.get(), 0,
					PlacementUtils.inlinePlaced(Feature.BLOCK_COLUMN, BlockColumnConfiguration.simple(BiasedToBottomInt.of(2, 4), BlockStateProvider.simple(WorldBlocks.GUNPOWDER_REED.get())), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.matchesBlock(Blocks.AIR, BlockPos.ZERO), BlockPredicate.wouldSurvive(WorldBlocks.GUNPOWDER_REED.get().defaultBlockState(), BlockPos.ZERO),
							BlockPredicate.anyOf(BlockPredicate.matchesFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(1, -1, 0)), BlockPredicate.matchesFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(-1, -1, 0)), BlockPredicate.matchesFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(0, -1, 1)), BlockPredicate.matchesFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(0, -1, -1))))))));
}
