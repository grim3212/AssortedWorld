package com.grim3212.assorted.world.common.gen;

import com.grim3212.assorted.world.common.block.WorldBlocks;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldGeneration {

	private static ConfiguredFeature<?, ?> ORE_RANDOMITE = null;

	private void initFeatures() {
		ORE_RANDOMITE = Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, WorldBlocks.RANDOMITE_ORE.get().getDefaultState(), 8)).range(164).square().func_242731_b(12);
	}

	@SubscribeEvent
	public void generateOverworldOres(final BiomeLoadingEvent evt) {
		if (evt.getCategory() != Biome.Category.NETHER && evt.getCategory() != Biome.Category.THEEND) {
			if (ORE_RANDOMITE == null)
				initFeatures();

			evt.getGeneration().withFeature(Decoration.UNDERGROUND_ORES, WorldGeneration.ORE_RANDOMITE);
		}
	}
}
