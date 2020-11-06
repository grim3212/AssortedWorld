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

		public final ForgeConfigSpec.IntValue ruinFountainChance;
		public final ForgeConfigSpec.IntValue ruinPyramidChance;
		public final ForgeConfigSpec.IntValue ruinSnowBallChance;
		public final ForgeConfigSpec.IntValue ruinWaterDomeChance;
		public final ForgeConfigSpec.IntValue ruinChance;
		public final ForgeConfigSpec.IntValue spireChance;
		public final ForgeConfigSpec.IntValue spireRadius;
		public final ForgeConfigSpec.IntValue spireHeight;
		public final ForgeConfigSpec.DoubleValue deathSpireChance;
		public final ForgeConfigSpec.IntValue ruinTries;
		public final ForgeConfigSpec.DoubleValue runeChance;
		
		public final ForgeConfigSpec.BooleanValue checkForStructures;
		
		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("Ruins");
			ruinFountainChance = builder.comment("Set this to the chance for a fountain structure to generate.").defineInRange("ruinFountainChance", 350, 0, 2000);
			ruinPyramidChance = builder.comment("Set this to the chance for a pyramid structure to generate.").defineInRange("ruinPyramidChance", 650, 0, 2000);
			ruinSnowBallChance = builder.comment("Set this to the chance for a snowball structure to generate.").defineInRange("ruinSnowBallChance", 1000, 0, 2000);
			ruinWaterDomeChance = builder.comment("Set this to the chance for a water dome structure to generate.").defineInRange("ruinWaterDomeChance", 1000, 0, 2000);
			ruinChance = builder.comment("Set this to the chance for a ruin to generate.").defineInRange("ruinChance", 350, 0, 2000);
			ruinTries = builder.comment("Set this to how many tries should happen for a ruin to generate.").defineInRange("ruinTries", 16, 0, 36);
			runeChance = builder.comment("Set this to the chance that a rune will genarate inside a structure.").defineInRange("runeChance", 0.15D, 0, 1);
			
			spireChance = builder.comment("Set this to the chance for a spire to generate.").defineInRange("spireChance", 350, 0, 2000);
			spireRadius = builder.comment("Set this to the radius you would like for the spires.").defineInRange("spireRadius", 7, 0, 100);
			spireHeight = builder.comment("Set this to the height you would like for spires.").defineInRange("spireHeight", 40, 0, 100);
			deathSpireChance = builder.comment("Set this to the chance for a death spire to generate.").defineInRange("deathSpireChance", 0.001D, 0, 1);
			
			checkForStructures = builder.comment("Set to true if the large structures should try and not overlap eachother").define("checkForStructures", true);
			builder.pop();
		}
	}

	public static final Client CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;
	static {
		final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	public static class Client {

		public Client(ForgeConfigSpec.Builder builder) {
		}

	}
}
