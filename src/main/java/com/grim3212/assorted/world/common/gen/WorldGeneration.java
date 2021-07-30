package com.grim3212.assorted.world.common.gen;

import java.util.HashMap;
import java.util.Map;

import com.grim3212.assorted.world.common.gen.feature.WorldConfiguredFeatures;
import com.grim3212.assorted.world.common.gen.structure.WorldConfiguredStructures;
import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.grim3212.assorted.world.common.handler.WorldConfig;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldGeneration {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void generateOverworld(final BiomeLoadingEvent evt) {
		BiomeCategory category = evt.getCategory();
		BiomeGenerationSettingsBuilder builder = evt.getGeneration();

		if (category != Biome.BiomeCategory.NETHER && category != Biome.BiomeCategory.THEEND) {
			if (WorldConfig.COMMON.generateRandomite.get())
				builder.addFeature(Decoration.UNDERGROUND_ORES, WorldConfiguredFeatures.ORE_RANDOMITE);
			if (category != Biome.BiomeCategory.OCEAN && category != Biome.BiomeCategory.RIVER && WorldConfig.COMMON.ruinChance.get() > 0)
				builder.addFeature(Decoration.SURFACE_STRUCTURES, WorldConfiguredFeatures.RUIN);
			if (category == Biome.BiomeCategory.EXTREME_HILLS && WorldConfig.COMMON.spireChance.get() > 0)
				builder.addFeature(Decoration.SURFACE_STRUCTURES, WorldConfiguredFeatures.SPIRE);
		}

		if (category == Biome.BiomeCategory.DESERT && WorldConfig.COMMON.pyramidMaxChunkDistance.get() > 0)
			builder.getStructures().add(() -> WorldConfiguredStructures.CONFIGURED_PYRAMID);

		if (category == Biome.BiomeCategory.SWAMP && WorldConfig.COMMON.fountainMaxChunkDistance.get() > 0)
			builder.getStructures().add(() -> WorldConfiguredStructures.CONFIGURED_FOUNTAIN);

		if (category == Biome.BiomeCategory.OCEAN && WorldConfig.COMMON.waterDomeMaxChunkDistance.get() > 0)
			builder.getStructures().add(() -> WorldConfiguredStructures.CONFIGURED_WATERDOME);

		if (evt.getClimate().precipitation == Biome.Precipitation.SNOW && WorldConfig.COMMON.snowBallMaxChunkDistance.get() > 0)
			builder.getStructures().add(() -> WorldConfiguredStructures.CONFIGURED_SNOWBALL);
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void addDimensionalSpacing(final WorldEvent.Load event) {
		if (event.getWorld() instanceof ServerLevel) {
			ServerLevel serverWorld = (ServerLevel) event.getWorld();

			// Prevent spawning our structure in Vanilla's superflat world as
			// people seem to want their superflat worlds free of modded structures.
			// Also that vanilla superflat is really tricky and buggy to work with in my
			// experience.
			if (serverWorld.getChunkSource().getGenerator() instanceof FlatLevelSource && serverWorld.dimension().equals(Level.OVERWORLD)) {
				return;
			}

			Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(serverWorld.getChunkSource().generator.getSettings().structureConfig());
			tempMap.put(WorldStructures.PYRAMID.get(), StructureSettings.DEFAULTS.get(WorldStructures.PYRAMID.get()));
			tempMap.put(WorldStructures.FOUNTAIN.get(), StructureSettings.DEFAULTS.get(WorldStructures.FOUNTAIN.get()));
			tempMap.put(WorldStructures.WATERDOME.get(), StructureSettings.DEFAULTS.get(WorldStructures.WATERDOME.get()));
			tempMap.put(WorldStructures.SNOWBALL.get(), StructureSettings.DEFAULTS.get(WorldStructures.SNOWBALL.get()));
			serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
		}
	}
}
