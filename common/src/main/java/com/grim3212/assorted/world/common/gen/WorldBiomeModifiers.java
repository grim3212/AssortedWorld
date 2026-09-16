package com.grim3212.assorted.world.common.gen;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.platform.services.IWorldGenHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.world.api.WorldTags;
import com.grim3212.assorted.world.data.WorldGenData;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;

public class WorldBiomeModifiers {

    public static void init() {
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(BiomeTags.IS_OVERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES, WorldGenData.RANDOMITE_KEY);
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(BiomeTags.IS_OVERWORLD), GenerationStep.Decoration.VEGETAL_DECORATION, WorldGenData.GUNPOWDER_REED_KEY);
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(WorldTags.Biomes.SUPPORTS_RUIN_GENERATION), GenerationStep.Decoration.SURFACE_STRUCTURES, WorldGenData.RUIN_KEY);
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(LibCommonTags.Biomes.IS_MOUNTAIN), GenerationStep.Decoration.SURFACE_STRUCTURES, WorldGenData.SPIRE_KEY);

        // Whether each of these generates at all is the config's rarity, read per attempt by
        // ConfigRarityFilter, so they are attached here unconditionally.
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(WorldTags.Biomes.HAS_FLOATING_ISLAND), GenerationStep.Decoration.SURFACE_STRUCTURES, WorldGenData.FLOATING_ISLAND_KEY);
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(WorldTags.Biomes.HAS_DESERT_WELL), GenerationStep.Decoration.SURFACE_STRUCTURES, WorldGenData.DESERT_WELL_KEY);
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(WorldTags.Biomes.HAS_SAND_PILLAR), GenerationStep.Decoration.SURFACE_STRUCTURES, WorldGenData.SAND_PILLAR_KEY);
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(WorldTags.Biomes.HAS_SAND_PIT), GenerationStep.Decoration.SURFACE_STRUCTURES, WorldGenData.SAND_PIT_KEY);

        // The planted ones go in with the vegetation, after the trees they grow among are standing.
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(WorldTags.Biomes.HAS_WHEAT_FIELD), GenerationStep.Decoration.VEGETAL_DECORATION, WorldGenData.WHEAT_FIELD_KEY);
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(WorldTags.Biomes.HAS_CACTUS_FIELD), GenerationStep.Decoration.VEGETAL_DECORATION, WorldGenData.CACTUS_FIELD_KEY);
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(WorldTags.Biomes.HAS_SAPLINGS), GenerationStep.Decoration.VEGETAL_DECORATION, WorldGenData.SAPLING_KEY);
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(WorldTags.Biomes.HAS_TREE_STUMPS), GenerationStep.Decoration.VEGETAL_DECORATION, WorldGenData.TREE_STUMP_KEY);
        Services.WORLD_GEN.addFeatureToBiomes(matchesTag(WorldTags.Biomes.HAS_MELONS), GenerationStep.Decoration.VEGETAL_DECORATION, WorldGenData.MELON_KEY);
    }

    private static IWorldGenHelper.BiomePredicate matchesTag(TagKey<Biome> tag) {
        return (resourceLocation, biome) -> biome.is(tag);
    }
}
