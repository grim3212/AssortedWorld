package com.grim3212.assorted.world.gametest;

import com.grim3212.assorted.world.api.WorldLootTables;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.world.gametest.WorldTestSupport.*;

/**
 * Desert wells: what the chest at the bottom of each depth tier is worth.
 */
final class DesertWellTests {

    private DesertWellTests() {
    }

    /** Rolls per tier. Big enough that the 5%-to-50% ladder cannot be sampling noise. */
    private static final int ROLLS = 2000;

    /** The tiers shallowest first, each with the sherd chance {@code WorldChestLoot.sherdPool} gives it. */
    private static final List<Tier> TIERS = List.of(
            new Tier(WorldLootTables.CHESTS_DESERT_WELL_10, 10, 5),
            new Tier(WorldLootTables.CHESTS_DESERT_WELL_15, 15, 10),
            new Tier(WorldLootTables.CHESTS_DESERT_WELL_20, 20, 20),
            new Tier(WorldLootTables.CHESTS_DESERT_WELL_25, 25, 30),
            new Tier(WorldLootTables.CHESTS_DESERT_WELL_30, 30, 50));

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("desert_well_sherd_odds_climb_with_depth", DesertWellTests::desertWellSherdOddsClimbWithDepth);
    }

    /**
     * A sherd gets likelier the deeper the shaft. The seeds are fixed, so a tier that drifts off its
     * intended share fails every run rather than one run in ten.
     */
    private static void desertWellSherdOddsClimbWithDepth(GameTestHelper helper) {
        ReloadableServerRegistries.Holder registries = helper.getLevel().getServer().reloadableRegistries();
        // CHEST requires an origin - a table that rolls fine in datagen throws without one.
        LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(helper.absolutePos(CENTRE)))
                .create(LootContextParamSets.CHEST);

        int previous = -1;
        for (Tier tier : TIERS) {
            LootTable table = registries.getLootTable(tier.table());

            int withSherd = 0;
            for (int seed = 0; seed < ROLLS; seed++) {
                if (table.getRandomItems(params, seed).stream().anyMatch(DesertWellTests::isDesertSherd)) {
                    withSherd++;
                }
            }

            // A chest is one roll of the sherd pool, so the share of chests holding a sherd is the
            // pool's own percentage. Five points of slack covers the spread at this many rolls.
            int expected = tier.percent() * ROLLS / 100;
            helper.assertTrue(Math.abs(withSherd - expected) <= ROLLS / 20,
                    "the " + tier.depth() + " block well gave a sherd in " + withSherd + " of " + ROLLS + " chests, not about " + expected);
            helper.assertTrue(withSherd > previous,
                    "the " + tier.depth() + " block well is no likelier to give a sherd than the tier above it");
            previous = withSherd;
        }

        helper.succeed();
    }

    /**
     * Only the six sherds a desert already gives up. A sherd from anywhere else would make the well
     * a new source for one, which these tables deliberately are not.
     */
    private static boolean isDesertSherd(ItemStack stack) {
        return stack.is(Items.ARMS_UP_POTTERY_SHERD) || stack.is(Items.BREWER_POTTERY_SHERD) || stack.is(Items.ARCHER_POTTERY_SHERD)
                || stack.is(Items.MINER_POTTERY_SHERD) || stack.is(Items.PRIZE_POTTERY_SHERD) || stack.is(Items.SKULL_POTTERY_SHERD);
    }

    private record Tier(ResourceKey<LootTable> table, int depth, int percent) {
    }
}
