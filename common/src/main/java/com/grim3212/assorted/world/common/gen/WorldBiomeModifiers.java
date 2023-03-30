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
    }

    private static IWorldGenHelper.BiomePredicate matchesTag(TagKey<Biome> tag) {
        return (resourceLocation, biome) -> biome.is(tag);
    }
}
