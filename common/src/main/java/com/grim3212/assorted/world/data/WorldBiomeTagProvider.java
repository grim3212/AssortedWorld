package com.grim3212.assorted.world.data;

import com.grim3212.assorted.lib.data.LibBiomeTagProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.world.api.WorldTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
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
        // addOptionalTag takes the TagKey itself now rather than its id.
        tagger.apply(WorldTags.Biomes.SUPPORTS_RUIN_GENERATION).addOptionalTag(LibCommonTags.Biomes.IS_DRY_OVERWORLD).addOptionalTag(LibCommonTags.Biomes.IS_SPARSE_OVERWORLD).addOptionalTag(LibCommonTags.Biomes.IS_PLAINS).addOptionalTag(LibCommonTags.Biomes.IS_DENSE_OVERWORLD).addOptionalTag(LibCommonTags.Biomes.IS_CONIFEROUS);
        tagger.apply(WorldTags.Biomes.HAS_FOUNTAIN).addOptionalTag(LibCommonTags.Biomes.IS_SWAMP);
        tagger.apply(WorldTags.Biomes.HAS_PYRAMID).addOptionalTag(BiomeTags.HAS_DESERT_PYRAMID);
        tagger.apply(WorldTags.Biomes.HAS_SNOWBALL).addOptionalTag(LibCommonTags.Biomes.IS_SNOWY);
        tagger.apply(WorldTags.Biomes.HAS_WATER_DOME).addOptionalTag(BiomeTags.IS_OCEAN);
    }
}
