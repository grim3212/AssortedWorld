package com.grim3212.assorted.world.common.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * One stray sapling, or one stump left where a tree was, in a wood that suits the biome it stands
 * in. The scatter into a patch is the placement's job; this only decides what the block is.
 */
public class WoodPatchFeature extends Feature<NoneFeatureConfiguration> {

    private final boolean stump;

    public WoodPatchFeature(Codec<NoneFeatureConfiguration> codec, boolean stump) {
        super(codec);
        this.stump = stump;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();

        BiomeWoods wood = BiomeWoods.pick(level.getBiome(pos), context.random());
        BlockState state = this.stump ? wood.stump() : wood.sapling();

        // A stump is a log and stands anywhere; a sapling has to have soil it accepts under it.
        if (!this.stump && !state.canSurvive(level, pos)) {
            return false;
        }

        level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        return true;
    }
}
