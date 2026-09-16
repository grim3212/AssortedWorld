package com.grim3212.assorted.world.common.block;

import com.grim3212.assorted.world.WorldCommonMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

/**
 * A seed that hangs from a netherrack ceiling and ripens into a blob of glowstone. It grows through
 * {@link #STEP} 0-8 on random ticks and then hands the spot to vanilla's glowstone blob feature, so
 * what it leaves behind is shaped like the Nether's own.
 */
public class GlowstoneSeedBlock extends Block {

    public static final IntegerProperty STEP = IntegerProperty.create("step", 0, 8);

    /** The step a seed ripens at, after which it becomes glowstone. */
    public static final int RIPE = 8;

    /** One in this many random ticks advances a step, so a seed takes a while to ripen. */
    private static final int GROWTH_RATE = 10;

    private static final VoxelShape SHAPE = Block.column(16.0D, 0.0D, 16.0D);

    public GlowstoneSeedBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(STEP, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STEP);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     * Netherrack overhead, and under a sky only low enough that seeds stay something you plant in a
     * cave rather than on a surface farm. {@code hasCeiling} is the Nether-shaped test that survived
     * 26.2 dropping {@code ultraWarm}, and it is the right one anyway: a seed hangs from a ceiling.
     */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (!level.getBlockState(pos.above()).is(Blocks.NETHERRACK)) {
            return false;
        }

        return level.dimensionType().hasCeiling() || pos.getY() <= WorldCommonMod.COMMON_CONFIG.glowstoneSeedPlantHeight.get();
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int step = state.getValue(STEP);

        if (step < RIPE) {
            if (random.nextInt(GROWTH_RATE) == 0) {
                level.setBlock(pos, state.setValue(STEP, step + 1), Block.UPDATE_CLIENTS);
            }
            return;
        }

        // The blob feature has to find the spot empty: it only starts from an air block under
        // netherrack, which is exactly where the ripe seed is standing.
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        Feature.GLOWSTONE_BLOB.place(new FeaturePlaceContext<>(Optional.empty(), level, level.getChunkSource().getGenerator(), random, pos, NoneFeatureConfiguration.INSTANCE));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return type == PathComputationType.AIR && !this.hasCollision || super.isPathfindable(state, type);
    }
}
