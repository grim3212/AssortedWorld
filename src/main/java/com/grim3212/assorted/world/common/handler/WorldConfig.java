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
		public final ForgeConfigSpec.IntValue waterDomePieceMod;

		public final ForgeConfigSpec.IntValue spireRadius;
		public final ForgeConfigSpec.IntValue spireHeight;
		public final ForgeConfigSpec.DoubleValue deathSpireChance;

		public final ForgeConfigSpec.DoubleValue runeChance;

		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("Common");
			runeChance = builder.comment("Set this to the chance that a rune will genarate inside a structure.").defineInRange("runeChance", 0.15D, 0, 1);
			builder.pop();

			builder.push("Structures");
			waterDomePieceMod = builder.comment("This value determines how many extra pieces to a water dome are added.").defineInRange("waterDomePieceMod", 8, 0, 100);
			builder.pop();

			builder.push("Spires");
			spireRadius = builder.comment("Set this to the radius you would like for the spires.").defineInRange("spireRadius", 7, 0, 100);
			spireHeight = builder.comment("Set this to the height you would like for spires.").defineInRange("spireHeight", 40, 0, 100);
			deathSpireChance = builder.comment("Set this to the chance for a death spire to generate.").defineInRange("deathSpireChance", 0.001D, 0, 1);
			builder.pop();
		}
	}
}
