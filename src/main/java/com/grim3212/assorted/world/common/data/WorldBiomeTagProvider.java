package com.grim3212.assorted.world.common.data;

import java.util.concurrent.CompletableFuture;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.gen.WorldBiomeTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class WorldBiomeTagProvider extends BiomeTagsProvider {

	public WorldBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, AssortedWorld.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		this.tag(WorldBiomeTags.SUPPORTS_RUIN_GENERATION).addTag(Tags.Biomes.IS_DRY_OVERWORLD).addTag(Tags.Biomes.IS_SPARSE_OVERWORLD).addTag(Tags.Biomes.IS_PLAINS).addTag(Tags.Biomes.IS_DENSE_OVERWORLD).addTag(Tags.Biomes.IS_CONIFEROUS);
		this.tag(WorldBiomeTags.HAS_FOUNTAIN).addTag(Tags.Biomes.IS_SWAMP);
		this.tag(WorldBiomeTags.HAS_PYRAMID).addTag(BiomeTags.HAS_DESERT_PYRAMID);
		this.tag(WorldBiomeTags.HAS_SNOWBALL).addTag(Tags.Biomes.IS_SNOWY);
		this.tag(WorldBiomeTags.HAS_WATER_DOME).addTag(BiomeTags.IS_OCEAN);
	}

	@Override
	public String getName() {
		return "Assorted World biome tags";
	}
}
