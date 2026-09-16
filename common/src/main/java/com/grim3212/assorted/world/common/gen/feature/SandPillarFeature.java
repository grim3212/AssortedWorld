package com.grim3212.assorted.world.common.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A sandstone pillar standing out of the desert sand, a few blocks thick and tapering to a single
 * column at the top so it does not read as a wall.
 */
public class SandPillarFeature extends Feature<NoneFeatureConfiguration> {

    private static final int MIN_HEIGHT = 4;
    private static final int HEIGHT_VARIANCE = 18;

    public SandPillarFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos base = context.origin();

        if (!level.getBlockState(base.below()).is(Blocks.SAND) || !level.isEmptyBlock(base)) {
            return false;
        }

        int height = MIN_HEIGHT + random.nextInt(HEIGHT_VARIANCE);
        if (base.getY() + height + 1 >= level.getMaxY()) {
            return false;
        }

        int radius = random.nextInt(2) + 1;

        for (int y = 0; y < height; y++) {
            // Sheds its outer ring evenly over the climb, so the base is radius wide and the last
            // few blocks are a single column. (radius + 1) rather than radius is what gets it all
            // the way down to 0 by the top.
            int ring = Math.max(0, radius - ((y * (radius + 1)) / height));

            for (int x = -ring; x <= ring; x++) {
                for (int z = -ring; z <= ring; z++) {
                    if ((x * x) + (z * z) > ring * ring) {
                        continue;
                    }

                    BlockPos at = base.offset(x, y, z);
                    if (level.isEmptyBlock(at) || level.getBlockState(at).is(Blocks.SAND)) {
                        level.setBlock(at, Blocks.SANDSTONE.defaultBlockState(), Block.UPDATE_CLIENTS);
                    }
                }
            }
        }

        return true;
    }
}
