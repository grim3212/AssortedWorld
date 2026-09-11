package com.grim3212.assorted.world.common.util;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The rune spawner's mob list, adapted from Forge's {@code DungeonHooks} and kept separate so the
 * extra mobs do not change regular dungeons.
 */
public class RuinUtil {
    private static ArrayList<RuneMob> runeMobs = new ArrayList<RuneMob>();

    /**
     * Adds a mob to the spawner list, or adds to its rarity if it is already there.
     *
     * @param type   The entity type
     * @param rarity Its weight against the others (vanilla: spider and skeleton 100, zombie 200)
     * @return The mob's new rarity
     */
    public static float addRuneMob(EntityType<?> type, int rarity) {
        if (rarity <= 0) {
            throw new IllegalArgumentException("Rarity must be greater then zero");
        }

        Iterator<RuneMob> itr = runeMobs.iterator();
        while (itr.hasNext()) {
            RuneMob mob = itr.next();
            if (type == mob.type) {
                itr.remove();
                rarity = mob.weight() + rarity;
                break;
            }
        }

        runeMobs.add(new RuneMob(rarity, type));
        return rarity;
    }

    /**
     * Removes a mob from the spawner list.
     *
     * @param name The mob to remove
     * @return Its rarity before removal
     */
    public static int removeRuneMob(EntityType<?> name) {
        for (RuneMob mob : runeMobs) {
            if (name.equals(mob.type)) {
                runeMobs.remove(mob);
                return mob.weight();
            }
        }
        return 0;
    }

    /** A random mob from the list, weighted by rarity. */
    public static EntityType<?> getRandomRuneMob(RandomSource rand) {
        if (rand.nextInt(3) > 0) {
            RuneMob mob = WeightedRandom.getRandomItem(rand, runeMobs, RuneMob::weight).orElseThrow();
            return mob.type;
        } else {

            // We still want to be able to pull from this in case of modded mobs
            // added
            return Services.PLATFORM.getRandomDungeonEntity(rand);
        }
    }

    public static double distanceBetweenD(int x1, int z1, int x2, int z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2D) + Math.pow(z2 - z1, 2D));
    }

    public static int distanceBetween(int x1, int z1, int x2, int z2) {
        return (int) distanceBetweenD(x1, z1, x2, z2);
    }

    public static Block randomRune(RandomSource random) {
        return WorldBlocks.runeBlocks()[random.nextInt(WorldBlocks.runeBlocks().length)];
    }

    /**
     * Structure pieces pick a rune index once at construction and serialize it. postProcess runs
     * again for every chunk the piece overlaps with a fresh RandomSource, so drawing the rune there
     * would place a different block on each pass.
     */
    public static int randomRuneIndex(RandomSource random) {
        return random.nextInt(WorldBlocks.runeBlocks().length);
    }

    /**
     * A random source that depends only on where a piece is. {@code postProcess} runs once per
     * chunk with a fresh random each time, so a block decision taken from that one leaves a seam at
     * the chunk border.
     */
    public static RandomSource pieceRandom(WorldGenLevel reader, BoundingBox boundingBox) {
        // setLargeFeatureWithSalt is vanilla's own "same answer for this spot in this world" seeding
        // and is not deprecated, unlike Mth.getSeed. minY rides along as the salt so two pieces
        // stacked over one another do not share a stream.
        WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
        random.setLargeFeatureWithSalt(reader.getSeed(), boundingBox.minX(), boundingBox.minZ(), boundingBox.minY());
        return random;
    }

    /**
     * The origin a scattered-feature piece builds from: the centre of its box in x and z, at the
     * box floor. Call it after {@code updateAverageGroundHeight}, which moves the box on the first
     * pass, so every chunk pass agrees on the height.
     */
    public static BlockPos pieceOrigin(BoundingBox boundingBox) {
        return new BlockPos(boundingBox.getCenter().getX(), boundingBox.minY(), boundingBox.getCenter().getZ());
    }

    public static Block runeAt(int index) {
        Block[] runes = WorldBlocks.runeBlocks();
        return runes[Math.floorMod(index, runes.length)];
    }

    /**
     * WeightedEntry and its IntrusiveBase are gone; a weight is a plain field on the entry and the
     * weight is read back through a ToIntFunction handed to {@link WeightedRandom}.
     */
    public static class RuneMob {
        public final EntityType<?> type;
        private final int weight;

        public RuneMob(int weight, EntityType<?> type) {
            this.weight = weight;
            this.type = type;
        }

        public int weight() {
            return this.weight;
        }

        @Override
        public boolean equals(Object target) {
            return target instanceof RuneMob && type.equals(((RuneMob) target).type);
        }

        @Override
        public int hashCode() {
            return this.type.hashCode();
        }
    }

    static {
        addRuneMob(EntityTypes.SKELETON, 100);
        addRuneMob(EntityTypes.ZOMBIE, 200);
        addRuneMob(EntityTypes.SPIDER, 100);
        addRuneMob(EntityTypes.CAVE_SPIDER, 100);
        addRuneMob(EntityTypes.CREEPER, 50);
        addRuneMob(EntityTypes.WITCH, 25);
        addRuneMob(EntityTypes.SILVERFISH, 50);
    }
}