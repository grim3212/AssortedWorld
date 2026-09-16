package com.grim3212.assorted.world.gametest;

import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.world.common.block.GlowstoneSeedBlock;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.world.gametest.WorldTestSupport.*;

/**
 * Glowstone seeds: what they will hang from, what they ripen into, and the recipe that makes them.
 */
final class GlowstoneSeedTests {

    private GlowstoneSeedTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("glowstone_seeds_need_a_netherrack_ceiling", GlowstoneSeedTests::glowstoneSeedsNeedANetherrackCeiling);
        out.accept("glowstone_seeds_ripen_into_glowstone", GlowstoneSeedTests::glowstoneSeedsRipenIntoGlowstone);
        out.accept("glowstone_seed_recipe_resolves", GlowstoneSeedTests::glowstoneSeedRecipeResolves);
    }

    /**
     * A seed holds under netherrack and falls away from anything else. The test box is near y=0 in
     * the overworld, which is under the configured plant height, so only the ceiling is in question.
     */
    private static void glowstoneSeedsNeedANetherrackCeiling(GameTestHelper helper) {
        BlockPos supported = CENTRE;
        helper.setBlock(supported.above(), Blocks.NETHERRACK);
        helper.setBlock(supported, WorldBlocks.GLOWSTONE_SEEDS.get());
        helper.assertBlockPresent(WorldBlocks.GLOWSTONE_SEEDS.get(), supported);

        // Taking the netherrack away has to drop the seed, which is the neighbour update path.
        helper.setBlock(supported.above(), Blocks.STONE);
        helper.assertBlockNotPresent(WorldBlocks.GLOWSTONE_SEEDS.get(), supported);

        // The placement rule is canSurvive, which is what a block item asks before it places
        // anything. setBlock does not consult it, so this has to ask directly.
        BlockState seed = WorldBlocks.GLOWSTONE_SEEDS.get().defaultBlockState();
        BlockPos candidate = new BlockPos(1, 1, 1);
        BlockPos absolute = helper.absolutePos(candidate);

        helper.setBlock(candidate.above(), Blocks.STONE);
        helper.assertFalse(seed.canSurvive(helper.getLevel(), absolute), "a glowstone seed thinks it can hang from stone");

        helper.setBlock(candidate.above(), Blocks.NETHERRACK);
        helper.assertTrue(seed.canSurvive(helper.getLevel(), absolute), "a glowstone seed will not hang from netherrack");

        helper.succeed();
    }

    /**
     * A ripe seed hands its spot to vanilla's glowstone blob. Starting at the last step makes this
     * one random tick rather than a wait on the 1-in-10 growth roll.
     */
    private static void glowstoneSeedsRipenIntoGlowstone(GameTestHelper helper) {
        BlockPos seed = CENTRE;
        helper.setBlock(seed.above(), Blocks.NETHERRACK);
        helper.setBlock(seed, WorldBlocks.GLOWSTONE_SEEDS.get().defaultBlockState().setValue(GlowstoneSeedBlock.STEP, GlowstoneSeedBlock.RIPE));

        helper.randomTick(seed);
        helper.assertBlockPresent(Blocks.GLOWSTONE, seed);

        helper.succeed();
    }

    /** The recipe resolves, including the {@code #c:glowstone_dusts} tag it is keyed on. */
    private static void glowstoneSeedRecipeResolves(GameTestHelper helper) {
        helper.assertTrue(new ItemStack(Items.GLOWSTONE_DUST).is(LibCommonTags.Items.DUSTS_GLOWSTONE),
                "#c:dusts/glowstone does not contain glowstone dust, so the glowstone seed recipe can never match");

        ItemStack dust = new ItemStack(Items.GLOWSTONE_DUST);
        ItemStack empty = ItemStack.EMPTY;
        ItemStack result = craft(helper, CraftingInput.of(3, 2, List.of(
                dust.copy(), new ItemStack(Items.SOUL_SAND), dust.copy(),
                empty, dust.copy(), empty)), "glowstone dust around a soul sand");

        helper.assertTrue(result.is(WorldBlocks.GLOWSTONE_SEEDS.get().asItem()),
                "the glowstone seed recipe made " + result + " instead of glowstone seeds");

        helper.succeed();
    }
}
