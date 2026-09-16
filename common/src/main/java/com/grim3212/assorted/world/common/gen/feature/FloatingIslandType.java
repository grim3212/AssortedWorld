package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.api.WorldTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * One kind of floating island: what it is built from, what grows on it and which biomes it hangs
 * over. The biomes come from the {@code assortedworld:floating_island/<name>} biome tag, so a
 * datapack can move a kind of island to other biomes without touching code.
 * <p>
 * See {@link FloatingIslandTypes} for the kinds themselves.
 */
public final class FloatingIslandType {

    private final String name;
    private final int weight;
    private final boolean fallback;
    private final WeightedList<BlockState> cover;
    private final BlockState filler;
    private final WeightedList<BlockState> core;
    private final List<BlockState> coreBands;
    private final boolean snowCover;
    private final WeightedList<BlockState> plants;
    private final int plantRate;
    private final WeightedList<ResourceKey<ConfiguredFeature<?, ?>>> trees;
    private final double treesPerColumn;
    private final int treeHeadroom;
    private final TagKey<Biome> biomes;

    private FloatingIslandType(Builder builder) {
        this.name = builder.name;
        this.weight = builder.weight;
        this.fallback = builder.fallback;
        this.cover = builder.cover.build();
        this.filler = builder.filler;
        this.core = builder.core.build();
        this.coreBands = List.copyOf(builder.coreBands);
        this.snowCover = builder.snowCover;
        this.plants = builder.plants.build();
        this.plantRate = builder.plantRate;
        this.trees = builder.trees.build();
        this.treesPerColumn = builder.treesPerColumn;
        this.treeHeadroom = builder.treeHeadroom;
        this.biomes = WorldTags.Biomes.floatingIsland(builder.name);
    }

    public static Builder builder(String name, int weight) {
        return new Builder(name, weight);
    }

    public String name() {
        return this.name;
    }

    public int weight() {
        return this.weight;
    }

    /** Whether this kind can hang over a biome that no kind's tag names, like an ocean or river. */
    public boolean fallback() {
        return this.fallback;
    }

    /** The biomes this kind hangs over. */
    public TagKey<Biome> biomes() {
        return this.biomes;
    }

    /** The top layer, rolled per column so a cover can be patchy. */
    public BlockState cover(RandomSource random) {
        return this.cover.getRandomOrThrow(random);
    }

    /** The few layers under the cover. */
    public BlockState filler() {
        return this.filler;
    }

    /**
     * Everything below the filler. Banded kinds pick by height so the stripes run level across the
     * whole island, the way badlands terracotta does.
     */
    public BlockState core(RandomSource random, int y) {
        if (!this.coreBands.isEmpty()) {
            return this.coreBands.get(Math.floorMod(y, this.coreBands.size()));
        }
        return this.core.getRandomOrThrow(random);
    }

    /** Whether every open surface block gets a layer of snow. */
    public boolean snowCover() {
        return this.snowCover;
    }

    /** Something small that grows on the surface, or nothing this time. */
    public Optional<BlockState> plant(RandomSource random) {
        if (this.plants.isEmpty() || random.nextInt(this.plantRate) != 0) {
            return Optional.empty();
        }
        return Optional.of(this.plants.getRandomOrThrow(random));
    }

    public boolean hasTrees() {
        return !this.trees.isEmpty() && this.treesPerColumn > 0.0D;
    }

    /** A vanilla configured feature to grow: a tree, a huge mushroom, a bush or a fallen log. */
    public ResourceKey<ConfiguredFeature<?, ?>> tree(RandomSource random) {
        return this.trees.getRandomOrThrow(random);
    }

    /** How many trees an island of {@code columns} surface blocks tries to grow. */
    public int treeCount(RandomSource random, int columns) {
        double expected = columns * this.treesPerColumn;
        int whole = (int) expected;
        return whole + (random.nextDouble() < expected - whole ? 1 : 0);
    }

    /** How far above the surface this kind's tallest tree can reach, so it fits under the build limit. */
    public int treeHeadroom() {
        return this.hasTrees() ? this.treeHeadroom : 0;
    }

    /** Every block the cover can be, for checking trees have soil to stand on. */
    public List<BlockState> allCovers() {
        return this.cover.unwrap().stream().map(Weighted::value).toList();
    }

    /** Every tree feature this kind can grow, for checking they all exist. */
    public List<ResourceKey<ConfiguredFeature<?, ?>>> allTrees() {
        return this.trees.unwrap().stream().map(Weighted::value).toList();
    }

    public static final class Builder {
        private final String name;
        private final int weight;
        private boolean fallback;
        private final WeightedList.Builder<BlockState> cover = WeightedList.builder();
        private BlockState filler;
        private final WeightedList.Builder<BlockState> core = WeightedList.builder();
        private List<BlockState> coreBands = List.of();
        private boolean snowCover;
        private final WeightedList.Builder<BlockState> plants = WeightedList.builder();
        private int plantRate = 1;
        private final WeightedList.Builder<ResourceKey<ConfiguredFeature<?, ?>>> trees = WeightedList.builder();
        private double treesPerColumn;
        private int treeHeadroom;

        private Builder(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }

        public Builder fallback() {
            this.fallback = true;
            return this;
        }

        public Builder cover(BlockState state, int weight) {
            this.cover.add(state, weight);
            return this;
        }

        public Builder cover(Block block) {
            return this.cover(block.defaultBlockState(), 1);
        }

        public Builder filler(Block block) {
            this.filler = block.defaultBlockState();
            return this;
        }

        public Builder core(Block block, int weight) {
            this.core.add(block.defaultBlockState(), weight);
            return this;
        }

        public Builder core(Block block) {
            return this.core(block, 1);
        }

        /** A repeating stack of layers for the core instead of a random mix. */
        public Builder coreBands(Block... bands) {
            this.coreBands = Arrays.stream(bands).map(Block::defaultBlockState).toList();
            // Something for core() to fall back to, so the weighted list is never empty.
            this.core.add(bands[0].defaultBlockState(), 1);
            return this;
        }

        public Builder snowCover() {
            this.snowCover = true;
            return this;
        }

        /** One in {@code rate} surface blocks grows one of the plants added with {@link #plant}. */
        public Builder plantRate(int rate) {
            this.plantRate = rate;
            return this;
        }

        public Builder plant(Block block, int weight) {
            this.plants.add(block.defaultBlockState(), weight);
            return this;
        }

        /**
         * @param perColumn trees per surface block, so bigger islands get more of them
         * @param headroom  the tallest the kind's trees grow, in blocks above the surface
         */
        public Builder trees(double perColumn, int headroom) {
            this.treesPerColumn = perColumn;
            this.treeHeadroom = headroom;
            return this;
        }

        /** A vanilla configured feature by its id, like {@code "jungle_tree"}. */
        public Builder tree(String id, int weight) {
            this.trees.add(ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.withDefaultNamespace(id)), weight);
            return this;
        }

        public FloatingIslandType build() {
            if (this.filler == null) {
                throw new IllegalStateException("Floating island " + this.name + " has no filler");
            }
            return new FloatingIslandType(this);
        }
    }
}
