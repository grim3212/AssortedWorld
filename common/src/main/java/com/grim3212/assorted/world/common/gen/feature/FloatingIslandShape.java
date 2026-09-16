package com.grim3212.assorted.world.common.gen.feature;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The outline, top and underside of one floating island, rolled before any block is placed.
 * <p>
 * An island is a handful of overlapping, stretched and turned lobes, so its outline is lumpy
 * rather than a circle and its deepest point is rarely under its middle. Noise then breaks up the
 * rim, lifts hills out of the top and hangs the underside unevenly, with clusters of spikes
 * dripping below the rest. GrimPack got this variety for free by lifting real terrain; this has to make it.
 * <p>
 * Kept free of blocks and registries so the shape can be checked without a world.
 */
public final class FloatingIslandShape {

    /**
     * How far any column may be from the island's centre. A feature's origin is inside the chunk
     * being decorated and it may only write into that chunk and its neighbours, so 15 is the
     * furthest that is always safe.
     */
    public static final int MAX_REACH = 15;

    public static final int MIN_SIZE = 3;

    /** How far past its lobes the rim noise can push the outline, as a share of a lobe's size. */
    private static final double RIM_NOISE = 0.3;

    /**
     * Where the drip noise rises above this, the underside hangs a spike. The noise is fine enough
     * that spikes come a few blocks across, like stalactites, rather than as single columns.
     */
    private static final double DRIP_THRESHOLD = 0.45;
    private static final double DRIP_FREQUENCY = 0.4;

    private final int size;
    private final List<Column> columns;

    private FloatingIslandShape(int size, List<Column> columns) {
        this.size = size;
        this.columns = columns;
    }

    /**
     * One column of the island. {@code rise} is how far its surface sits above the island's base
     * level and {@code depth} how far its underside hangs below it; a column always holds at least
     * its surface block.
     */
    public record Column(int x, int z, int rise, int depth) {

        public int height() {
            return this.rise + this.depth;
        }
    }

    private record Lobe(double x, double z, double radiusX, double radiusZ, double cos, double sin, double depthScale) {

        /** 1 at the lobe's centre, 0 on its edge and negative outside it. */
        double strength(double px, double pz) {
            double dx = px - this.x;
            double dz = pz - this.z;
            double u = ((dx * this.cos) + (dz * this.sin)) / this.radiusX;
            double v = ((dz * this.cos) - (dx * this.sin)) / this.radiusZ;
            return 1.0D - Math.sqrt((u * u) + (v * v));
        }
    }

    /**
     * Rolls an island reaching at most {@code size} blocks from its centre. Everything comes from
     * {@code random}, so one seed always makes the same island.
     */
    public static FloatingIslandShape roll(RandomSource random, int size) {
        size = Math.clamp(size, MIN_SIZE, MAX_REACH);

        List<Lobe> lobes = rollLobes(random, size);

        SimplexNoise rim = new SimplexNoise(RandomSource.create(random.nextLong()));
        SimplexNoise hills = new SimplexNoise(RandomSource.create(random.nextLong()));
        SimplexNoise hang = new SimplexNoise(RandomSource.create(random.nextLong()));
        SimplexNoise drips = new SimplexNoise(RandomSource.create(random.nextLong()));

        // Frequencies scale with the island, so a small one gets a couple of lumps rather than a
        // speckled edge, and a big one is not a single smooth blob.
        double lumpFrequency = 1.6D / size;
        double hillFrequency = 1.2D / size;
        double hillHeight = size / 2.8D;

        Map<Long, Column> byPosition = new HashMap<>();
        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                Lobe strongest = null;
                double strength = -Double.MAX_VALUE;
                for (Lobe lobe : lobes) {
                    double s = lobe.strength(x, z);
                    if (s > strength) {
                        strength = s;
                        strongest = lobe;
                    }
                }

                // Broad lumps and a little finer detail on top of them.
                double edge = (rim.getValue(x * lumpFrequency, z * lumpFrequency) * 0.75D)
                        + (rim.getValue((x * lumpFrequency * 2.3D) + 31.7D, (z * lumpFrequency * 2.3D) - 17.3D) * 0.25D);
                strength += edge * RIM_NOISE;
                if (strength <= 0.0D) {
                    continue;
                }
                double t = Math.min(1.0D, strength);

                // A low dome with hills on it. Both fade out towards the rim so the edge stays low.
                double hill = (hills.getValue(x * hillFrequency, z * hillFrequency) + 1.0D) / 2.0D;
                int rise = (int) Math.round(Math.pow(t, 0.7D) * hillHeight * (0.35D + (0.65D * hill)));
                if (t < 0.12D) {
                    // The outermost ring crumbles a block below the rest of the rim.
                    rise -= 1;
                }

                // A bowl, deepest under the strongest part of each lobe and uneven across it.
                double sag = (hang.getValue(x * hillFrequency, z * hillFrequency) + 1.0D) / 2.0D;
                int depth = (int) Math.round(Math.sqrt(t) * size * strongest.depthScale() * (0.7D + (0.5D * sag)));

                double drip = drips.getValue(x * DRIP_FREQUENCY, z * DRIP_FREQUENCY);
                if (t > 0.25D && drip > DRIP_THRESHOLD) {
                    depth += (int) Math.round(((drip - DRIP_THRESHOLD) / (1.0D - DRIP_THRESHOLD)) * size * 0.6D);
                }

                // Never let the column vanish entirely: a surface block with nothing under it is
                // still part of the outline.
                if (rise + depth < 1) {
                    depth = 1 - rise;
                }

                byPosition.put(key(x, z), new Column(x, z, rise, depth));
            }
        }

        return new FloatingIslandShape(size, connectedToCentre(byPosition));
    }

    /**
     * Only the columns joined to the centre, side by side. The rim noise can leave the odd column
     * cut off from the rest, and a lone block hanging beside an island looks like a mistake.
     */
    private static List<Column> connectedToCentre(Map<Long, Column> byPosition) {
        List<Column> kept = new ArrayList<>();
        if (!byPosition.containsKey(key(0, 0))) {
            return kept;
        }

        Set<Long> seen = new HashSet<>();
        Deque<Column> queue = new ArrayDeque<>();
        seen.add(key(0, 0));
        queue.add(byPosition.get(key(0, 0)));

        while (!queue.isEmpty()) {
            Column column = queue.poll();
            kept.add(column);
            for (int[] step : NEIGHBOURS) {
                long next = key(column.x() + step[0], column.z() + step[1]);
                Column neighbour = byPosition.get(next);
                if (neighbour != null && seen.add(next)) {
                    queue.add(neighbour);
                }
            }
        }

        // Back into row order, so what places the blocks walks the island the same way every time.
        kept.sort(Comparator.comparingInt(Column::x).thenComparingInt(Column::z));
        return List.copyOf(kept);
    }

    private static final int[][] NEIGHBOURS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private static long key(int x, int z) {
        return ((long) x << 32) ^ (z & 0xFFFFFFFFL);
    }

    /**
     * A main lobe on the centre and up to three more around it. Each is shrunk by the rim noise's
     * reach, so no noisy edge ever gets clipped square by the {@code size} bound.
     */
    private static List<Lobe> rollLobes(RandomSource random, int size) {
        double usable = size / (1.0D + RIM_NOISE);
        List<Lobe> lobes = new ArrayList<>();

        lobes.add(lobe(random, 0.0D, 0.0D, usable * (0.75D + (random.nextDouble() * 0.25D))));

        int extra = size >= 8 ? random.nextInt(4) : random.nextInt(2);
        for (int i = 0; i < extra; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0D;
            double distance = usable * (0.3D + (random.nextDouble() * 0.35D));
            double radius = Math.min(usable * (0.35D + (random.nextDouble() * 0.25D)), usable - distance);
            if (radius < 2.0D) {
                continue;
            }
            lobes.add(lobe(random, Math.cos(angle) * distance, Math.sin(angle) * distance, radius));
        }

        return lobes;
    }

    /**
     * A lobe stretched along one axis and turned. The stretch shrinks the short axis rather than
     * growing the long one, so a lobe never reaches past {@code radius}.
     */
    private static Lobe lobe(RandomSource random, double x, double z, double radius) {
        double squash = 0.7D + (random.nextDouble() * 0.3D);
        double turn = random.nextDouble() * Math.PI;
        double depthScale = 0.8D + (random.nextDouble() * 0.45D);
        return new Lobe(x, z, radius, radius * squash, Math.cos(turn), Math.sin(turn), depthScale);
    }

    public int size() {
        return this.size;
    }

    public List<Column> columns() {
        return this.columns;
    }

    /** How far the lowest column hangs below the base level. */
    public int deepest() {
        return this.columns.stream().mapToInt(Column::depth).max().orElse(0);
    }

    /** How far the highest column rises above the base level. */
    public int highest() {
        return this.columns.stream().mapToInt(Column::rise).max().orElse(0);
    }
}
