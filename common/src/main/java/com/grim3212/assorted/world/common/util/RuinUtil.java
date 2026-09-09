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
 * Straight from net.minecraftforge.common.DungeonHooks
 * <p>
 * I wanted to keep the two separate so that the extra mobs added don't change
 * regular dungeons.
 */
public class RuinUtil {
    private static ArrayList<RuneMob> runeMobs = new ArrayList<RuneMob>();

    /**
     * Adds a mob to the possible list of creatures the spawner will create. If the
     * mob is already in the spawn list, the rarity will be added to the existing
     * one, causing the mob to be more common.
     *
     * @param type   The entity type of the monster
     * @param rarity The rarity of selecting this mob over others. Must be greater
     *               then 0. Vanilla Minecraft has the following mobs: Spider 100
     *               Skeleton 100 Zombie 200 Meaning, Zombies are twice as common as
     *               spiders or skeletons.
     * @return The new rarity of the monster,
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
     * Will completely remove a Mob from the dungeon spawn list.
     *
     * @param name The name of the mob to remove
     * @return The rarity of the removed mob, prior to being removed.
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

    /**
     * Gets a random mob name from the list.
     *
     * @param rand World generation random number generator
     * @return The mob name
     */
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
     * A random source that depends only on where a structure piece is, never on which chunk is
     * being generated.
     * <p>
     * {@code postProcess} runs once per chunk a piece overlaps and is handed a fresh
     * {@link RandomSource} each time, so anything decided from that one differs between passes.
     * That was invisible while every pass rewrote the whole structure and the last one simply won.
     * Now that each pass writes only its own chunk, two passes that disagree leave a seam along the
     * chunk border, so every block decision has to come from here instead.
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
     * The origin a scattered-feature piece should build from: the centre of its own bounding box in
     * x and z, at the box floor in y.
     * <p>
     * This is what {@code StructureStart.placeInChunk} passes as {@code postProcess}'s {@code pos}
     * argument — but it reads the box <em>before</em> calling {@code postProcess}, and
     * {@code ScatteredFeaturePiece.updateAverageGroundHeight} <em>moves the box vertically</em> the
     * first time it runs. So that argument is the pre-move height on the first pass and the
     * post-move height on every later one. It never showed while each pass rewrote the whole
     * structure and the last one won; now that a pass writes only its own chunk, the chunks land at
     * two different heights and the structure comes out in slabs. Call this after
     * {@code updateAverageGroundHeight} instead and every pass agrees.
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