package com.grim3212.assorted.world.common.gen;

import java.util.HashMap;
import java.util.Map;

import com.grim3212.assorted.world.common.gen.feature.WorldConfiguredFeatures;
import com.grim3212.assorted.world.common.gen.structure.WorldConfiguredStructures;
import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.grim3212.assorted.world.common.handler.WorldConfig;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldGeneration {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void generateOverworld(final BiomeLoadingEvent evt) {
		Category category = evt.getCategory();
		BiomeGenerationSettingsBuilder builder = evt.getGeneration();

		if (category != Biome.Category.NETHER && category != Biome.Category.THEEND) {
			if (WorldConfig.COMMON.generateRandomite.get())
				builder.withFeature(Decoration.UNDERGROUND_ORES, WorldConfiguredFeatures.ORE_RANDOMITE);
			if (category != Biome.Category.OCEAN && category != Biome.Category.RIVER && WorldConfig.COMMON.ruinChance.get() > 0)
				builder.withFeature(Decoration.SURFACE_STRUCTURES, WorldConfiguredFeatures.RUIN);
			if (category == Biome.Category.EXTREME_HILLS && WorldConfig.COMMON.spireChance.get() > 0)
				builder.withFeature(Decoration.SURFACE_STRUCTURES, WorldConfiguredFeatures.SPIRE);
		}

		if (category == Biome.Category.DESERT && WorldConfig.COMMON.pyramidMaxChunkDistance.get() > 0)
			builder.getStructures().add(() -> WorldConfiguredStructures.CONFIGURED_PYRAMID);

		if (category == Biome.Category.SWAMP && WorldConfig.COMMON.fountainMaxChunkDistance.get() > 0)
			builder.getStructures().add(() -> WorldConfiguredStructures.CONFIGURED_FOUNTAIN);

		if (category == Biome.Category.OCEAN && WorldConfig.COMMON.waterDomeMaxChunkDistance.get() > 0)
			builder.getStructures().add(() -> WorldConfiguredStructures.CONFIGURED_WATERDOME);

		if (evt.getClimate().precipitation == Biome.RainType.SNOW && WorldConfig.COMMON.snowBallMaxChunkDistance.get() > 0)
			builder.getStructures().add(() -> WorldConfiguredStructures.CONFIGURED_SNOWBALL);
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void addDimensionalSpacing(final WorldEvent.Load event) {
		if (event.getWorld() instanceof ServerWorld) {
			ServerWorld serverWorld = (ServerWorld) event.getWorld();

			// Prevent spawning our structure in Vanilla's superflat world as
			// people seem to want their superflat worlds free of modded structures.
			// Also that vanilla superflat is really tricky and buggy to work with in my
			// experience.
			if (serverWorld.getChunkProvider().getChunkGenerator() instanceof FlatChunkGenerator && serverWorld.getDimensionKey().equals(World.OVERWORLD)) {
				return;
			}

			Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(serverWorld.getChunkProvider().generator.func_235957_b_().func_236195_a_());
			tempMap.put(WorldStructures.PYRAMID.get(), DimensionStructuresSettings.field_236191_b_.get(WorldStructures.PYRAMID.get()));
			tempMap.put(WorldStructures.FOUNTAIN.get(), DimensionStructuresSettings.field_236191_b_.get(WorldStructures.FOUNTAIN.get()));
			tempMap.put(WorldStructures.WATERDOME.get(), DimensionStructuresSettings.field_236191_b_.get(WorldStructures.WATERDOME.get()));
			tempMap.put(WorldStructures.SNOWBALL.get(), DimensionStructuresSettings.field_236191_b_.get(WorldStructures.SNOWBALL.get()));
			serverWorld.getChunkProvider().generator.func_235957_b_().field_236193_d_ = tempMap;
		}
	}
}
