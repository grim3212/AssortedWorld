package com.grim3212.assorted.world.gametest;

import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.WorldCommonMod;
import com.grim3212.assorted.world.api.WorldLootTables;
import com.grim3212.assorted.world.common.gen.WorldBiomeModifiers;
import com.grim3212.assorted.world.common.gen.placement.ConfigRarityFilter;
import com.grim3212.assorted.world.common.gen.placement.WorldPlacements;
import com.grim3212.assorted.world.common.util.RuinUtil;
import com.grim3212.assorted.world.data.WorldGenData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.world.gametest.WorldTestSupport.*;

/**
 * Worldgen: structure chest loot, piece randomness, datapack entries and biome modifiers.
 */
final class WorldgenTests {

    private WorldgenTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("structure_chest_loot_tables_roll_items", WorldgenTests::structureChestLootTablesRollItems);
        out.accept("piece_random_depends_only_on_position", WorldgenTests::pieceRandomDependsOnlyOnPosition);
        out.accept("worldgen_datapack_entries_resolve", WorldgenTests::worldgenDatapackEntriesResolve);
        out.accept("mod_features_are_attached_to_biomes", WorldgenTests::modFeaturesAreAttachedToBiomes);
        out.accept("config_rarity_gates_the_new_features", WorldgenTests::configRarityGatesTheNewFeatures);
        out.accept("vanilla_desert_well_is_replaced", WorldgenTests::vanillaDesertWellIsReplaced);
    }

    /**
     * Every structure chest loot table exists and rolls something. {@code setLootTable} on a
     * missing table is silent, and the chest simply comes up empty.
     */
    private static void structureChestLootTablesRollItems(GameTestHelper helper) {
        ReloadableServerRegistries.Holder registries = helper.getLevel().getServer().reloadableRegistries();
        // CHEST requires an origin - a table that rolls fine in datagen throws without one.
        LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(helper.absolutePos(CENTRE)))
                .create(LootContextParamSets.CHEST);

        List<ResourceKey<LootTable>> tables = List.of(
                WorldLootTables.CHESTS_FOUNTAIN,
                WorldLootTables.CHESTS_PYRAMID,
                WorldLootTables.CHESTS_RUIN,
                WorldLootTables.CHESTS_WATER_DOME_COBBLESTONE,
                WorldLootTables.CHESTS_WATER_DOME_GLOWSTONE,
                WorldLootTables.CHESTS_WATER_DOME_IRON,
                WorldLootTables.CHESTS_WATER_DOME_OBSIDIAN,
                WorldLootTables.CHESTS_DESERT_WELL_10,
                WorldLootTables.CHESTS_DESERT_WELL_15,
                WorldLootTables.CHESTS_DESERT_WELL_20,
                WorldLootTables.CHESTS_DESERT_WELL_25,
                WorldLootTables.CHESTS_DESERT_WELL_30);

        for (ResourceKey<LootTable> key : tables) {
            LootTable table = registries.getLootTable(key);
            helper.assertFalse(table == LootTable.EMPTY, "loot table " + key.identifier() + " does not exist");

            // Every one of these has a pool of guaranteed rolls with no empty entry in it, so a
            // fixed seed makes "rolled nothing" a real failure rather than bad luck.
            helper.assertFalse(table.getRandomItems(params, 42L).isEmpty(), "loot table " + key.identifier() + " rolled nothing");
        }

        helper.succeed();
    }

    /**
     * A clipped {@code postProcess} pass writes only its own chunk, so two passes over one piece
     * have to agree block for block. {@link RuinUtil#pieceRandom} is what makes that true: it takes
     * the piece's own box and the world seed, and nothing that varies per pass.
     */
    private static void pieceRandomDependsOnlyOnPosition(GameTestHelper helper) {
        BoundingBox piece = new BoundingBox(120, 64, -40, 130, 74, -30);
        BoundingBox shifted = new BoundingBox(121, 64, -40, 131, 74, -30);
        BoundingBox stacked = new BoundingBox(120, 96, -40, 130, 106, -30);

        helper.assertValueEqual(draw(helper, piece), draw(helper, piece), "pieceRandom is not stable for one piece");
        helper.assertFalse(draw(helper, piece).equals(draw(helper, shifted)), "pieceRandom does not vary with position");
        helper.assertFalse(draw(helper, piece).equals(draw(helper, stacked)), "pieceRandom does not salt on height");

        BlockPos origin = RuinUtil.pieceOrigin(piece);
        helper.assertValueEqual(origin.getX(), piece.getCenter().getX(), "piece origin x");
        helper.assertValueEqual(origin.getZ(), piece.getCenter().getZ(), "piece origin z");
        // The box floor, not its centre: this is the height every pass after the first sees.
        helper.assertValueEqual(origin.getY(), piece.minY(), "piece origin y");

        helper.succeed();
    }

    /**
     * The four structures and two features are in the loaded datapack and something places them.
     * Each can go missing silently: a misrouted datagen run prunes its json, and a wrong {@code c:}
     * name in a {@code has_structure/*} biome tag loads it empty.
     */
    private static void worldgenDatapackEntriesResolve(GameTestHelper helper) {
        RegistryAccess registries = helper.getLevel().registryAccess();
        List<String> problems = new ArrayList<>();

        Registry<Structure> structures = registries.lookupOrThrow(Registries.STRUCTURE);
        Registry<StructureSet> structureSets = registries.lookupOrThrow(Registries.STRUCTURE_SET);
        for (String name : List.of("fountain", "pyramid", "snowball", "water_dome")) {
            Identifier id = Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
            Structure structure = structures.getValue(id);
            if (structure == null) {
                problems.add("structure " + id + " is not in the registry");
            } else if (structure.biomes().size() == 0) {
                problems.add("structure " + id + " has an empty biome tag, so it can never generate");
            }
            if (structureSets.getValue(id) == null) {
                problems.add("structure set " + id + " is not in the registry, so nothing places " + name);
            }
        }

        Registry<ConfiguredFeature<?, ?>> configured = registries.lookupOrThrow(Registries.CONFIGURED_FEATURE);
        Registry<PlacedFeature> placed = registries.lookupOrThrow(Registries.PLACED_FEATURE);
        for (String name : List.of("ruin", "spire", "ore_randomite", "patch_gunpowder_reed", "floating_island", "desert_well", "wheat_field", "cactus_field", "sand_pillar", "sand_pit", "patch_saplings", "tree_stumps", "patch_melons")) {
            Identifier id = Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
            if (configured.getValue(id) == null) {
                problems.add("configured feature " + id + " is not in the registry");
            }
            if (placed.getValue(id) == null) {
                problems.add("placed feature " + id + " is not in the registry");
            }
        }

        helper.assertTrue(problems.isEmpty(), "worldgen datapack entries missing: " + String.join("; ", problems));
        helper.succeed();
    }

    /**
     * The config rarity modifier is registered, every part name it knows reads back a rarity, and the
     * placed features that are meant to be config gated carry it. A placed feature that lost the
     * modifier would silently generate in every chunk, and one naming a part nothing registered
     * would silently generate in none.
     */
    private static void configRarityGatesTheNewFeatures(GameTestHelper helper) {
        List<String> problems = new ArrayList<>();

        Identifier rarityType = BuiltInRegistries.PLACEMENT_MODIFIER_TYPE.getKey(WorldPlacements.CONFIG_RARITY.get());
        if (rarityType == null) {
            problems.add("the config_rarity placement modifier type is not registered, so its placed features cannot load");
        }

        for (String part : List.of(WorldPlacements.Parts.FLOATING_ISLAND, WorldPlacements.Parts.DESERT_WELL, WorldPlacements.Parts.WHEAT_FIELD, WorldPlacements.Parts.SAPLING,
                WorldPlacements.Parts.TREE_STUMP, WorldPlacements.Parts.CACTUS_FIELD, WorldPlacements.Parts.SANDSTONE_PILLAR, WorldPlacements.Parts.SAND_PIT, WorldPlacements.Parts.MELON)) {
            if (WorldPlacements.rarity(part) <= 0) {
                problems.add("part " + part + " reads back no rarity, so it can never generate");
            }
        }

        Registry<PlacedFeature> placed = helper.getLevel().registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
        for (String name : List.of("floating_island", "desert_well", "wheat_field", "cactus_field", "sand_pillar", "sand_pit", "patch_saplings", "tree_stumps", "patch_melons")) {
            Identifier id = Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
            PlacedFeature feature = placed.getValue(id);
            if (feature == null) {
                problems.add("placed feature " + id + " is not in the registry");
            } else if (feature.placement().stream().noneMatch(modifier -> modifier instanceof ConfigRarityFilter)) {
                problems.add("placed feature " + id + " has no config rarity filter, so the config cannot turn it off");
            }
        }

        helper.assertTrue(problems.isEmpty(), "config gated features: " + String.join("; ", problems));
        helper.succeed();
    }

    /**
     * With the config at its default, the desert has this mod's well and not vanilla's. The removal
     * goes through a different loader API on each side, like the additions.
     */
    private static void vanillaDesertWellIsReplaced(GameTestHelper helper) {
        helper.assertTrue(WorldCommonMod.COMMON_CONFIG.desertWellReplaceVanilla.get(), "desertWells.replaceVanilla is off in the test config");

        Biome desert = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).getValueOrThrow(Biomes.DESERT);
        helper.assertFalse(hasFeature(desert, WorldBiomeModifiers.VANILLA_DESERT_WELL), "the desert still has vanilla's desert well");
        helper.assertTrue(hasFeature(desert, WorldGenData.DESERT_WELL_KEY), "the desert lost this mod's desert well as well");
        helper.succeed();
    }

    /**
     * Every placed feature is attached to a biome. {@code WorldBiomeModifiers} does that through a
     * different loader API on each side, so this is where the two could disagree.
     */
    private static void modFeaturesAreAttachedToBiomes(GameTestHelper helper) {
        Registry<Biome> biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        List<String> problems = new ArrayList<>();

        for (String name : List.of("ore_randomite", "patch_gunpowder_reed", "ruin", "spire", "floating_island", "desert_well", "wheat_field", "cactus_field", "sand_pillar", "sand_pit", "patch_saplings", "tree_stumps", "patch_melons")) {
            Identifier id = Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
            int biomeCount = 0;
            for (Biome biome : biomes) {
                if (hasFeature(biome, id)) {
                    biomeCount++;
                }
            }
            if (biomeCount == 0) {
                problems.add(id + " is attached to no biome at all");
            }
        }

        helper.assertTrue(problems.isEmpty(), "features that can never generate: " + String.join("; ", problems));
        helper.succeed();
    }
}
