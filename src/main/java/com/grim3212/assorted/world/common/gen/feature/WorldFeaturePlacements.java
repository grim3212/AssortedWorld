package com.grim3212.assorted.world.common.gen.feature;

import java.util.List;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.handler.WorldConfig;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class WorldFeaturePlacements {

	public static final Holder<PlacedFeature> ORE_RANDOMITE = PlacementUtils.register(prefix("ore_randomite"), WorldConfiguredFeatures.ORE_RANDOMITE, commonOrePlacement(WorldConfig.COMMON.randomiteCount.get(), HeightRangePlacement.triangle(VerticalAnchor.BOTTOM, VerticalAnchor.TOP)));
	public static final Holder<PlacedFeature> RUIN = PlacementUtils.register(prefix("ruin"), WorldConfiguredFeatures.RUIN, heightmapPlacement(WorldConfig.COMMON.ruinRarity.get()));
	public static final Holder<PlacedFeature> SPIRE = PlacementUtils.register(prefix("spire"), WorldConfiguredFeatures.SPIRE, heightmapPlacement(WorldConfig.COMMON.spireRarity.get()));
	public static final Holder<PlacedFeature> PATCH_GUNPOWDER_REED = PlacementUtils.register(prefix("patch_gunpowder_reed"), WorldConfiguredFeatures.PATCH_GUNPOWDER_REED, heightmapPlacement(WorldConfig.COMMON.gunpowderReedsRarity.get()));

	private static List<PlacementModifier> orePlacement(PlacementModifier placement, PlacementModifier modifier) {
		return List.of(placement, InSquarePlacement.spread(), modifier, BiomeFilter.biome());
	}

	private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier modifier) {
		return orePlacement(CountPlacement.of(count), modifier);
	}

	private static List<PlacementModifier> heightmapPlacement(int rarity) {
		return List.of(RarityFilter.onAverageOnceEvery(rarity), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
	}

	static String prefix(String s) {
		return AssortedWorld.MODID + ":" + s;
	}
}
