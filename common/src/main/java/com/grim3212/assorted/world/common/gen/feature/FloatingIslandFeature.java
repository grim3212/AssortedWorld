package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.WorldCommonMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * An island hanging in the air above the surface. Each one rolls its own {@link FloatingIslandType},
 * so what is floating over a forest may be desert, snow or mycelium.
 * <p>
 * GrimPack made these by lifting a bowl of terrain into the sky and leaving the crater behind. This
 * builds a new island instead and leaves the ground alone: a feature may only write to the chunk it
 * is decorating and its neighbours, and scooping the ground meant rewriting terrain well outside
 * that.
 */
public class FloatingIslandFeature extends Feature<NoneFeatureConfiguration> {

    /** Every island is at least this wide; the config adds the variation on top. */
    private static final int BASE_RADIUS = 7;

    /** How far above the surface an island floats. */
    private static final int MIN_CLEARANCE = 20;
    private static final int CLEARANCE_VARIANCE = 25;

    /** One in this many surface blocks grows something. */
    private static final int DECORATION_RATE = 5;

    public FloatingIslandFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();

        int radius = BASE_RADIUS + random.nextInt(WorldCommonMod.COMMON_CONFIG.floatingIslandSizeVariance.get());
        BlockPos top = context.origin().above(MIN_CLEARANCE + random.nextInt(CLEARANCE_VARIANCE));

        // Room for what grows on top as well as for the island, and clear air where it goes.
        if (top.getY() + 2 >= level.getMaxY() || !isClear(level, top, radius)) {
            return false;
        }

        FloatingIslandType type = FloatingIslandType.random(random);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt((x * x) + (z * z));
                if (distance > radius) {
                    continue;
                }

                // A lens: full depth under the middle, tapering to the rim, with the rim itself
                // roughed up so the underside is not a smooth cone.
                int depth = (int) Math.round((1.0D - (distance / radius)) * radius);
                if (depth < 1) {
                    depth = random.nextInt(2);
                }

                for (int y = 0; y < depth; y++) {
                    level.setBlock(top.offset(x, -y, z), layer(y, depth, type), Block.UPDATE_CLIENTS);
                }

                if (depth > 0) {
                    decorate(level, random, top.offset(x, 1, z), type);
                }
            }
        }

        return true;
    }

    /** Cover on top, a little filler under it, then the island's core the rest of the way down. */
    private static BlockState layer(int y, int depth, FloatingIslandType type) {
        if (y == 0) {
            return type.cover();
        }
        return y <= Math.min(3, depth / 2) ? type.filler() : type.core();
    }

    private static void decorate(WorldGenLevel level, RandomSource random, BlockPos pos, FloatingIslandType type) {
        if (random.nextInt(DECORATION_RATE) != 0) {
            return;
        }

        BlockState decoration = type.decoration(random);

        // canSurvive keeps a cactus off sand it would break on and a sapling off snow, without this
        // needing to know which pairings those are.
        if (decoration != null && decoration.canSurvive(level, pos)) {
            level.setBlock(pos, decoration, Block.UPDATE_CLIENTS);
        }
    }

    /**
     * Nothing already in the island's box, and nothing under it reaching up into where it goes.
     * Checked before any block is placed so a rejected island leaves no half of itself behind.
     */
    private static boolean isClear(WorldGenLevel level, BlockPos top, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if ((x * x) + (z * z) > radius * radius) {
                    continue;
                }

                // The heightmap rather than a block walk: a column whose surface reaches into the
                // island's box is a mountainside, and an island buried in one is the crater bug in
                // another form.
                if (level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, top.getX() + x, top.getZ() + z) > top.getY() - radius) {
                    return false;
                }
            }
        }

        return true;
    }
}
