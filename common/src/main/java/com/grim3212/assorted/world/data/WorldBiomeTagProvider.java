package com.grim3212.assorted.world.data;

import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.world.api.WorldTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.apache.commons.lang3.NotImplementedException;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class WorldBiomeTagProvider extends TagsProvider<Biome> {

    public WorldBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.BIOME, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        throw new NotImplementedException();
    }

    @Override
    protected TagAppender<Biome> tag(TagKey<Biome> tag) {
        throw new NotImplementedException();
    }

    public void addCommonTags(Function<TagKey<Biome>, TagAppender<Biome>> tagger) {
        tagger.apply(WorldTags.Biomes.SUPPORTS_RUIN_GENERATION).addTag(LibCommonTags.Biomes.IS_DRY_OVERWORLD).addTag(LibCommonTags.Biomes.IS_SPARSE_OVERWORLD).addTag(LibCommonTags.Biomes.IS_PLAINS).addTag(LibCommonTags.Biomes.IS_DENSE_OVERWORLD).addTag(LibCommonTags.Biomes.IS_CONIFEROUS);
        tagger.apply(WorldTags.Biomes.HAS_FOUNTAIN).addTag(LibCommonTags.Biomes.IS_SWAMP);
        tagger.apply(WorldTags.Biomes.HAS_PYRAMID).addTag(BiomeTags.HAS_DESERT_PYRAMID);
        tagger.apply(WorldTags.Biomes.HAS_SNOWBALL).addTag(LibCommonTags.Biomes.IS_SNOWY);
        tagger.apply(WorldTags.Biomes.HAS_WATER_DOME).addTag(BiomeTags.IS_OCEAN);
    }
}
