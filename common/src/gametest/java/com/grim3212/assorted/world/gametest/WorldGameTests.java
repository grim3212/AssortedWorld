package com.grim3212.assorted.world.gametest;

import com.grim3212.assorted.world.api.WorldLootTables;
import com.grim3212.assorted.world.common.block.RuneBlock;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.util.RuinUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
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
     * table change cannot cover.
     */
    private static void randomiteOreDropsExperience(GameTestHelper helper) {
        helper.setBlock(CENTRE, WorldBlocks.RANDOMITE_ORE.get());

        // helper.destroyBlock breaks with dropBlock false, which skips spawnAfterBreak entirely.
        helper.getLevel().destroyBlock(helper.absolutePos(CENTRE), true);
        helper.assertBlockPresent(Blocks.AIR, CENTRE);

        helper.assertFalse(helper.getEntities(EntityTypes.ITEM).isEmpty(), "randomite ore dropped no loot");

        int experience = helper.getEntities(EntityTypes.EXPERIENCE_ORB).stream().mapToInt(ExperienceOrb::getValue).sum();
        helper.assertTrue(experience >= 2 && experience <= 5, "randomite ore dropped " + experience + " experience, not the 2-5 it is registered with");

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
}
