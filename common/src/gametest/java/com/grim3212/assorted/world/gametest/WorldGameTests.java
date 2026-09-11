package com.grim3212.assorted.world.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
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
 * <p>
 * The tests themselves are split by feature into the {@code *Tests} classes in this package,
 * with shared helpers in {@code WorldTestSupport}; this only lists them.
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
