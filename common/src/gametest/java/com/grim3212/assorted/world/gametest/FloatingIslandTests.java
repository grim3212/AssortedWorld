package com.grim3212.assorted.world.gametest;

import com.grim3212.assorted.world.common.gen.feature.FloatingIslandShape;
import com.grim3212.assorted.world.common.gen.feature.FloatingIslandType;
import com.grim3212.assorted.world.common.gen.feature.FloatingIslandTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Floating islands: shapes that stay where a feature may write and actually vary, and kinds that
 * suit the biome under them and only name trees that exist.
 */
final class FloatingIslandTests {

    private FloatingIslandTests() {
    }

    /** Enough islands that a shape bug shows up, few enough to stay a one tick test. */
    private static final int SAMPLES = 40;

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("floating_islands_stay_in_reach_and_repeat", FloatingIslandTests::floatingIslandsStayInReachAndRepeat);
        out.accept("floating_islands_vary_in_size_and_shape", FloatingIslandTests::floatingIslandsVaryInSizeAndShape);
        out.accept("floating_island_trees_exist", FloatingIslandTests::floatingIslandTreesExist);
        out.accept("floating_island_kinds_fit_their_biomes", FloatingIslandTests::floatingIslandKindsFitTheirBiomes);
    }

    /**
     * Every tree an island kind names is a real configured feature, and a kind with trees has soil
     * for them to stand on. Either going wrong is silent: the island just grows nothing.
     */
    private static void floatingIslandTreesExist(GameTestHelper helper) {
        Registry<ConfiguredFeature<?, ?>> features = helper.getLevel().registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE);
        List<String> missing = new ArrayList<>();

        for (FloatingIslandType type : FloatingIslandTypes.ALL) {
            for (ResourceKey<ConfiguredFeature<?, ?>> tree : type.allTrees()) {
                if (features.get(tree).isEmpty()) {
                    missing.add(type.name() + " -> " + tree.identifier());
                }
            }
            // The feature only plants on #substrate_overworld; #dirt no longer holds grass in 26.2.
            if (type.hasTrees() && type.allCovers().stream().noneMatch(cover -> cover.is(BlockTags.SUBSTRATE_OVERWORLD))) {
                missing.add(type.name() + " has trees but no cover they can grow on");
            }
        }

        helper.assertTrue(missing.isEmpty(), "island trees that do not exist: " + missing);
        helper.succeed();
    }

    /**
     * Every kind's biome tag holds something, and a cold, dry, wet or mushroom biome only rolls kinds
     * that belong there, while a biome no tag names gets the fallback kinds.
     */
    private static void floatingIslandKindsFitTheirBiomes(GameTestHelper helper) {
        HolderLookup.RegistryLookup<Biome> biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        List<String> problems = new ArrayList<>();

        for (FloatingIslandType type : FloatingIslandTypes.ALL) {
            if (biomes.get(type.biomes()).map(set -> set.size() == 0).orElse(true)) {
                problems.add(type.name() + " has an empty biome tag, so it can never generate");
            }
        }

        Set<FloatingIslandType> cold = Set.of(FloatingIslandTypes.SNOWY, FloatingIslandTypes.TAIGA, FloatingIslandTypes.GLACIER, FloatingIslandTypes.ROCKY);
        Set<FloatingIslandType> dry = Set.of(FloatingIslandTypes.DESERT, FloatingIslandTypes.SAVANNA, FloatingIslandTypes.BADLANDS);
        Set<FloatingIslandType> wet = Set.of(FloatingIslandTypes.JUNGLE, FloatingIslandTypes.SWAMP, FloatingIslandTypes.MANGROVE, FloatingIslandTypes.LUSH);

        expectOnly(problems, biomes, Biomes.SNOWY_PLAINS, cold);
        expectOnly(problems, biomes, Biomes.ICE_SPIKES, cold);
        expectOnly(problems, biomes, Biomes.TAIGA, cold);
        expectOnly(problems, biomes, Biomes.DESERT, dry);
        expectOnly(problems, biomes, Biomes.BADLANDS, dry);
        expectOnly(problems, biomes, Biomes.SAVANNA, dry);
        expectOnly(problems, biomes, Biomes.JUNGLE, wet);
        expectOnly(problems, biomes, Biomes.MANGROVE_SWAMP, wet);
        expectOnly(problems, biomes, Biomes.MUSHROOM_FIELDS, Set.of(FloatingIslandTypes.MUSHROOM));
        expectOnly(problems, biomes, Biomes.OCEAN, Set.of(FloatingIslandTypes.MEADOW, FloatingIslandTypes.FOREST));

        helper.assertTrue(problems.isEmpty(), "island kinds in the wrong biomes: " + String.join("; ", problems));
        helper.succeed();
    }

    private static void expectOnly(List<String> problems, HolderLookup.RegistryLookup<Biome> biomes, ResourceKey<Biome> biome, Set<FloatingIslandType> allowed) {
        List<FloatingIslandType> fits = FloatingIslandTypes.fitting(biomes.getOrThrow(biome));
        if (fits.isEmpty()) {
            problems.add(biome.identifier() + " gets no island kind at all");
        }
        for (FloatingIslandType type : fits) {
            if (!allowed.contains(type)) {
                problems.add(biome.identifier() + " can roll " + type.name());
            }
        }
    }

    /**
     * Every column is within the reach a feature may write to, every island is one piece, and one
     * seed always makes the same island - worldgen has to agree with itself on a reload.
     */
    private static void floatingIslandsStayInReachAndRepeat(GameTestHelper helper) {
        RandomSource seeds = RandomSource.create(4242L);
        List<String> problems = new ArrayList<>();

        for (int i = 0; i < SAMPLES; i++) {
            long seed = seeds.nextLong();
            int size = FloatingIslandShape.MIN_SIZE + (i % (FloatingIslandShape.MAX_REACH - FloatingIslandShape.MIN_SIZE + 1));
            FloatingIslandShape shape = FloatingIslandShape.roll(RandomSource.create(seed), size);

            if (shape.columns().isEmpty()) {
                problems.add("size " + size + " seed " + seed + " made no island");
                continue;
            }

            for (FloatingIslandShape.Column column : shape.columns()) {
                if (Math.abs(column.x()) > FloatingIslandShape.MAX_REACH || Math.abs(column.z()) > FloatingIslandShape.MAX_REACH) {
                    problems.add("size " + size + " seed " + seed + " reaches " + column.x() + ", " + column.z());
                    break;
                }
                if (column.height() < 1) {
                    problems.add("size " + size + " seed " + seed + " has an empty column at " + column.x() + ", " + column.z());
                    break;
                }
            }

            if (!isOnePiece(shape.columns())) {
                problems.add("size " + size + " seed " + seed + " is in more than one piece");
            }

            if (!shape.columns().equals(FloatingIslandShape.roll(RandomSource.create(seed), size).columns())) {
                problems.add("size " + size + " seed " + seed + " came out different the second time");
            }
        }

        helper.assertTrue(problems.isEmpty(), problems.size() + " island problems: " + String.join("; ", problems.subList(0, Math.min(10, problems.size()))));
        helper.succeed();
    }

    /**
     * Islands are not mirror images of themselves, the tops are not flat, and the default size range
     * gives a real spread of sizes. The old lens was a perfect mirror with a flat top.
     */
    private static void floatingIslandsVaryInSizeAndShape(GameTestHelper helper) {
        RandomSource seeds = RandomSource.create(1337L);
        List<String> problems = new ArrayList<>();
        int smallest = Integer.MAX_VALUE;
        int largest = 0;
        int hilly = 0;

        for (int i = 0; i < SAMPLES; i++) {
            RandomSource random = RandomSource.create(seeds.nextLong());
            int size = 6 + random.nextInt(14 - 6 + 1);
            FloatingIslandShape shape = FloatingIslandShape.roll(random, size);

            smallest = Math.min(smallest, shape.columns().size());
            largest = Math.max(largest, shape.columns().size());
            if (shape.highest() >= 2) {
                hilly++;
            }

            double mirror = mirrorMatch(shape.columns());
            if (mirror > 0.8D) {
                problems.add("a size " + size + " island is " + Math.round(mirror * 100) + "% a mirror image of itself");
            }
        }

        if (largest < smallest * 3) {
            problems.add("island sizes only run from " + smallest + " to " + largest + " columns");
        }
        if (hilly < SAMPLES / 2) {
            problems.add("only " + hilly + " of " + SAMPLES + " islands have any hills on top");
        }

        helper.assertTrue(problems.isEmpty(), "islands are too alike: " + String.join("; ", problems));
        helper.succeed();
    }

    private static boolean isOnePiece(List<FloatingIslandShape.Column> columns) {
        Set<Long> all = new HashSet<>();
        for (FloatingIslandShape.Column column : columns) {
            all.add(key(column.x(), column.z()));
        }

        Set<Long> reached = new HashSet<>();
        List<Long> queue = new ArrayList<>();
        long start = key(columns.getFirst().x(), columns.getFirst().z());
        reached.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            long at = queue.removeLast();
            int x = (int) (at >> 32);
            int z = (int) at;
            for (long next : new long[]{key(x + 1, z), key(x - 1, z), key(x, z + 1), key(x, z - 1)}) {
                if (all.contains(next) && reached.add(next)) {
                    queue.add(next);
                }
            }
        }

        return reached.size() == all.size();
    }

    /** Share of columns whose mirror across x = 0 has the same top and bottom. */
    private static double mirrorMatch(List<FloatingIslandShape.Column> columns) {
        Map<Long, FloatingIslandShape.Column> byPosition = new HashMap<>();
        for (FloatingIslandShape.Column column : columns) {
            byPosition.put(key(column.x(), column.z()), column);
        }

        int same = 0;
        for (FloatingIslandShape.Column column : columns) {
            FloatingIslandShape.Column mirrored = byPosition.get(key(-column.x(), column.z()));
            if (mirrored != null && mirrored.rise() == column.rise() && mirrored.depth() == column.depth()) {
                same++;
            }
        }
        return (double) same / columns.size();
    }

    private static long key(int x, int z) {
        return ((long) x << 32) ^ (z & 0xFFFFFFFFL);
    }
}
