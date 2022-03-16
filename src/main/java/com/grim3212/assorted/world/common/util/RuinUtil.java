package com.grim3212.assorted.world.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.grim3212.assorted.world.common.block.WorldBlocks;

import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.DungeonHooks;

/**
 * Straight from net.minecraftforge.common.DungeonHooks
 * 
 * I wanted to keep the two separate so that the extra mobs added don't change
 * regular dungeons.
 *
 */
public class RuinUtil {
	private static ArrayList<RuneMob> runeMobs = new ArrayList<RuneMob>();

	/**
	 * Adds a mob to the possible list of creatures the spawner will create. If the
	 * mob is already in the spawn list, the rarity will be added to the existing
	 * one, causing the mob to be more common.
	 *
	 * @param name   The entity type of the monster
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
				rarity = mob.getWeight().asInt() + rarity;
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
				return mob.getWeight().asInt();
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
	public static EntityType<?> getRandomRuneMob(Random rand) {
		if (rand.nextInt(3) > 0) {
			RuneMob mob = WeightedRandom.getRandomItem(rand, runeMobs).orElseThrow();
			return mob.type;
		} else {

			// We still want to be able to pull from this in case of modded mobs
			// added
			return DungeonHooks.getRandomDungeonMob(rand);
		}
	}

	public static double distanceBetweenD(int x1, int z1, int x2, int z2) {
		return Math.sqrt(Math.pow(x2 - x1, 2D) + Math.pow(z2 - z1, 2D));
	}

	public static int distanceBetween(int x1, int z1, int x2, int z2) {
		return (int) distanceBetweenD(x1, z1, x2, z2);
	}

	public static Block randomRune(Random random) {
		return WorldBlocks.runeBlocks()[random.nextInt(WorldBlocks.runeBlocks().length)];
	}

	public static class RuneMob extends WeightedEntry.IntrusiveBase {
		public final EntityType<?> type;

		public RuneMob(int weight, EntityType<?> type) {
			super(weight);
			this.type = type;
		}

		@Override
		public boolean equals(Object target) {
			return target instanceof RuneMob && type.equals(((RuneMob) target).type);
		}
	}

	static {
		addRuneMob(EntityType.SKELETON, 100);
		addRuneMob(EntityType.ZOMBIE, 200);
		addRuneMob(EntityType.SPIDER, 100);
		addRuneMob(EntityType.CAVE_SPIDER, 100);
		addRuneMob(EntityType.CREEPER, 50);
		addRuneMob(EntityType.WITCH, 25);
		addRuneMob(EntityType.SILVERFISH, 50);
	}
}