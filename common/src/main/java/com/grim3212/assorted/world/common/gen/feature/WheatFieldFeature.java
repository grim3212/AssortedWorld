package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.WorldCommonMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A patch of wheat growing wild: farmland cut into the grass with wheat at mixed ages on top. The
 * farmland is left moist so the crop does not dry back out before anyone finds it.
 */
public class WheatFieldFeature extends Feature<NoneFeatureConfiguration> {

    public WheatFieldFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        int radius = WorldCommonMod.COMMON_CONFIG.wheatFieldSize.get();
        int planted = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if ((x * x) + (z * z) > radius * radius) {
                    continue;
                }

                // Per column rather than off the origin's height: a field on a rise follows the
                // ground instead of hanging off it.
                BlockPos pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, origin.offset(x, 0, z));
                if (!level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) || !level.isEmptyBlock(pos)) {
                    continue;
                }

                level.setBlock(pos.below(), Blocks.FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, FarmlandBlock.MAX_MOISTURE), Block.UPDATE_CLIENTS);
                level.setBlock(pos, Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, random.nextInt(CropBlock.MAX_AGE + 1)), Block.UPDATE_CLIENTS);
                planted++;
            }
        }

        return planted > 0;
    }
}
