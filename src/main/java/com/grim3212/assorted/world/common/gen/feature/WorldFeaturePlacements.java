package com.grim3212.assorted.world.common.gen.feature;

import java.util.List;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.handler.WorldConfig;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class WorldFeaturePlacements {

	public static final Holder<PlacedFeature> ORE_RANDOMITE = PlacementUtils.register(prefix("ore_aluminum"), WorldConfiguredFeatures.ORE_RANDOMITE, commonOrePlacement(12, HeightRangePlacement.triangle(VerticalAnchor.BOTTOM, VerticalAnchor.TOP)));
	public static final Holder<PlacedFeature> RUIN = PlacementUtils.register(prefix("ruin"), WorldConfiguredFeatures.RUIN, heightmapPlacement(WorldConfig.COMMON.ruinTries.get(), WorldConfig.COMMON.ruinChance.get()));
	public static final Holder<PlacedFeature> SPIRE = PlacementUtils.register(prefix("spire"), WorldConfiguredFeatures.RUIN, heightmapPlacement(WorldConfig.COMMON.spireTries.get(), WorldConfig.COMMON.spireChance.get()));

	private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier modifier) {
		return List.of(p_195347_, InSquarePlacement.spread(), modifier, BiomeFilter.biome());
	}

	private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier modifier) {
		return orePlacement(CountPlacement.of(count), modifier);
	}

	private static List<PlacementModifier> heightmapPlacement(int count, int rarity) {
		return List.of(CountPlacement.of(count), InSquarePlacement.spread(), HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome(), RarityFilter.onAverageOnceEvery(rarity));
	}

	static String prefix(String s) {
		return AssortedWorld.MODID + ":" + s;
	}
}
