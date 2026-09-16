package com.grim3212.assorted.world.gametest;

import com.grim3212.assorted.world.common.gen.structure.waterdome.WaterDomePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.RandomSource;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * What a water dome stands on its floor: the loot chests, and the suspicious gravel beside them.
 */
final class WaterDomeTests {

    private WaterDomeTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("water_dome_floor_offsets_never_collide", WaterDomeTests::waterDomeFloorOffsetsNeverCollide);
    }

    /**
     * Chests and gravel are rolled into columns of the same lobe, so the gravel roll has to see what
     * the chest roll already took: a shared column would place one over the other and lose a chest.
     * The centre column is the rune's and is never either.
     */
    private static void waterDomeFloorOffsetsNeverCollide(GameTestHelper helper) {
        List<String> problems = new ArrayList<>();
        RandomSource random = RandomSource.create(11L);

        for (int radius = 3; radius <= 10; radius++) {
            int maxOffset = Math.max(1, radius - 2);

            for (int attempt = 0; attempt < 200; attempt++) {
                List<BlockPos> chests = WaterDomePiece.rollFloorOffsets(random, radius, 1 + random.nextInt(2), List.of());
                List<BlockPos> gravel = WaterDomePiece.rollFloorOffsets(random, radius, random.nextInt(6), chests);

                for (BlockPos at : gravel) {
                    if (chests.contains(at)) {
                        problems.add("radius " + radius + " put gravel in a chest's column at " + at);
                    }
                }

                for (BlockPos at : concat(chests, gravel)) {
                    if (at.getX() == 0 && at.getZ() == 0) {
                        problems.add("radius " + radius + " used the rune's own column");
                    }
                    if ((at.getX() * at.getX()) + (at.getZ() * at.getZ()) > maxOffset * maxOffset) {
                        problems.add("radius " + radius + " put " + at + " in the dome wall");
                    }
                }

                if (chests.size() != chests.stream().distinct().count() || gravel.size() != gravel.stream().distinct().count()) {
                    problems.add("radius " + radius + " rolled the same column twice");
                }
            }
        }

        helper.assertTrue(problems.isEmpty(), "water dome floor: " + String.join("; ", problems));
        helper.succeed();
    }

    private static List<BlockPos> concat(List<BlockPos> first, List<BlockPos> second) {
        List<BlockPos> both = new ArrayList<>(first);
        both.addAll(second);
        return both;
    }
}
