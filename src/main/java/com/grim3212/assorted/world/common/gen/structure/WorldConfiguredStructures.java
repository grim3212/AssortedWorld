package com.grim3212.assorted.world.common.gen.structure;

import com.grim3212.assorted.world.AssortedWorld;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class WorldConfiguredStructures {

	public static StructureFeature<?, ?> CONFIGURED_PYRAMID = WorldStructures.PYRAMID.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG);
	public static StructureFeature<?, ?> CONFIGURED_FOUNTAIN = WorldStructures.FOUNTAIN.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG);
	public static StructureFeature<?, ?> CONFIGURED_WATERDOME = WorldStructures.WATERDOME.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG);

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
		Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "configured_pyramid"), CONFIGURED_PYRAMID);
		FlatGenerationSettings.STRUCTURES.put(WorldStructures.PYRAMID.get(), CONFIGURED_PYRAMID);

		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "configured_fountain"), CONFIGURED_FOUNTAIN);
		FlatGenerationSettings.STRUCTURES.put(WorldStructures.FOUNTAIN.get(), CONFIGURED_FOUNTAIN);

		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "configured_waterdome"), CONFIGURED_WATERDOME);
		FlatGenerationSettings.STRUCTURES.put(WorldStructures.WATERDOME.get(), CONFIGURED_WATERDOME);
	}

}
