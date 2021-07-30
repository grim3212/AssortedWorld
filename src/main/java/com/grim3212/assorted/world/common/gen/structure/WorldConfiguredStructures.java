package com.grim3212.assorted.world.common.gen.structure;

import com.grim3212.assorted.world.AssortedWorld;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class WorldConfiguredStructures {

	public static ConfiguredStructureFeature<?, ?> CONFIGURED_PYRAMID = WorldStructures.PYRAMID.get().configured(FeatureConfiguration.NONE);
	public static ConfiguredStructureFeature<?, ?> CONFIGURED_FOUNTAIN = WorldStructures.FOUNTAIN.get().configured(FeatureConfiguration.NONE);
	public static ConfiguredStructureFeature<?, ?> CONFIGURED_WATERDOME = WorldStructures.WATERDOME.get().configured(FeatureConfiguration.NONE);
	public static ConfiguredStructureFeature<?, ?> CONFIGURED_SNOWBALL = WorldStructures.SNOWBALL.get().configured(FeatureConfiguration.NONE);

	/**
	 * Registers the configured structure which is what gets added to the biomes.
	 * Noticed we are not using a forge registry because there is none for
	 * configured structures.
	 *
	 * We can register configured structures at any time before a world is clicked
	 * on and made. But the best time to register configured features by code is
	 * honestly to do it in FMLCommonSetupEvent.
	 */
	public static void registerConfiguredStructures() {
		Registry<ConfiguredStructureFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE;
		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "configured_pyramid"), CONFIGURED_PYRAMID);
		FlatLevelGeneratorSettings.STRUCTURE_FEATURES.put(WorldStructures.PYRAMID.get(), CONFIGURED_PYRAMID);

		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "configured_fountain"), CONFIGURED_FOUNTAIN);
		FlatLevelGeneratorSettings.STRUCTURE_FEATURES.put(WorldStructures.FOUNTAIN.get(), CONFIGURED_FOUNTAIN);

		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "configured_waterdome"), CONFIGURED_WATERDOME);
		FlatLevelGeneratorSettings.STRUCTURE_FEATURES.put(WorldStructures.WATERDOME.get(), CONFIGURED_WATERDOME);

		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "configured_snowball"), CONFIGURED_SNOWBALL);
		FlatLevelGeneratorSettings.STRUCTURE_FEATURES.put(WorldStructures.SNOWBALL.get(), CONFIGURED_SNOWBALL);
	}

}
