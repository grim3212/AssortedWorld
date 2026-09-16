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
    public final Supplier<Double> waterDomeChestChance;

    public final Supplier<Boolean> glowstoneSeedsEnabled;
    public final Supplier<Integer> glowstoneSeedPlantHeight;

    public final Supplier<Integer> floatingIslandRarity;
    public final Supplier<Integer> floatingIslandSizeVariance;

    public final Supplier<Integer> desertWellRarity;

    public final Supplier<Integer> wheatFieldRarity;
    public final Supplier<Integer> wheatFieldSize;
    public final Supplier<Integer> saplingRarity;
    public final Supplier<Integer> treeStumpRarity;
    public final Supplier<Integer> cactusFieldRarity;
    public final Supplier<Integer> cactusFieldSize;
    public final Supplier<Integer> sandstonePillarRarity;
    public final Supplier<Integer> sandPitRarity;
    public final Supplier<Integer> sandPitSize;
    public final Supplier<Integer> melonRarity;

    /** Every rarity is read through {@code ConfigRarityFilter}, where 0 turns the feature off. */
    private static final String RARITY = "One in this many chunks gets one. Larger is rarer, and 0 turns them off entirely.";

    public WorldCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        runeChance = builder.defineDouble("common.runeChance", 0.15D, 0, 1, "Set this to the chance that a rune will generate inside a ruin. The four structures always generate exactly one rune.");
        waterDomePieceMod = builder.defineInteger("structures.waterDomePieceMod", 8, 0, 100, "This value determines how many extra pieces to a water dome are added.");
        waterDomeChestChance = builder.defineDouble("structures.waterDomeChestChance", 0.6D, 0, 1, "Set this to the chance that a water dome generates loot chests. A dome that does gets 1 or 2 of them, filled according to the material the dome is ribbed with.");

        spireRadius = builder.defineInteger("spires.spireRadius", 7, 0, 100, "Set this to the radius you would like for the spires.");
        spireHeight = builder.defineInteger("spires.spireHeight", 40, 0, 100, "Set this to the height you would like for spires.");
        deathSpireChance = builder.defineDouble("spires.deathSpireChance", 0.001D, 0, 1, "Set this to the chance for a death spire to generate.");

        glowstoneSeedsEnabled = builder.defineBoolean("parts.glowstoneSeedsEnabled", true, "Set this to true if you would like glowstone seeds to be craftable and found in the creative tab.");
        glowstoneSeedPlantHeight = builder.defineInteger("glowstoneSeeds.plantHeight", 15, -64, 320, "Outside the Nether, glowstone seeds only take on a netherrack ceiling at or below this height. In the Nether they take at any height.");

        floatingIslandRarity = builder.defineInteger("floatingIslands.rarity", 500, 0, 10000, RARITY);
        floatingIslandSizeVariance = builder.defineInteger("floatingIslands.sizeVariance", 5, 1, 8, "How much an island's radius varies above the base of 7. Capped at 8 because an island has to stay inside the chunks the generator lets a feature write to.");

        desertWellRarity = builder.defineInteger("desertWells.rarity", 1000, 0, 10000, RARITY + " These are the deep wells with a chest at the bottom; vanilla's own desert wells are untouched.");

        wheatFieldRarity = builder.defineInteger("worldGenExpanded.wheatFieldRarity", 150, 0, 10000, RARITY);
        wheatFieldSize = builder.defineInteger("worldGenExpanded.wheatFieldSize", 6, 1, 11, "The radius in blocks of a wheat field.");
        saplingRarity = builder.defineInteger("worldGenExpanded.saplingRarity", 200, 0, 10000, RARITY);
        treeStumpRarity = builder.defineInteger("worldGenExpanded.treeStumpRarity", 200, 0, 10000, RARITY);
        cactusFieldRarity = builder.defineInteger("worldGenExpanded.cactusFieldRarity", 150, 0, 10000, RARITY);
        cactusFieldSize = builder.defineInteger("worldGenExpanded.cactusFieldSize", 8, 1, 11, "The radius in blocks of a cactus field.");
        sandstonePillarRarity = builder.defineInteger("worldGenExpanded.sandstonePillarRarity", 250, 0, 10000, RARITY);
        sandPitRarity = builder.defineInteger("worldGenExpanded.sandPitRarity", 250, 0, 10000, RARITY);
        sandPitSize = builder.defineInteger("worldGenExpanded.sandPitSize", 7, 2, 11, "The radius in blocks of a sand pit. Its depth follows from it.");
        melonRarity = builder.defineInteger("worldGenExpanded.melonRarity", 200, 0, 10000, RARITY);

        builder.setup();
    }
}
