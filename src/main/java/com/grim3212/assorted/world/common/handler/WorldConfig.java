package com.grim3212.assorted.world.common.handler;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public final class WorldConfig {

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static class Common {

		public final ForgeConfigSpec.IntValue fountainMaxChunkDistance;
		public final ForgeConfigSpec.IntValue pyramidMaxChunkDistance;
		public final ForgeConfigSpec.IntValue snowBallChance;
		public final ForgeConfigSpec.IntValue waterDomeMaxChunkDistance;
		public final ForgeConfigSpec.IntValue waterDomeTries;
		public final ForgeConfigSpec.IntValue ruinChance;
		public final ForgeConfigSpec.IntValue spireChance;
		public final ForgeConfigSpec.IntValue spireRadius;
		public final ForgeConfigSpec.IntValue spireHeight;
		public final ForgeConfigSpec.DoubleValue deathSpireChance;
		public final ForgeConfigSpec.IntValue ruinTries;
		public final ForgeConfigSpec.DoubleValue runeChance;

		public final ForgeConfigSpec.BooleanValue generateRandomite;

		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("Ruins");
			fountainMaxChunkDistance = builder.comment("How rare are fountains to generate. 0 to disable generating fountains.").defineInRange("fountainMaxChunkDistance", 50, 0, 1000);
			pyramidMaxChunkDistance = builder.comment("How rare are Pyramids to spawn in Desert Biomes. 0 to disable generating pyramids.").defineInRange("pyramidMaxChunkDistance", 54, 0, 1000);
			snowBallChance = builder.comment("Set this to the chance for a snowball structure to generate.").defineInRange("snowBallChance", 50, 0, 1000);
			waterDomeMaxChunkDistance = builder.comment("How rare are Water Domes to spawn in the ocean. 0 to disable generating water domes.").defineInRange("waterDomeMaxChunkDistance", 32, 0, 1000);
			waterDomeTries = builder.comment("Set this to how many tries should happen for a water dome to generate.").defineInRange("waterDomeTries", 16, 0, 36);
			ruinChance = builder.comment("Set this to the chance for a ruin to generate.").defineInRange("ruinChance", 400, 0, 2000);
			ruinTries = builder.comment("Set this to how many tries should happen for a ruin to generate.").defineInRange("ruinTries", 4, 0, 36);
			runeChance = builder.comment("Set this to the chance that a rune will genarate inside a structure.").defineInRange("runeChance", 0.15D, 0, 1);

			spireChance = builder.comment("Set this to the chance for a spire to generate.").defineInRange("spireChance", 350, 0, 2000);
			spireRadius = builder.comment("Set this to the radius you would like for the spires.").defineInRange("spireRadius", 7, 0, 100);
			spireHeight = builder.comment("Set this to the height you would like for spires.").defineInRange("spireHeight", 40, 0, 100);
			deathSpireChance = builder.comment("Set this to the chance for a death spire to generate.").defineInRange("deathSpireChance", 0.001D, 0, 1);

			generateRandomite = builder.comment("Set to true if you would like randomite to generate in your world.").define("generateRandomite", true);
			builder.pop();
		}
	}
}
