package com.grim3212.assorted.world.common.gen;

import com.grim3212.assorted.world.AssortedWorld;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class WorldBiomeTags {

	public static final TagKey<Biome> SUPPORTS_RUIN_GENERATION = create("supports_ruin_generation");
	public static final TagKey<Biome> HAS_FOUNTAIN = create("has_structure/fountain");
	public static final TagKey<Biome> HAS_PYRAMID = create("has_structure/pyramid");
	public static final TagKey<Biome> HAS_SNOWBALL = create("has_structure/snowball");
	public static final TagKey<Biome> HAS_WATER_DOME = create("has_structure/water_dome");

	private static TagKey<Biome> create(String n) {
		return TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(AssortedWorld.MODID, n));
	}
}
