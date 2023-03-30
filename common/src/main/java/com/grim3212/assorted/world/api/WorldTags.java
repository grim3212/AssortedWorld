package com.grim3212.assorted.world.api;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.world.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class WorldTags {

    public static class Blocks {
        public static final TagKey<Block> ORES_RANDOMITE = commonTag("ores/randomite");
        public static final TagKey<Block> RUNES = worldTag("runes");


        private static TagKey<Block> worldTag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, name));
        }

        private static TagKey<Block> commonTag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(Services.PLATFORM.getCommonTagPrefix(), name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ORES_RANDOMITE = commonTag("ores/randomite");
        public static final TagKey<Item> RUNES = worldTag("runes");

        private static TagKey<Item> worldTag(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(Constants.MOD_ID, name));
        }

        private static TagKey<Item> commonTag(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(Services.PLATFORM.getCommonTagPrefix(), name));
        }
    }

    public static class Biomes {

        public static final TagKey<Biome> SUPPORTS_RUIN_GENERATION = create("supports_ruin_generation");
        public static final TagKey<Biome> HAS_FOUNTAIN = create("has_structure/fountain");
        public static final TagKey<Biome> HAS_PYRAMID = create("has_structure/pyramid");
        public static final TagKey<Biome> HAS_SNOWBALL = create("has_structure/snowball");
        public static final TagKey<Biome> HAS_WATER_DOME = create("has_structure/water_dome");

        private static TagKey<Biome> create(String n) {
            return TagKey.create(Registries.BIOME, new ResourceLocation(Constants.MOD_ID, n));
        }
    }
}
