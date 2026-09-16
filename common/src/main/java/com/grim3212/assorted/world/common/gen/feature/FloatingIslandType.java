package com.grim3212.assorted.world.common.gen.feature;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * What a floating island is made of. Each island rolls one of these on its own rather than copying
 * the ground under it, so a desert island can be hanging over a forest.
 * <p>
 * Weights are out of {@link #TOTAL_WEIGHT}: grass is the everyday island and the rest are the ones
 * worth flying out to.
 */
public enum FloatingIslandType {
    GRASS(30, Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.STONE, List.of(Blocks.SHORT_GRASS, Blocks.SHORT_GRASS, Blocks.SHORT_GRASS, Blocks.POPPY, Blocks.DANDELION, Blocks.OAK_SAPLING)),
    DESERT(20, Blocks.SAND, Blocks.SANDSTONE, Blocks.SANDSTONE, List.of(Blocks.DEAD_BUSH, Blocks.CACTUS)),
    SNOWY(15, Blocks.SNOW_BLOCK, Blocks.PACKED_ICE, Blocks.STONE, List.of(Blocks.SNOW, Blocks.SPRUCE_SAPLING)),
    BADLANDS(10, Blocks.RED_SAND, Blocks.TERRACOTTA, Blocks.TERRACOTTA, List.of(Blocks.DEAD_BUSH)),
    MUSHROOM(5, Blocks.MYCELIUM, Blocks.DIRT, Blocks.STONE, List.of(Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM)),
    LUSH(5, Blocks.MOSS_BLOCK, Blocks.PODZOL, Blocks.STONE, List.of(Blocks.SHORT_GRASS, Blocks.MOSS_CARPET, Blocks.AZALEA));

    private static final List<FloatingIslandType> VALUES = List.of(values());

    private static final int TOTAL_WEIGHT = VALUES.stream().mapToInt(type -> type.weight).sum();

    private final int weight;
    private final Block cover;
    private final Block filler;
    private final Block core;
    private final List<Block> decorations;

    FloatingIslandType(int weight, Block cover, Block filler, Block core, List<Block> decorations) {
        this.weight = weight;
        this.cover = cover;
        this.filler = filler;
        this.core = core;
        this.decorations = decorations;
    }

    /** The island's top layer. */
    public BlockState cover() {
        return this.cover.defaultBlockState();
    }

    /** The few layers under the cover. */
    public BlockState filler() {
        return this.filler.defaultBlockState();
    }

    /** Everything below the filler, down to the island's point. */
    public BlockState core() {
        return this.core.defaultBlockState();
    }

    /** One thing that grows on this island, or nothing where the list is empty. */
    public BlockState decoration(RandomSource random) {
        return this.decorations.isEmpty() ? null : this.decorations.get(random.nextInt(this.decorations.size())).defaultBlockState();
    }

    public static FloatingIslandType random(RandomSource random) {
        int roll = random.nextInt(TOTAL_WEIGHT);

        for (FloatingIslandType type : VALUES) {
            roll -= type.weight;
            if (roll < 0) {
                return type;
            }
        }

        return GRASS;
    }
}
