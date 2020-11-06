package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class WorldConfiguredFeatures {

	public static ConfiguredFeature<?, ?> RUIN = WorldFeatures.RUIN.get().withConfiguration(NoFeatureConfig.field_236559_b_).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).chance(30);
	public static ConfiguredFeature<?, ?> PYRAMID = WorldFeatures.PYRAMID.get().withConfiguration(NoFeatureConfig.field_236559_b_).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).chance(30);
	public static ConfiguredFeature<?, ?> ORE_RANDOMITE = Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, WorldBlocks.RANDOMITE_ORE.get().getDefaultState(), 8)).range(164).square().func_242731_b(12);
	
	public static ConfiguredFeature<?, ?> RUIN_TEST = WorldFeatures.RUIN_TEST.get().withConfiguration(NoFeatureConfig.field_236559_b_);

	public static void registerConfiguredFeatures() {
		Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;
		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "ruin"), RUIN);
		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "pyramid"), PYRAMID);
		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "ore_randomite"), ORE_RANDOMITE);
		
		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "ruin_test"), RUIN_TEST);
	}
}
