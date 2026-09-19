package com.grim3212.assorted.world.data;

import com.grim3212.assorted.lib.data.LibBiomeTagProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.world.api.WorldTags;
import com.grim3212.assorted.world.common.gen.feature.BiomeWoods;
import com.grim3212.assorted.world.common.gen.feature.FloatingIslandType;
import com.grim3212.assorted.world.common.gen.feature.FloatingIslandTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.List;
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

        // Islands hang over any overworld surface; what they are made of is rolled per island and
        // has nothing to do with the biome underneath.
        tagger.apply(WorldTags.Biomes.HAS_FLOATING_ISLAND).addOptionalTag(BiomeTags.IS_OVERWORLD);

        tagger.apply(WorldTags.Biomes.HAS_DESERT_WELL).addOptionalTag(LibCommonTags.Biomes.IS_DESERT);
        // Red sandstone pillars stand on the badlands' red sand.
        tagger.apply(WorldTags.Biomes.HAS_SAND_PILLAR).addOptionalTag(LibCommonTags.Biomes.IS_DESERT).addOptionalTag(BiomeTags.IS_BADLANDS);
        // Desert only: c:is_sandy also holds the beach and the badlands.
        tagger.apply(WorldTags.Biomes.HAS_CACTUS_FIELD).addOptionalTag(LibCommonTags.Biomes.IS_DESERT);
        tagger.apply(WorldTags.Biomes.HAS_SAND_PIT).addOptionalTag(LibCommonTags.Biomes.IS_DESERT);

        tagger.apply(WorldTags.Biomes.HAS_CROP_FIELD).addOptionalTag(LibCommonTags.Biomes.IS_PLAINS);
        tagger.apply(WorldTags.Biomes.HAS_MELONS).addOptionalTag(LibCommonTags.Biomes.IS_PLAINS).addOptionalTag(LibCommonTags.Biomes.IS_WET_OVERWORLD);

        // Where trees already grow, so a stray sapling or stump reads as part of the wood. The two
        // c: tags only reach the dense and old growth woods, so the plain vanilla ones come in
        // alongside them - a forest is the first place anyone looks.
        for (TagKey<Biome> woodland : List.of(WorldTags.Biomes.HAS_SAPLINGS, WorldTags.Biomes.HAS_TREE_STUMPS)) {
            tagger.apply(woodland).addOptionalTag(LibCommonTags.Biomes.IS_DENSE_OVERWORLD).addOptionalTag(LibCommonTags.Biomes.IS_CONIFEROUS)
                    .addOptionalTag(BiomeTags.IS_FOREST).addOptionalTag(BiomeTags.IS_TAIGA).addOptionalTag(BiomeTags.IS_JUNGLE)
                    .add(Biomes.CHERRY_GROVE, Biomes.PALE_GARDEN, Biomes.WINDSWEPT_FOREST, Biomes.MANGROVE_SWAMP, Biomes.SWAMP);
        }

        this.addFloatingIslands(tagger);
        this.addWoods(tagger);
    }

    /**
     * Which wood a stray sapling or stump is made of, per biome. A biome in several of these rolls
     * between those woods; one in none of them gets the woods marked as a fallback.
     */
    private void addWoods(Function<TagKey<Biome>, TagAppender<Biome>> tagger) {
        wood(tagger, BiomeWoods.OAK).addOptionalTag(BiomeTags.IS_FOREST).addOptionalTag(LibCommonTags.Biomes.IS_PLAINS).addOptionalTag(LibCommonTags.Biomes.IS_SWAMP)
                .add(Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.SPARSE_JUNGLE);
        wood(tagger, BiomeWoods.BIRCH).addOptionalTag(BiomeTags.IS_FOREST).add(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST);
        wood(tagger, BiomeWoods.SPRUCE).addOptionalTag(BiomeTags.IS_TAIGA).addOptionalTag(LibCommonTags.Biomes.IS_SNOWY).add(Biomes.GROVE, Biomes.WINDSWEPT_FOREST);
        wood(tagger, BiomeWoods.JUNGLE).addOptionalTag(BiomeTags.IS_JUNGLE);
        wood(tagger, BiomeWoods.ACACIA).addOptionalTag(BiomeTags.IS_SAVANNA);
        wood(tagger, BiomeWoods.DARK_OAK).add(Biomes.DARK_FOREST);
        wood(tagger, BiomeWoods.CHERRY).add(Biomes.CHERRY_GROVE);
        wood(tagger, BiomeWoods.PALE_OAK).add(Biomes.PALE_GARDEN);
        wood(tagger, BiomeWoods.MANGROVE).add(Biomes.MANGROVE_SWAMP);
    }

    private static TagAppender<Biome> wood(Function<TagKey<Biome>, TagAppender<Biome>> tagger, BiomeWoods wood) {
        return tagger.apply(wood.biomes());
    }

    /**
     * Which kinds of floating island hang over which biomes. A biome in several of these rolls
     * between those kinds; one in none of them gets the fallback kinds.
     */
    private void addFloatingIslands(Function<TagKey<Biome>, TagAppender<Biome>> tagger) {
        island(tagger, FloatingIslandTypes.MEADOW).addOptionalTag(LibCommonTags.Biomes.IS_PLAINS).add(Biomes.MEADOW);
        island(tagger, FloatingIslandTypes.FOREST).addOptionalTag(BiomeTags.IS_FOREST).addOptionalTag(LibCommonTags.Biomes.IS_PLAINS);
        island(tagger, FloatingIslandTypes.CHERRY).add(Biomes.CHERRY_GROVE, Biomes.MEADOW);
        island(tagger, FloatingIslandTypes.DARK_FOREST).add(Biomes.DARK_FOREST);
        island(tagger, FloatingIslandTypes.PALE_GARDEN).add(Biomes.PALE_GARDEN, Biomes.DARK_FOREST);

        island(tagger, FloatingIslandTypes.SNOWY).addOptionalTag(LibCommonTags.Biomes.IS_SNOWY);
        island(tagger, FloatingIslandTypes.TAIGA).addOptionalTag(BiomeTags.IS_TAIGA).add(Biomes.WINDSWEPT_FOREST, Biomes.GROVE);
        island(tagger, FloatingIslandTypes.GLACIER).add(Biomes.ICE_SPIKES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.SNOWY_SLOPES, Biomes.FROZEN_OCEAN,
                Biomes.DEEP_FROZEN_OCEAN, Biomes.FROZEN_RIVER);
        island(tagger, FloatingIslandTypes.ROCKY).add(Biomes.STONY_PEAKS, Biomes.STONY_SHORE, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS,
                Biomes.JAGGED_PEAKS, Biomes.FROZEN_PEAKS, Biomes.SNOWY_SLOPES);

        island(tagger, FloatingIslandTypes.DESERT).addOptionalTag(LibCommonTags.Biomes.IS_DESERT);
        island(tagger, FloatingIslandTypes.SAVANNA).addOptionalTag(BiomeTags.IS_SAVANNA);
        island(tagger, FloatingIslandTypes.BADLANDS).addOptionalTag(BiomeTags.IS_BADLANDS);

        island(tagger, FloatingIslandTypes.JUNGLE).addOptionalTag(BiomeTags.IS_JUNGLE);
        island(tagger, FloatingIslandTypes.SWAMP).add(Biomes.SWAMP);
        island(tagger, FloatingIslandTypes.MANGROVE).add(Biomes.MANGROVE_SWAMP);
        island(tagger, FloatingIslandTypes.LUSH).addOptionalTag(BiomeTags.IS_JUNGLE).addOptionalTag(LibCommonTags.Biomes.IS_SWAMP);

        island(tagger, FloatingIslandTypes.MUSHROOM).addOptionalTag(LibCommonTags.Biomes.IS_MUSHROOM).add(Biomes.DARK_FOREST);
    }

    private static TagAppender<Biome> island(Function<TagKey<Biome>, TagAppender<Biome>> tagger, FloatingIslandType type) {
        return tagger.apply(type.biomes());
    }
}
