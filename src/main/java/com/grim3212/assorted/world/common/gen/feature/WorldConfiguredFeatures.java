package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.common.block.WorldBlocks;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class WorldConfiguredFeatures {

	public static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> RUIN = FeatureUtils.register(WorldFeaturePlacements.prefix("ruin"), WorldFeatures.RUIN.get(), NoneFeatureConfiguration.INSTANCE);
	public static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> SPIRE = FeatureUtils.register(WorldFeaturePlacements.prefix("spire"), WorldFeatures.SPIRE.get(), NoneFeatureConfiguration.INSTANCE);
	public static Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_RANDOMITE = FeatureUtils.register(WorldFeaturePlacements.prefix("ore_randomite"), Feature.ORE, new OreConfiguration(OreFeatures.NATURAL_STONE, WorldBlocks.RANDOMITE_ORE.get().defaultBlockState(), 8));
}
