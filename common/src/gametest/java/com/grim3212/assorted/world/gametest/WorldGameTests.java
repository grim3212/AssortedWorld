package com.grim3212.assorted.world.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for AssortedWorld. A test volume cannot run worldgen, so these cover
 * what is reachable without it: the blocks a structure leaves, the loot its chests draw, and the
 * position-seeded helpers. The tests live in the {@code *Tests} classes; this only lists them.
 */
public final class WorldGameTests {

    private WorldGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        RuneTests.register(out);
        WorldgenTests.register(out);
        OreTests.register(out);
        GunpowderReedTests.register(out);
        AssetTests.register(out);
    }
}
