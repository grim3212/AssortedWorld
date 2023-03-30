package com.grim3212.assorted.world.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.world.Constants;

import java.util.function.Supplier;

public class WorldCommonConfig {

    public final Supplier<Double> runeChance;
    public final Supplier<Integer> spireRadius;
    public final Supplier<Integer> spireHeight;
    public final Supplier<Double> deathSpireChance;
    public final Supplier<Integer> waterDomePieceMod;

    public WorldCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        runeChance = builder.defineDouble("common.runeChance", 0.15D, 0, 1, "Set this to the chance that a rune will genarate inside a structure.");
        waterDomePieceMod = builder.defineInteger("structures.waterDomePieceMod", 8, 0, 100, "This value determines how many extra pieces to a water dome are added.");

        spireRadius = builder.defineInteger("spires.spireRadius", 7, 0, 100, "Set this to the radius you would like for the spires.");
        spireHeight = builder.defineInteger("spires.spireHeight", 40, 0, 100, "Set this to the height you would like for spires.");
        deathSpireChance = builder.defineDouble("spires.deathSpireChance", 0.001D, 0, 1, "Set this to the chance for a death spire to generate.");

        builder.setup();
    }
}
