package com.grim3212.assorted.world.common.gen.feature;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyBlock;

import java.util.List;

/**
 * Every kind of floating island, and the pick of one for a biome. A biome gets the kinds whose
 * {@code floating_island/<name>} tag holds it, so a snowy biome rolls between the cold kinds and a
 * desert between the dry ones. A biome no tag names falls back to the plain temperate kinds.
 * <p>
 * Tree ids are vanilla configured features. The weights only compete with the other kinds that
 * fit the same biome.
 */
public final class FloatingIslandTypes {

    private FloatingIslandTypes() {
    }

    // Temperate. Meadow and forest are also what an ocean, river or beach gets.

    public static final FloatingIslandType MEADOW = FloatingIslandType.builder("meadow", 30).fallback()
            .cover(Blocks.GRASS_BLOCK).filler(Blocks.DIRT).core(Blocks.STONE)
            .plantRate(3).plant(Blocks.SHORT_GRASS, 10).plant(Blocks.TALL_GRASS, 2).plant(Blocks.WILDFLOWERS, 2).plant(Blocks.POPPY, 2)
            .plant(Blocks.DANDELION, 2).plant(Blocks.CORNFLOWER, 1).plant(Blocks.OXEYE_DAISY, 1).plant(Blocks.AZURE_BLUET, 1).plant(Blocks.BUSH, 1)
            .trees(0.01D, 8).tree("oak", 3).tree("birch", 1)
            .build();

    public static final FloatingIslandType FOREST = FloatingIslandType.builder("forest", 30).fallback()
            .cover(Blocks.GRASS_BLOCK).filler(Blocks.DIRT).core(Blocks.STONE)
            .plantRate(4).plant(Blocks.SHORT_GRASS, 6).plant(Blocks.FERN, 2).plant(Blocks.LEAF_LITTER, 2).plant(Blocks.BUSH, 1).plant(Blocks.LILY_OF_THE_VALLEY, 1)
            .trees(0.04D, 16).tree("oak", 4).tree("birch", 3).tree("fancy_oak", 1).tree("fallen_oak_tree", 1).tree("fallen_birch_tree", 1)
            .build();

    public static final FloatingIslandType CHERRY = FloatingIslandType.builder("cherry", 15)
            .cover(Blocks.GRASS_BLOCK).filler(Blocks.DIRT).core(Blocks.STONE)
            .plantRate(3).plant(Blocks.PINK_PETALS, 6).plant(Blocks.SHORT_GRASS, 4)
            .trees(0.03D, 12).tree("cherry", 1)
            .build();

    public static final FloatingIslandType DARK_FOREST = FloatingIslandType.builder("dark_forest", 25)
            .cover(Blocks.GRASS_BLOCK).filler(Blocks.DIRT).core(Blocks.STONE)
            .plantRate(5).plant(Blocks.SHORT_GRASS, 3).plant(Blocks.LEAF_LITTER, 2).plant(Blocks.RED_MUSHROOM, 1).plant(Blocks.BROWN_MUSHROOM, 1)
            .trees(0.05D, 14).tree("dark_oak", 6).tree("huge_red_mushroom", 1).tree("huge_brown_mushroom", 1)
            .build();

    public static final FloatingIslandType PALE_GARDEN = FloatingIslandType.builder("pale_garden", 15)
            .cover(Blocks.PALE_MOSS_BLOCK.defaultBlockState(), 3).cover(Blocks.GRASS_BLOCK.defaultBlockState(), 1).filler(Blocks.DIRT).core(Blocks.STONE)
            .plantRate(3).plant(Blocks.PALE_MOSS_CARPET, 6).plant(Blocks.CLOSED_EYEBLOSSOM, 2)
            .trees(0.04D, 14).tree("pale_oak", 1)
            .build();

    // Cold.

    public static final FloatingIslandType SNOWY = FloatingIslandType.builder("snowy", 30)
            .cover(Blocks.GRASS_BLOCK.defaultBlockState().setValue(SnowyBlock.SNOWY, true), 1).filler(Blocks.DIRT).core(Blocks.STONE)
            .snowCover()
            .trees(0.03D, 14).tree("spruce", 3).tree("pine", 1)
            .build();

    public static final FloatingIslandType TAIGA = FloatingIslandType.builder("taiga", 30)
            .cover(Blocks.PODZOL.defaultBlockState(), 2).cover(Blocks.GRASS_BLOCK.defaultBlockState(), 1).cover(Blocks.COARSE_DIRT.defaultBlockState(), 1)
            .filler(Blocks.DIRT).core(Blocks.STONE)
            .plantRate(3).plant(Blocks.FERN, 6).plant(Blocks.SHORT_GRASS, 3).plant(Blocks.LARGE_FERN, 2).plant(Blocks.SWEET_BERRY_BUSH, 1)
            .trees(0.05D, 16).tree("spruce", 4).tree("pine", 2).tree("fallen_spruce_tree", 1)
            .build();

    public static final FloatingIslandType GLACIER = FloatingIslandType.builder("glacier", 20)
            .cover(Blocks.SNOW_BLOCK).filler(Blocks.PACKED_ICE).core(Blocks.PACKED_ICE, 4).core(Blocks.BLUE_ICE, 1)
            .snowCover()
            .build();

    public static final FloatingIslandType ROCKY = FloatingIslandType.builder("rocky", 20)
            .cover(Blocks.STONE.defaultBlockState(), 5).cover(Blocks.GRAVEL.defaultBlockState(), 2).cover(Blocks.ANDESITE.defaultBlockState(), 2)
            .cover(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 1)
            .filler(Blocks.STONE).core(Blocks.STONE, 6).core(Blocks.ANDESITE, 2).core(Blocks.TUFF, 2)
            .plantRate(8).plant(Blocks.MOSS_CARPET, 1)
            .build();

    // Dry.

    public static final FloatingIslandType DESERT = FloatingIslandType.builder("desert", 30)
            .cover(Blocks.SAND).filler(Blocks.SANDSTONE).core(Blocks.SANDSTONE)
            .plantRate(6).plant(Blocks.DEAD_BUSH, 3).plant(Blocks.SHORT_DRY_GRASS, 3).plant(Blocks.TALL_DRY_GRASS, 2).plant(Blocks.CACTUS, 2)
            .build();

    public static final FloatingIslandType SAVANNA = FloatingIslandType.builder("savanna", 30)
            .cover(Blocks.GRASS_BLOCK.defaultBlockState(), 6).cover(Blocks.COARSE_DIRT.defaultBlockState(), 2).filler(Blocks.DIRT).core(Blocks.STONE)
            .plantRate(3).plant(Blocks.SHORT_GRASS, 6).plant(Blocks.TALL_GRASS, 2).plant(Blocks.SHORT_DRY_GRASS, 1)
            .trees(0.02D, 10).tree("acacia", 1)
            .build();

    public static final FloatingIslandType BADLANDS = FloatingIslandType.builder("badlands", 30)
            .cover(Blocks.RED_SAND).filler(Blocks.TERRACOTTA)
            .coreBands(Blocks.TERRACOTTA, Blocks.DYED_TERRACOTTA.orange(), Blocks.DYED_TERRACOTTA.yellow(), Blocks.TERRACOTTA, Blocks.DYED_TERRACOTTA.white(),
                    Blocks.DYED_TERRACOTTA.brown(), Blocks.DYED_TERRACOTTA.orange(), Blocks.DYED_TERRACOTTA.red(), Blocks.DYED_TERRACOTTA.lightGray(), Blocks.DYED_TERRACOTTA.orange())
            .plantRate(8).plant(Blocks.DEAD_BUSH, 3).plant(Blocks.CACTUS, 1)
            .build();

    // Wet.

    public static final FloatingIslandType JUNGLE = FloatingIslandType.builder("jungle", 30)
            .cover(Blocks.GRASS_BLOCK).filler(Blocks.DIRT).core(Blocks.STONE)
            .plantRate(3).plant(Blocks.FERN, 4).plant(Blocks.SHORT_GRASS, 4).plant(Blocks.LARGE_FERN, 1).plant(Blocks.MELON, 1).plant(Blocks.BUSH, 1)
            .trees(0.06D, 32).tree("jungle_tree", 4).tree("jungle_bush", 4).tree("mega_jungle_tree", 1)
            .build();

    public static final FloatingIslandType SWAMP = FloatingIslandType.builder("swamp", 25)
            .cover(Blocks.GRASS_BLOCK).filler(Blocks.DIRT).core(Blocks.STONE)
            .plantRate(4).plant(Blocks.SHORT_GRASS, 5).plant(Blocks.BLUE_ORCHID, 2).plant(Blocks.BROWN_MUSHROOM, 1)
            .trees(0.03D, 10).tree("swamp_oak", 1)
            .build();

    public static final FloatingIslandType MANGROVE = FloatingIslandType.builder("mangrove", 25)
            .cover(Blocks.MUD).filler(Blocks.MUD).core(Blocks.STONE, 3).core(Blocks.PACKED_MUD, 1)
            .plantRate(5).plant(Blocks.SHORT_GRASS, 2).plant(Blocks.MOSS_CARPET, 1)
            .trees(0.04D, 20).tree("mangrove", 3).tree("tall_mangrove", 1)
            .build();

    public static final FloatingIslandType LUSH = FloatingIslandType.builder("lush", 15)
            .cover(Blocks.MOSS_BLOCK).filler(Blocks.ROOTED_DIRT).core(Blocks.STONE)
            .plantRate(3).plant(Blocks.MOSS_CARPET, 5).plant(Blocks.AZALEA, 2).plant(Blocks.SHORT_GRASS, 2).plant(Blocks.FLOWERING_AZALEA, 1)
            .trees(0.03D, 8).tree("azalea_tree", 1)
            .build();

    // Mushroom.

    public static final FloatingIslandType MUSHROOM = FloatingIslandType.builder("mushroom", 15)
            .cover(Blocks.MYCELIUM).filler(Blocks.DIRT).core(Blocks.STONE)
            .plantRate(8).plant(Blocks.RED_MUSHROOM, 1).plant(Blocks.BROWN_MUSHROOM, 1)
            .trees(0.04D, 14).tree("huge_red_mushroom", 1).tree("huge_brown_mushroom", 1)
            .build();

    public static final List<FloatingIslandType> ALL = List.of(MEADOW, FOREST, CHERRY, DARK_FOREST, PALE_GARDEN, SNOWY, TAIGA, GLACIER, ROCKY, DESERT, SAVANNA, BADLANDS,
            JUNGLE, SWAMP, MANGROVE, LUSH, MUSHROOM);

    /** The kinds that fit a biome, or the fallback kinds if none name it. */
    public static List<FloatingIslandType> fitting(Holder<Biome> biome) {
        List<FloatingIslandType> fits = ALL.stream().filter(type -> biome.is(type.biomes())).toList();
        return fits.isEmpty() ? ALL.stream().filter(FloatingIslandType::fallback).toList() : fits;
    }

    /** A weighted pick among the kinds that fit a biome. */
    public static FloatingIslandType pick(RandomSource random, Holder<Biome> biome) {
        List<FloatingIslandType> fits = fitting(biome);
        int roll = random.nextInt(fits.stream().mapToInt(FloatingIslandType::weight).sum());
        for (FloatingIslandType type : fits) {
            roll -= type.weight();
            if (roll < 0) {
                return type;
            }
        }
        return fits.getFirst();
    }
}
