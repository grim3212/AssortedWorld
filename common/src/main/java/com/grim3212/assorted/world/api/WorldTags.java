package com.grim3212.assorted.world.api;

import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.world.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class WorldTags {

    public static class Blocks {
        public static final TagKey<Block> ORES_RANDOMITE = commonTag("ores/randomite");
        public static final TagKey<Block> RUNES = worldTag("runes");


        private static TagKey<Block> worldTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }

        private static TagKey<Block> commonTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ORES_RANDOMITE = commonTag("ores/randomite");
        public static final TagKey<Item> RUNES = worldTag("runes");

        private static TagKey<Item> worldTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }

        private static TagKey<Item> commonTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, name));
        }
    }

    public static class Biomes {

        public static final TagKey<Biome> SUPPORTS_RUIN_GENERATION = create("supports_ruin_generation");
        public static final TagKey<Biome> HAS_FOUNTAIN = create("has_structure/fountain");
        public static final TagKey<Biome> HAS_PYRAMID = create("has_structure/pyramid");
        public static final TagKey<Biome> HAS_SNOWBALL = create("has_structure/snowball");
        public static final TagKey<Biome> HAS_WATER_DOME = create("has_structure/water_dome");

        // has_feature/* for the placed features, next to has_structure/* for the structures.
        public static final TagKey<Biome> HAS_FLOATING_ISLAND = create("has_feature/floating_island");
        public static final TagKey<Biome> HAS_DESERT_WELL = create("has_feature/desert_well");
        public static final TagKey<Biome> HAS_WHEAT_FIELD = create("has_feature/wheat_field");
        public static final TagKey<Biome> HAS_SAPLINGS = create("has_feature/saplings");
        public static final TagKey<Biome> HAS_TREE_STUMPS = create("has_feature/tree_stumps");
        public static final TagKey<Biome> HAS_CACTUS_FIELD = create("has_feature/cactus_field");
        public static final TagKey<Biome> HAS_SAND_PILLAR = create("has_feature/sand_pillar");
        public static final TagKey<Biome> HAS_SAND_PIT = create("has_feature/sand_pit");
        public static final TagKey<Biome> HAS_MELONS = create("has_feature/melons");

        private static TagKey<Biome> create(String n) {
            return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(Constants.MOD_ID, n));
        }
    }
}
