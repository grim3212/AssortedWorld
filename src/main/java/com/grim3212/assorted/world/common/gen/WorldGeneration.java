package com.grim3212.assorted.world.common.gen;

import com.grim3212.assorted.world.common.gen.feature.WorldFeaturePlacements;
import com.grim3212.assorted.world.common.handler.WorldConfig;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldGeneration {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void generateOverworld(final BiomeLoadingEvent evt) {
		BiomeCategory category = evt.getCategory();
		BiomeGenerationSettingsBuilder builder = evt.getGeneration();

		if (category != Biome.BiomeCategory.NETHER && category != Biome.BiomeCategory.THEEND) {
			if (WorldConfig.COMMON.generateRandomite.get())
				builder.addFeature(Decoration.UNDERGROUND_ORES, WorldFeaturePlacements.ORE_RANDOMITE);
			if (category != Biome.BiomeCategory.OCEAN && category != Biome.BiomeCategory.RIVER && WorldConfig.COMMON.ruinChance.get() > 0)
				builder.addFeature(Decoration.SURFACE_STRUCTURES, WorldFeaturePlacements.RUIN);
			if ((category == Biome.BiomeCategory.EXTREME_HILLS || category == Biome.BiomeCategory.MOUNTAIN) && WorldConfig.COMMON.spireChance.get() > 0)
				builder.addFeature(Decoration.SURFACE_STRUCTURES, WorldFeaturePlacements.SPIRE);
		}
	}
}
