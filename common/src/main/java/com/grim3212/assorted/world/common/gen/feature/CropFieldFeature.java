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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A field of a crop growing wild: farmland cut into the grass with one of the overworld crops at
 * mixed ages on top, watered by holes dug into the field itself.
 * <p>
 * One crop per field, so a field reads as a field of something rather than a seed drawer emptied
 * over a hillside.
 */
public class CropFieldFeature extends Feature<NoneFeatureConfiguration> {

    /** The crops a wild field can be. Each carries its own age range, so beetroot tops out at 3. */
    private static final List<CropBlock> CROPS = List.of((CropBlock) Blocks.WHEAT, (CropBlock) Blocks.CARROTS, (CropBlock) Blocks.POTATOES, (CropBlock) Blocks.BEETROOTS);

    /**
     * How far {@code FarmlandBlock.isNearWater} looks, in blocks. Farmland further than this from
     * every water block dries out, so the watering pass works to the same reach.
     */
    private static final int WATER_REACH = 4;

    public CropFieldFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        // Keyed by column so the watering pass can look a neighbour up without walking the field.
        Map<Long, BlockPos> field = tillable(level, origin, WorldCommonMod.COMMON_CONFIG.cropFieldSize.get());
        if (field.isEmpty()) {
            return false;
        }

        Set<BlockPos> water = wateringHoles(field);
        CropBlock crop = CROPS.get(random.nextInt(CROPS.size()));

        for (BlockPos farmland : field.values()) {
            if (water.contains(farmland)) {
                // The hole is the water, and nothing grows in it.
                level.setBlock(farmland, Blocks.WATER.defaultBlockState(), Block.UPDATE_CLIENTS);
                continue;
            }

            BlockState moist = Blocks.FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, FarmlandBlock.MAX_MOISTURE);
            level.setBlock(farmland, moist, Block.UPDATE_CLIENTS);
            level.setBlock(farmland.above(), crop.getStateForAge(random.nextInt(crop.getMaxAge() + 1)), Block.UPDATE_CLIENTS);
        }

        return true;
    }

    /**
     * The blocks of the field that can be turned over: grass with air above it. The height is taken
     * per column rather than off the origin, so a field on a rise follows the ground instead of
     * hanging off it.
     */
    private static Map<Long, BlockPos> tillable(WorldGenLevel level, BlockPos origin, int radius) {
        Map<Long, BlockPos> field = new LinkedHashMap<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if ((x * x) + (z * z) > radius * radius) {
                    continue;
                }

                BlockPos pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, origin.offset(x, 0, z));
                if (level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) && level.isEmptyBlock(pos)) {
                    field.put(column(pos), pos.below());
                }
            }
        }

        return field;
    }

    /**
     * Where to dig the water in. Every farmland block has to be within {@link #WATER_REACH} of a
     * water block at its own level or one above, or it dries back out; a hole is only dug where the
     * field surrounds it at the same height, so the water sits in the field rather than running out
     * of it down a slope.
     */
    private static Set<BlockPos> wateringHoles(Map<Long, BlockPos> field) {
        List<BlockPos> dug = new ArrayList<>();

        for (BlockPos farmland : field.values()) {
            if (hydrated(farmland, dug)) {
                continue;
            }

            BlockPos hole = holeFor(farmland, field);
            if (hole != null) {
                dug.add(hole);
            }
        }

        return new HashSet<>(dug);
    }

    /**
     * A spot in reach of {@code farmland} the water will stay in, or null when the field is too
     * narrow or too steep around it to hold any - a dry corner keeps its crop either way, it just
     * stops growing.
     */
    private static BlockPos holeFor(BlockPos farmland, Map<Long, BlockPos> field) {
        for (int x = -WATER_REACH; x <= WATER_REACH; x++) {
            for (int z = -WATER_REACH; z <= WATER_REACH; z++) {
                BlockPos candidate = field.get(column(farmland.offset(x, 0, z)));
                if (candidate != null && hydrates(candidate, farmland) && enclosed(candidate, field)) {
                    return candidate;
                }
            }
        }

        return null;
    }

    private static boolean hydrated(BlockPos farmland, List<BlockPos> water) {
        return water.stream().anyMatch(at -> hydrates(at, farmland));
    }

    /** Matches what {@code FarmlandBlock.isNearWater} looks at: 4 blocks out, level or one up. */
    private static boolean hydrates(BlockPos water, BlockPos farmland) {
        int dy = water.getY() - farmland.getY();
        return Math.abs(water.getX() - farmland.getX()) <= WATER_REACH && Math.abs(water.getZ() - farmland.getZ()) <= WATER_REACH && dy >= 0 && dy <= 1;
    }

    /** Field on all four sides at the same height, so the water has nowhere to spill. */
    private static boolean enclosed(BlockPos pos, Map<Long, BlockPos> field) {
        for (BlockPos side : List.of(pos.north(), pos.south(), pos.east(), pos.west())) {
            if (!side.equals(field.get(column(side)))) {
                return false;
            }
        }
        return true;
    }

    /** The xz of a position as one key, so a column can be looked up without its height. */
    private static long column(BlockPos pos) {
        return ((long) pos.getX() << 32) | (pos.getZ() & 0xFFFFFFFFL);
    }
}
