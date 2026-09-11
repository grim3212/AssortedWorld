package com.grim3212.assorted.world.gametest;

import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SugarCaneBlock;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.world.gametest.WorldTestSupport.*;

/**
 * The gunpowder reed: where it grows and the recipes that use it.
 */
final class GunpowderReedTests {

    private GunpowderReedTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("gunpowder_reed_grows_only_on_valid_ground", GunpowderReedTests::gunpowderReedGrowsOnlyOnValidGround);
        out.accept("gunpowder_recipes_resolve", GunpowderReedTests::gunpowderRecipesResolve);
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
}
