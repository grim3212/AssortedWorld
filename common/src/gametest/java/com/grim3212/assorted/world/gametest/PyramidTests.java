package com.grim3212.assorted.world.gametest;

import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidPiece;
import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;

/**
 * Where a pyramid's base goes, and what is buried in its floor. Pyramids used to take their height from the average ground of
 * whichever chunk generated first, and on a dune that left the downhill side of the base in the air.
 */
final class PyramidTests {

    private PyramidTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("pyramid_base_never_overhangs", PyramidTests::pyramidBaseNeverOverhangs);
        out.accept("pyramid_buries_suspicious_sand_by_the_rune", PyramidTests::pyramidBuriesSuspiciousSandByTheRune);
    }

    /**
     * The sand sits in the floor course around the rune and nowhere else. A brushable block is
     * {@code Fallable}: one placed in the open room above the floor falls and takes its sherd with
     * it, so the course it lands in is the whole point.
     */
    private static void pyramidBuriesSuspiciousSandByTheRune(GameTestHelper helper) {
        List<String> problems = new ArrayList<>();
        RandomSource random = RandomSource.create(7L);

        int total = 0;
        int pyramids = 200;
        for (int i = 0; i < pyramids; i++) {
            int buried = 0;
            // The same sweep the piece makes, so the sand can only come from where it really would.
            for (int x = -8; x <= 8; x++) {
                for (int z = -8; z <= 8; z++) {
                    for (int y = -1; y <= 8; y++) {
                        if (!PyramidPiece.suspiciousSand(random, new BlockPos(x, y, z))) {
                            continue;
                        }

                        buried++;
                        if (y != -1) {
                            problems.add("sand at y " + y + ", which is not the floor course");
                        }
                        if (Math.max(Math.abs(x), Math.abs(z)) > 2) {
                            problems.add("sand " + Math.max(Math.abs(x), Math.abs(z)) + " blocks out, too far from the rune");
                        }
                        if (x == 0 && z == 0) {
                            problems.add("sand straight under the rune");
                        }
                    }
                }
            }

            if (buried == 0) {
                problems.add("a pyramid buried no suspicious sand at all");
            }
            total += buried;
        }

        // 24 floor blocks at one in four, so about six; vanilla's desert pyramid scatters 5 to 7.
        int average = total / pyramids;
        helper.assertTrue(average >= 4 && average <= 8, "pyramids averaged " + average + " suspicious sands, not about 6");
        helper.assertTrue(problems.isEmpty(), "pyramid sand: " + String.join("; ", problems));
        helper.succeed();
    }

    /**
     * Level ground keeps the half-buried pyramid; on slopes, dunes and ridges in any direction the
     * whole base outline - corners included - stays below the ground; a slope steeper than the
     * pyramid is tall is refused.
     */
    private static void pyramidBaseNeverOverhangs(GameTestHelper helper) {
        List<String> problems = new ArrayList<>();

        for (int maxHeight = 8; maxHeight <= 16; maxHeight += 2) {
            // Level ground: half the pyramid below the surface, as before.
            Optional<PyramidStructure.Placement> level = PyramidStructure.placement((x, z) -> 70, 0, 0, maxHeight);
            if (level.isEmpty() || level.get().base() != 70 - maxHeight / 2) {
                problems.add("size " + maxHeight + " on level ground went to " + level);
            }

            // A slope too steep to stand on.
            int steep = maxHeight;
            int reach = PyramidPiece.halfWidth(maxHeight);
            if (PyramidStructure.placement((x, z) -> 70 + (x * steep) / reach, 0, 0, maxHeight).isPresent()) {
                problems.add("size " + maxHeight + " was placed on a slope of " + (2 * steep) + " blocks across it");
            }
        }

        // Random gentle terrain: planes tilted any way plus a dune bump, over and over.
        RandomSource random = RandomSource.create(99L);
        int placed = 0;
        for (int i = 0; i < 500; i++) {
            int maxHeight = 2 * (4 + random.nextInt(5));
            int reach = PyramidPiece.halfWidth(maxHeight);
            double slopeX = (random.nextDouble() - 0.5D) * 0.8D;
            double slopeZ = (random.nextDouble() - 0.5D) * 0.8D;
            double dune = random.nextDouble() * 6.0D;
            double duneX = random.nextInt(2 * reach + 1) - reach;
            IntBinaryOperator ground = (x, z) -> 70 + (int) Math.round(x * slopeX + z * slopeZ
                    + dune * Math.exp(-((x - duneX) * (x - duneX) + z * z) / 40.0D));

            Optional<PyramidStructure.Placement> placement = PyramidStructure.placement(ground, 0, 0, maxHeight);
            if (placement.isEmpty()) {
                continue;
            }
            placed++;

            int base = placement.get().base();
            for (int j = -reach; j <= reach; j++) {
                for (int[] column : new int[][]{{j, -reach}, {j, reach}, {-reach, j}, {reach, j}}) {
                    int top = ground.applyAsInt(column[0], column[1]);
                    if (base >= top) {
                        problems.add("size " + maxHeight + " base at " + base + " stands at or above the ground (" + top + ") at " + column[0] + ", " + column[1]);
                    }
                }
            }
        }

        if (placed < 250) {
            problems.add("only " + placed + " of 500 gentle terrains took a pyramid");
        }

        helper.assertTrue(problems.isEmpty(), "pyramid placement: " + String.join("; ", problems.subList(0, Math.min(8, problems.size()))));
        helper.succeed();
    }
}
