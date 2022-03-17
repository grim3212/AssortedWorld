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

		public final ForgeConfigSpec.IntValue fountainSpacing;
		public final ForgeConfigSpec.IntValue fountainSeparation;

		public final ForgeConfigSpec.IntValue pyramidSpacing;
		public final ForgeConfigSpec.IntValue pyramidSeparation;

		public final ForgeConfigSpec.IntValue snowBallSpacing;
		public final ForgeConfigSpec.IntValue snowBallSeparation;

		public final ForgeConfigSpec.IntValue waterDomeSpacing;
		public final ForgeConfigSpec.IntValue waterDomeSeparation;

		public final ForgeConfigSpec.IntValue waterDomePieceMod;
		public final ForgeConfigSpec.IntValue ruinRarity;

		public final ForgeConfigSpec.IntValue spireRarity;
		public final ForgeConfigSpec.IntValue spireRadius;
		public final ForgeConfigSpec.IntValue spireHeight;
		public final ForgeConfigSpec.DoubleValue deathSpireChance;

		public final ForgeConfigSpec.DoubleValue runeChance;

		public final ForgeConfigSpec.BooleanValue generateRandomite;
		public final ForgeConfigSpec.IntValue randomiteCount;
		public final ForgeConfigSpec.IntValue randomiteSize;

		public final ForgeConfigSpec.BooleanValue generateGunpowderReeds;
		public final ForgeConfigSpec.IntValue gunpowderReedsRarity;
		public final ForgeConfigSpec.IntValue gunpowderReedsTries;
		public final ForgeConfigSpec.IntValue gunpowderReedsXZSpread;

		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("Common");
			runeChance = builder.comment("Set this to the chance that a rune will genarate inside a structure.").defineInRange("runeChance", 0.15D, 0, 1);
			builder.pop();

			builder.push("Ruins");
			ruinRarity = builder.comment("Set this to how rare ruins are to generate.").defineInRange("ruinRarity", 900, 0, 2000);
			builder.pop();

			builder.comment("To disable a structure set the spacing to `0` and set the separation to `-1`.");
			builder.push("Structures");
			fountainSpacing = builder.comment("The average distance apart in chunks between spawn attempts.").defineInRange("fountainSpacing", 32, 0, 1000);
			fountainSeparation = builder.comment("Minimum distance apart in chunks between spawn attempts. MUST BE LESS THAN `fountainSpacing` VALUE.").defineInRange("fountainSeparation", 10, -1, 1000);

			pyramidSpacing = builder.comment("The average distance apart in chunks between spawn attempts.").defineInRange("pyramidSpacing", 36, 0, 1000);
			pyramidSeparation = builder.comment("Minimum distance apart in chunks between spawn attempts. MUST BE LESS THAN `pyramidSpacing` VALUE.").defineInRange("pyramidSeparation", 10, -1, 1000);

			snowBallSpacing = builder.comment("The average distance apart in chunks between spawn attempts.").defineInRange("snowBallSpacing", 36, 0, 1000);
			snowBallSeparation = builder.comment("Minimum distance apart in chunks between spawn attempts. MUST BE LESS THAN `snowBallSpacing` VALUE.").defineInRange("snowBallSeparation", 21, -1, 1000);

			waterDomeSpacing = builder.comment("The average distance apart in chunks between spawn attempts.").defineInRange("waterDomeSpacing", 32, 0, 1000);
			waterDomeSeparation = builder.comment("Minimum distance apart in chunks between spawn attempts. MUST BE LESS THAN `waterDomeSpacing` VALUE.").defineInRange("waterDomeSeparation", 14, -1, 1000);
			waterDomePieceMod = builder.comment("This value determines how many extra pieces to a water dome are added.").defineInRange("waterDomePieceMod", 8, 0, 100);
			builder.pop();

			builder.push("Spires");
			spireRarity = builder.comment("Set this to the chance for a spire to generate.").defineInRange("spireChance", 350, 0, 2000);
			spireRadius = builder.comment("Set this to the radius you would like for the spires.").defineInRange("spireRadius", 7, 0, 100);
			spireHeight = builder.comment("Set this to the height you would like for spires.").defineInRange("spireHeight", 40, 0, 100);
			deathSpireChance = builder.comment("Set this to the chance for a death spire to generate.").defineInRange("deathSpireChance", 0.001D, 0, 1);
			builder.pop();

			builder.push("Randomite");
			generateRandomite = builder.comment("Set to true if you would like randomite to generate in your world.").define("generateRandomite", true);
			randomiteCount = builder.comment("Set this to the average count of randomite clusters per chunk.").defineInRange("randomiteCount", 12, 0, 256);
			randomiteSize = builder.comment("Set this to the average number of blocks that randomite clusters will generate with.").defineInRange("randomiteSize", 8, 0, 64);
			builder.pop();

			builder.push("Gunpowder Reeds");
			generateGunpowderReeds = builder.comment("Set to true if you would like gunpowder reeds to generate in your world. This will disable the recipes associated with Gunpowder Reeds as well.").define("generateGunpowderReeds", true);
			gunpowderReedsTries = builder.comment("The number of tries that will be used to try and generate gunpowder reeds.").defineInRange("gunpowderReedsTries", 20, 0, 1000);
			gunpowderReedsXZSpread = builder.comment("The x and z spread that will be used when trying to generate gunpowder reeds.").defineInRange("gunpowderReedsXZSpread", 4, 0, 1000);
			gunpowderReedsRarity = builder.comment("Set this to the chance for a gunpowder reeds to try and generate.").defineInRange("gunpowderReedsRarity", 8, 0, 10000);
			builder.pop();
		}
	}
}
