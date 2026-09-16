package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.WorldCommonMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A funnel dug into the desert in stepped terraces, deepest in the middle. The floor of each terrace
 * is sand, and anything left hanging over it is cleared away.
 */
public class SandPitFeature extends Feature<NoneFeatureConfiguration> {

    /** How far each terrace drops below the one outside it. */
    private static final int STEP = 3;

    public SandPitFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        int radius = WorldCommonMod.COMMON_CONFIG.sandPitSize.get();

        // The middle terrace, two blocks of radius per terrace.
        int deepest = radius / 2;

        // Room for the floor plus the sand that closes whatever it cut into, so a pit never digs out
        // through the bottom of the world.
        if (origin.getY() - (deepest * STEP) - 4 <= level.getMinY() || !onSand(level, origin, radius)) {
            return false;
        }

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Square terraces stepping in from the rim, so the middle is the deepest. The
                // outermost ring comes out 0 and is left as ground, which is the pit's lip.
                int terrace = (radius - Math.max(Math.abs(x), Math.abs(z))) / 2;
                if (terrace == 0) {
                    continue;
                }

                BlockPos column = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, origin.offset(x, 0, z));
                BlockPos floor = column.below(terrace * STEP);

                clearAbove(level, floor.above(), column.getY());
                fillBelow(level, floor);
            }
        }

        return true;
    }

    /** Hollows the terrace out, up to where the ground used to be. */
    private static void clearAbove(WorldGenLevel level, BlockPos from, int surfaceY) {
        for (BlockPos at = from; at.getY() <= surfaceY; at = at.above()) {
            level.setBlock(at, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    /**
     * Sand for the floor and enough under it to close any cave the pit has cut into, so a pit does
     * not open onto a hole.
     */
    private static void fillBelow(WorldGenLevel level, BlockPos floor) {
        level.setBlock(floor, Blocks.SAND.defaultBlockState(), Block.UPDATE_CLIENTS);

        for (int y = 1; y <= 3; y++) {
            BlockPos at = floor.below(y);
            if (level.isEmptyBlock(at)) {
                level.setBlock(at, Blocks.SAND.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    /** Sand across the whole footprint, so a pit is not cut half out of a cliff. */
    private static boolean onSand(WorldGenLevel level, BlockPos origin, int radius) {
        for (int x = -radius; x <= radius; x += radius) {
            for (int z = -radius; z <= radius; z += radius) {
                BlockPos column = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, origin.offset(x, 0, z));
                if (!level.getBlockState(column.below()).is(Blocks.SAND)) {
                    return false;
                }
            }
        }

        return true;
    }
}
