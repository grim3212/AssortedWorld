package com.grim3212.assorted.world.gametest;

import com.grim3212.assorted.world.common.block.RuneBlock;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.util.RuinUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.world.gametest.WorldTestSupport.*;

/**
 * Runes: effects when stepped on, scaling with experience, distinct effects and placing.
 */
final class RuneTests {

    private RuneTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("rune_applies_effect_when_stepped_on", RuneTests::runeAppliesEffectWhenSteppedOn);
        out.accept("rune_effect_scales_with_experience", RuneTests::runeEffectScalesWithExperience);
        out.accept("every_rune_maps_to_a_distinct_effect", RuneTests::everyRuneMapsToADistinctEffect);
        out.accept("every_rune_places", RuneTests::everyRunePlaces);
    }

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
}
