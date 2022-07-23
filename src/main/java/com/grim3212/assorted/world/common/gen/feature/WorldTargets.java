package com.grim3212.assorted.world.common.gen.feature;

import com.google.common.collect.ImmutableList;
import com.grim3212.assorted.world.common.block.WorldBlocks;

import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class WorldTargets {

	public static final ImmutableList<OreConfiguration.TargetBlockState> ORE_RANDOMITE_TARGET_LIST = ImmutableList.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, WorldBlocks.RANDOMITE_ORE.get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get().defaultBlockState()));

}
