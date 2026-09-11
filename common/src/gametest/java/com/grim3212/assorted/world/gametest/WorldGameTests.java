package com.grim3212.assorted.world.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.MinecraftServer;
import com.grim3212.assorted.lib.platform.Services;
import java.io.BufferedReader;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.api.WorldLootTables;
import com.grim3212.assorted.world.common.block.RuneBlock;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.handlers.WorldCreativeItems;
import com.grim3212.assorted.world.common.util.RuinUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for AssortedWorld.
 * <p>
 * The bodies live in common because the behaviour they check is common; each loader module only
 * registers them into {@code Registries.TEST_FUNCTION} through its own hook, and
 * {@code data/assortedworld/test_instance/*.json} pairs each one with the shared {@code test_box}
 * structure.
 * <p>
 * This mod is mostly worldgen, and a 9x9x9 test volume cannot run a generation pass - no heightmap,
 * no light level, no biome placement. So what is covered here is what is reachable without one: the
 * blocks a structure leaves behind, the loot its chests draw, and the position-seeded helpers the
 * clipped-worldgen fix rests on. Whether a structure actually generates is a human's job, in
 * {@code TESTING-CHECKLIST.md}.
 */
public final class WorldGameTests {

    private WorldGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("rune_applies_effect_when_stepped_on", WorldGameTests::runeAppliesEffectWhenSteppedOn);
        out.accept("rune_effect_scales_with_experience", WorldGameTests::runeEffectScalesWithExperience);
        out.accept("every_rune_maps_to_a_distinct_effect", WorldGameTests::everyRuneMapsToADistinctEffect);
        out.accept("randomite_ore_needs_a_stone_tool", WorldGameTests::randomiteOreNeedsAStoneTool);
        out.accept("randomite_ore_drops_experience", WorldGameTests::randomiteOreDropsExperience);
        out.accept("structure_chest_loot_tables_roll_items", WorldGameTests::structureChestLootTablesRollItems);
        out.accept("piece_random_depends_only_on_position", WorldGameTests::pieceRandomDependsOnlyOnPosition);
        out.accept("gunpowder_reed_grows_only_on_valid_ground", WorldGameTests::gunpowderReedGrowsOnlyOnValidGround);
        out.accept("every_rune_places", WorldGameTests::everyRunePlaces);
        out.accept("gunpowder_recipes_resolve", WorldGameTests::gunpowderRecipesResolve);
        out.accept("every_block_and_item_has_a_model_and_a_name", WorldGameTests::everyBlockAndItemHasAModelAndAName);
        out.accept("worldgen_datapack_entries_resolve", WorldGameTests::worldgenDatapackEntriesResolve);
        out.accept("mod_features_are_attached_to_biomes", WorldGameTests::modFeaturesAreAttachedToBiomes);
        out.accept("every_recipe_loads_or_is_conditioned_off", WorldGameTests::everyRecipeLoadsOrIsConditionedOff);
    }

    /** Middle of the 9x9x9 box, one block above its floor - room on every side for a drop. */
    private static final BlockPos CENTRE = new BlockPos(4, 1, 4);

    /**
     * A mob that walks onto a rune picks up its effect. {@code stepOn} fires out of
     * {@code Entity#move} for whatever the entity is standing on, so the pig only has to land.
     */
    private static void runeAppliesEffectWhenSteppedOn(GameTestHelper helper) {
        helper.setBlock(CENTRE, WorldBlocks.UR_RUNE.get());
        Pig pig = helper.spawn(EntityTypes.PIG, CENTRE.above());

        // A non-player gets xpLevel 1, so the amplifier is floor(0.06 * 1) = 0.
        helper.succeedWhen(() -> helper.assertLivingEntityHasMobEffect(pig, MobEffects.STRENGTH, 0));
    }

    /**
     * Both halves of {@code RuneBlock#getPotionEffect} scale off the player's experience level, and
     * nothing else in the mod reads that number - so a change to the formula shows up only here.
     * Two players rather than one: {@code addEffect} keeps whichever instance is stronger.
     */
    private static void runeEffectScalesWithExperience(GameTestHelper helper) {
        helper.setBlock(CENTRE, WorldBlocks.RAD_RUNE.get());

        MobEffectInstance noExperience = useRuneAs(helper, 0);
        helper.assertValueEqual(noExperience.getAmplifier(), 0, "rune amplifier at experience level 0");
        helper.assertValueEqual(noExperience.getDuration(), 1500, "rune duration at experience level 0");

        MobEffectInstance thirtyLevels = useRuneAs(helper, 30);
        helper.assertValueEqual(thirtyLevels.getAmplifier(), 1, "rune amplifier at experience level 30");
        helper.assertValueEqual(thirtyLevels.getDuration(), 4500, "rune duration at experience level 30");

        helper.succeed();
    }

    private static MobEffectInstance useRuneAs(GameTestHelper helper, int experienceLevel) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.experienceLevel = experienceLevel;
        helper.useBlock(CENTRE, player);

        MobEffectInstance effect = player.getEffect(MobEffects.SPEED);
        helper.assertTrue(effect != null, "using a rad rune gave no speed effect at experience level " + experienceLevel);
        return effect;
    }

    /**
     * {@code runeBlocks()} and {@code RuneType} are two hand-written parallel lists, and the loot
     * tables, the block tags and every structure's rune roll all index into the first one. A rune
     * whose effect name does not resolve is silently a null holder, and two runes sharing an effect
     * is invisible in game.
     */
    private static void everyRuneMapsToADistinctEffect(GameTestHelper helper) {
        Block[] runes = WorldBlocks.runeBlocks();
        helper.assertValueEqual(runes.length, RuneBlock.RuneType.values.length, "rune block count");

        Set<MobEffect> effects = new HashSet<>();
        for (int i = 0; i < runes.length; i++) {
            RuneBlock.RuneType type = RuneBlock.RuneType.values[i];
            String expectedName = type.getSerializedName() + "_rune";
            String actualName = BuiltInRegistries.BLOCK.getKey(runes[i]).getPath();
            helper.assertValueEqual(actualName, expectedName, "rune block " + i + " is out of step with RuneType");
            helper.assertTrue(runes[i] instanceof RuneBlock, actualName + " is not a RuneBlock");

            Holder<MobEffect> effect = type.getEffect();
            helper.assertTrue(effect != null, "rune " + expectedName + " names an effect that does not exist");
            helper.assertTrue(effects.add(effect.value()), "rune " + expectedName + " shares its effect with another rune");
        }

        // runeAt takes an index a structure piece serialised, so it has to stay in range for
        // anything an old save could hand back.
        helper.assertValueEqual(RuinUtil.runeAt(0), runes[0], "runeAt(0)");
        helper.assertValueEqual(RuinUtil.runeAt(runes.length), runes[0], "runeAt wraps past the end");
        helper.assertValueEqual(RuinUtil.runeAt(-1), runes[runes.length - 1], "runeAt wraps below zero");

        helper.succeed();
    }

    /**
     * Both randomite ores are {@code requiresCorrectToolForDrops} and sit in
     * {@code #minecraft:needs_stone_tool}, so wood harvests neither. Nothing in the ore's own loot
     * table says so - the gate is the block property plus the tag - which is why it is worth
     * pinning here.
     */
    private static void randomiteOreNeedsAStoneTool(GameTestHelper helper) {
        for (Block ore : List.of(WorldBlocks.RANDOMITE_ORE.get(), WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get())) {
            helper.setBlock(CENTRE, ore);
            BlockState state = helper.getBlockState(CENTRE);
            String name = BuiltInRegistries.BLOCK.getKey(ore).getPath();
            helper.assertTrue(state.requiresCorrectToolForDrops(), name + " drops without a correct tool");

            helper.assertFalse(new ItemStack(Items.WOODEN_PICKAXE).isCorrectToolForDrops(state), "a wooden pickaxe harvests " + name);
            helper.assertTrue(new ItemStack(Items.IRON_PICKAXE).isCorrectToolForDrops(state), "an iron pickaxe does not harvest " + name);
        }

        helper.succeed();
    }

    /**
     * Randomite is a {@code DropExperienceBlock}, and its experience comes out of
     * {@code spawnAfterBreak} rather than out of the loot table - a separate path that a loot
     * table change cannot cover. Both ores, because each declares its own {@code UniformInt} and
     * the deepslate one is the easy half of the pair to forget.
     */
    private static void randomiteOreDropsExperience(GameTestHelper helper) {
        // Measured as deltas: whatever the first break dropped is still lying in the box when the
        // second one happens, and item entities are counted by stack size so a merge cannot hide a
        // drop either.
        int experienceSoFar = 0;
        int itemsSoFar = 0;

        for (Block ore : List.of(WorldBlocks.RANDOMITE_ORE.get(), WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get())) {
            String name = BuiltInRegistries.BLOCK.getKey(ore).getPath();
            helper.setBlock(CENTRE, ore);

            // helper.destroyBlock breaks with dropBlock false, which skips spawnAfterBreak entirely.
            helper.getLevel().destroyBlock(helper.absolutePos(CENTRE), true);
            helper.assertBlockPresent(Blocks.AIR, CENTRE);

            int items = helper.getEntities(EntityTypes.ITEM).stream().mapToInt(item -> item.getItem().getCount()).sum();
            helper.assertTrue(items > itemsSoFar, name + " dropped no loot");
            itemsSoFar = items;

            int experience = helper.getEntities(EntityTypes.EXPERIENCE_ORB).stream().mapToInt(ExperienceOrb::getValue).sum();
            int dropped = experience - experienceSoFar;
            helper.assertTrue(dropped >= 2 && dropped <= 5, name + " dropped " + dropped + " experience, not the 2-5 it is registered with");
            experienceSoFar = experience;
        }

        helper.succeed();
    }

    /**
     * Every structure chest table has to exist and roll something. The first three were addressed
     * at {@code loot_tables/} - the plural directory 26.2 stopped reading - and
     * {@code setLootTable} on a table that is not there is silent, so every structure chest in the
     * world came up empty and nothing said so.
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
                WorldLootTables.CHESTS_WATER_DOME_OBSIDIAN);

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

    private static List<Integer> draw(GameTestHelper helper, BoundingBox box) {
        RandomSource random = RuinUtil.pieceRandom(helper.getLevel(), box);
        return List.of(random.nextInt(30), random.nextInt(30), random.nextInt(30), random.nextInt(30),
                random.nextInt(30), random.nextInt(30), random.nextInt(30), random.nextInt(30));
    }

    /** Gunpowder reed is sugar cane, so it grows on wet sand and dies on anything else. */
    private static void gunpowderReedGrowsOnlyOnValidGround(GameTestHelper helper) {
        BlockPos sand = CENTRE;
        BlockPos reed = sand.above();
        helper.setBlock(sand, Blocks.SAND);
        helper.setBlock(sand.east(), Blocks.WATER);

        // Age 15 is the last tick before growth, so one random tick is the whole test.
        helper.setBlock(reed, WorldBlocks.GUNPOWDER_REED.get().defaultBlockState().setValue(SugarCaneBlock.AGE, 15));
        helper.randomTick(reed);
        helper.assertBlockPresent(WorldBlocks.GUNPOWDER_REED.get(), reed.above());

        BlockPos dry = new BlockPos(1, 1, 1);
        helper.setBlock(dry, WorldBlocks.GUNPOWDER_REED.get());
        helper.tickBlock(dry);
        helper.assertBlockNotPresent(WorldBlocks.GUNPOWDER_REED.get(), dry);

        helper.succeed();
    }

    /**
     * All sixteen runes are separate blocks with no block state properties at all, so "a rune
     * places" really means the registry entry, the block and its item still line up. They go in
     * side by side rather than one after another in the same spot: a rune that quietly resolved to
     * its neighbour would pass a single-position loop.
     */
    private static void everyRunePlaces(GameTestHelper helper) {
        Block[] runes = WorldBlocks.runeBlocks();
        // Two rows of eight along the floor of the box, which is 9 wide.
        helper.assertTrue(runes.length <= 16, "the test box has room for 16 runes, not " + runes.length);

        for (int i = 0; i < runes.length; i++) {
            helper.setBlock(runePos(i), runes[i]);
        }

        List<String> problems = new ArrayList<>();
        for (int i = 0; i < runes.length; i++) {
            Block rune = runes[i];
            String name = String.valueOf(BuiltInRegistries.BLOCK.getKey(rune));
            BlockState placed = helper.getBlockState(runePos(i));

            if (!placed.is(rune)) {
                problems.add(name + " did not place - " + BuiltInRegistries.BLOCK.getKey(placed.getBlock()) + " is there instead");
                continue;
            }
            // One "" variant is all the blockstate json carries; a property would need more.
            if (!placed.getProperties().isEmpty()) {
                problems.add(name + " has block state properties " + placed.getProperties() + ", which its blockstate json does not cover");
            }
            if (!(rune.asItem() instanceof BlockItem item) || item.getBlock() != rune) {
                problems.add(name + " has no block item of its own");
            }
        }

        helper.assertTrue(problems.isEmpty(), "runes did not place cleanly: " + String.join("; ", problems));
        helper.succeed();
    }

    private static BlockPos runePos(int index) {
        return new BlockPos(index % 8, 1, index / 8);
    }

    /**
     * Both gunpowder recipes, and the tag the shaped one is keyed on. {@code #c:gunpowders} is
     * filled by AssortedLib, not by this mod, and a shaped recipe whose key resolves to an empty
     * tag matches nothing and reports nothing - which is exactly how this recipe stayed dead
     * through most of the port.
     */
    private static void gunpowderRecipesResolve(GameTestHelper helper) {
        helper.assertTrue(new ItemStack(Items.GUNPOWDER).is(LibCommonTags.Items.GUNPOWDER),
                "#c:gunpowders does not contain gunpowder, so the gunpowder reed recipe can never match");

        ItemStack powder = new ItemStack(Items.GUNPOWDER);
        ItemStack reedResult = craft(helper, CraftingInput.of(3, 3, List.of(
                powder.copy(), powder.copy(), powder.copy(),
                powder.copy(), new ItemStack(Items.SUGAR_CANE), powder.copy(),
                powder.copy(), powder.copy(), powder.copy())), "eight gunpowder around a sugar cane");
        helper.assertTrue(reedResult.is(WorldBlocks.GUNPOWDER_REED.get().asItem()),
                "the gunpowder reed recipe made " + reedResult + " instead of a gunpowder reed");

        ItemStack powderResult = craft(helper, CraftingInput.of(1, 1, List.of(new ItemStack(WorldBlocks.GUNPOWDER_REED.get()))), "a gunpowder reed on its own");
        helper.assertTrue(powderResult.is(Items.GUNPOWDER), "a gunpowder reed made " + powderResult + " instead of gunpowder");

        helper.succeed();
    }

    private static ItemStack craft(GameTestHelper helper, CraftingInput input, String what) {
        // recipeAccess() is the full RecipeManager server side, so a headless test sees every
        // loaded recipe without needing a crafting menu.
        Optional<RecipeHolder<CraftingRecipe>> found = helper.getLevel().recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel());
        helper.assertTrue(found.isPresent(), "no crafting recipe matches " + what);
        return found.get().value().assemble(input);
    }

    /**
     * Every block and item this mod registers needs a model and a name, and so does the creative
     * tab. Missing models and missing lang keys are the most repeated failure of this port and
     * neither says anything at runtime - a missing model is a purple cube and a missing lang key is
     * the raw translation string.
     * <p>
     * The tab's contents are this mod's own blocks, added in {@code WorldCreativeItems}, so walking
     * the registries covers the same ground and also catches anything registered but never added.
     */
    private static void everyBlockAndItemHasAModelAndAName(GameTestHelper helper) {
        JsonObject lang = readJson(helper, "/assets/" + Constants.MOD_ID + "/lang/en_us.json");
        List<String> problems = new ArrayList<>();

        if (!BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(WorldCreativeItems.CREATIVE_TAB_KEY)) {
            problems.add("the creative tab " + WorldCreativeItems.CREATIVE_TAB_KEY.identifier() + " is not registered");
        }
        String tabKey = "itemGroup." + Constants.MOD_ID;
        if (!lang.has(tabKey)) {
            problems.add("the creative tab has no lang key " + tabKey);
        }

        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            if (!resourceExists("/assets/" + Constants.MOD_ID + "/blockstates/" + id.getPath() + ".json")) {
                problems.add("block " + id + " has no blockstate json");
            }
            if (!lang.has(block.getDescriptionId())) {
                problems.add("block " + id + " has no lang key " + block.getDescriptionId());
            }
        }

        for (Item item : BuiltInRegistries.ITEM) {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            // Item models moved out of models/item into their own items/ directory in 1.21.4.
            if (!resourceExists("/assets/" + Constants.MOD_ID + "/items/" + id.getPath() + ".json")) {
                problems.add("item " + id + " has no item model json");
            }
            if (!lang.has(item.getDescriptionId())) {
                problems.add("item " + id + " has no lang key " + item.getDescriptionId());
            }
        }

        // All of them at once: one failure per run would make fixing these a slow loop.
        helper.assertTrue(problems.isEmpty(), problems.size() + " asset problems: " + String.join("; ", problems));
        helper.succeed();
    }

    /** The mod's own assets are on the classpath even on a headless server, jar or source root. */
    private static boolean resourceExists(String path) {
        try (InputStream in = WorldGameTests.class.getResourceAsStream(path)) {
            return in != null;
        } catch (IOException e) {
            return false;
        }
    }

    private static JsonObject readJson(GameTestHelper helper, String path) {
        try (InputStream in = WorldGameTests.class.getResourceAsStream(path)) {
            helper.assertTrue(in != null, path + " is not on the classpath");
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            helper.assertTrue(false, "could not read " + path + ": " + e);
            return new JsonObject();
        }
    }

    /**
     * A registry-level check, not a generation one: whether the four structures and the two features
     * are in the loaded datapack at all, and whether anything places them.
     * <p>
     * Worth pinning because each of these can go missing in silence. The two datagen runs each prune
     * their own output root, so a structure json is one misrouted run away from disappearing; and
     * every {@code has_structure/*} biome tag is a single optional reference to a {@code c:} tag -
     * get that name wrong and the tag loads empty, leaving the structure registered, placed, and
     * generating nowhere at all.
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
        for (String name : List.of("ruin", "spire", "ore_randomite", "patch_gunpowder_reed")) {
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
     * The other half of the same question: a placed feature that exists but is attached to no biome
     * never runs. {@code WorldBiomeModifiers} is the only thing that attaches these, and it goes
     * through a different loader API on each side - a {@code BiomeModifier} on NeoForge,
     * {@code BiomeModifications} on Fabric - so this is where the two can silently disagree.
     */
    private static void modFeaturesAreAttachedToBiomes(GameTestHelper helper) {
        Registry<Biome> biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        List<String> problems = new ArrayList<>();

        for (String name : List.of("ore_randomite", "patch_gunpowder_reed", "ruin", "spire")) {
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

    private static boolean hasFeature(Biome biome, Identifier id) {
        for (HolderSet<PlacedFeature> step : biome.getGenerationSettings().features()) {
            for (Holder<PlacedFeature> feature : step) {
                if (feature.unwrapKey().filter(key -> key.identifier().equals(id)).isPresent()) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Every recipe file this mod ships either loaded, or carries this loader's load conditions and was
     * skipped by them. A file with neither failed to parse. On Fabric that was every conditional
     * recipe for a while: Fabric's datagen wrote them without conditions, and the NeoForge copy that
     * shadowed it carries a key Fabric ignores - so only this loader's own key counts.
     */
    private static void everyRecipeLoadsOrIsConditionedOff(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        FileToIdConverter recipes = FileToIdConverter.json("recipe");
        String conditionsKey = Services.PLATFORM.getPlatformName().equals("Fabric") ? "fabric:load_conditions" : "neoforge:conditions";
        List<String> failed = new ArrayList<>();

        recipes.listMatchingResources(server.getResourceManager()).forEach((file, resource) -> {
            Identifier id = recipes.fileToId(file);
            if (!id.getNamespace().equals(Constants.MOD_ID) || server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, id)).isPresent()) {
                return;
            }

            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (!json.has(conditionsKey)) {
                    failed.add(id.toString());
                }
            } catch (IOException e) {
                failed.add(id + " (" + e.getMessage() + ")");
            }
        });

        helper.assertTrue(failed.isEmpty(), failed.size() + " recipes failed to load without being conditioned off: " + String.join(", ", failed.subList(0, Math.min(10, failed.size()))));
        helper.succeed();
    }
}
