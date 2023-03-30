package com.grim3212.assorted.world.data;

import com.grim3212.assorted.lib.data.LibBiomeTagProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.world.api.WorldTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class WorldBiomeTagProvider extends LibBiomeTagProvider {

    public WorldBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    public void addCommonTags(Function<TagKey<Biome>, TagAppender<Biome>> tagger) {
        tagger.apply(WorldTags.Biomes.SUPPORTS_RUIN_GENERATION).addOptionalTag(LibCommonTags.Biomes.IS_DRY_OVERWORLD.location()).addOptionalTag(LibCommonTags.Biomes.IS_SPARSE_OVERWORLD.location()).addOptionalTag(LibCommonTags.Biomes.IS_PLAINS.location()).addOptionalTag(LibCommonTags.Biomes.IS_DENSE_OVERWORLD.location()).addOptionalTag(LibCommonTags.Biomes.IS_CONIFEROUS.location());
        tagger.apply(WorldTags.Biomes.HAS_FOUNTAIN).addOptionalTag(LibCommonTags.Biomes.IS_SWAMP.location());
        tagger.apply(WorldTags.Biomes.HAS_PYRAMID).addOptionalTag(BiomeTags.HAS_DESERT_PYRAMID.location());
        tagger.apply(WorldTags.Biomes.HAS_SNOWBALL).addOptionalTag(LibCommonTags.Biomes.IS_SNOWY.location());
        tagger.apply(WorldTags.Biomes.HAS_WATER_DOME).addOptionalTag(BiomeTags.IS_OCEAN.location());
    }
}
