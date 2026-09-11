package com.grim3212.assorted.world.gametest;

import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.world.gametest.WorldTestSupport.*;

/**
 * Randomite ore: the tool it needs and the experience it drops.
 */
final class OreTests {

    private OreTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("randomite_ore_needs_a_stone_tool", OreTests::randomiteOreNeedsAStoneTool);
        out.accept("randomite_ore_drops_experience", OreTests::randomiteOreDropsExperience);
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
}
