package com.grim3212.assorted.world.common.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

/**
 * A sandstone column standing in the desert: a plinth sunk into the sand, a straight shaft and a
 * capital, in one of a few {@link Style styles}. On red sand it is built from red sandstone.
 * Some have fallen, leaving a broken stump and rubble.
 */
public class SandPillarFeature extends Feature<NoneFeatureConfiguration> {

    private static final int MIN_SHAFT = 4;
    private static final int SHAFT_VARIANCE = 11;

    /** How far uneven ground may fall away under the plinth before the pillar is not placed. */
    private static final int MAX_SLOPE = 2;

    /** How deep the plinth reaches down to find solid ground on a slope. */
    private static final int MAX_FOOTING = 4;

    public SandPillarFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    /** One family of sandstone blocks: plain, smooth, cut and chiseled, and their slabs. */
    private record Stone(BlockState plain, BlockState smooth, BlockState cut, BlockState chiseled, BlockState plainSlab, BlockState smoothSlab, BlockState cutSlab) {

        static Stone of(Block plain, Block smooth, Block cut, Block chiseled, Block plainSlab, Block smoothSlab, Block cutSlab) {
            return new Stone(plain.defaultBlockState(), smooth.defaultBlockState(), cut.defaultBlockState(), chiseled.defaultBlockState(), plainSlab.defaultBlockState(),
                    smoothSlab.defaultBlockState(), cutSlab.defaultBlockState());
        }
    }

    private static final Stone SANDSTONE = Stone.of(Blocks.SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE_SLAB,
            Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.CUT_SANDSTONE_SLAB);
    private static final Stone RED_SANDSTONE = Stone.of(Blocks.RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE,
            Blocks.RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE_SLAB);

    /** The look of a pillar. Each picks the blocks for its plinth, shaft, bands and capital. */
    private enum Style {
        /** Plain sandstone shaft between cut sandstone plinth and capital. */
        CLASSIC(4),
        /** Smooth shaft with chiseled bands and a smooth capital. */
        SMOOTH(3),
        /** Cut sandstone shaft ringed with chiseled sandstone every few blocks. */
        CARVED(3),
        /** A classic pillar that has fallen: a jagged stump and its rubble round the base. */
        RUINED(2);

        private static final List<Style> VALUES = List.of(values());
        private static final int TOTAL = VALUES.stream().mapToInt(style -> style.weight).sum();

        private final int weight;

        Style(int weight) {
            this.weight = weight;
        }

        static Style random(RandomSource random) {
            int roll = random.nextInt(TOTAL);
            for (Style style : VALUES) {
                roll -= style.weight;
                if (roll < 0) {
                    return style;
                }
            }
            return CLASSIC;
        }
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos base = context.origin();

        BlockState ground = level.getBlockState(base.below());
        Stone stone;
        if (ground.is(Blocks.SAND)) {
            stone = SANDSTONE;
        } else if (ground.is(Blocks.RED_SAND)) {
            stone = RED_SANDSTONE;
        } else {
            return false;
        }

        // A square shaft 1, 2 or 3 wide. min/max are offsets from the origin, so a 2 wide shaft is
        // not centred - which on a block grid it cannot be anyway.
        int width = 1 + random.nextInt(3);
        int min = width == 3 ? -1 : 0;
        int max = width == 1 ? 0 : 1;

        Style style = Style.random(random);
        int shaft = MIN_SHAFT + random.nextInt(SHAFT_VARIANCE) + width;
        int plinthHeight = width >= 2 ? 2 : 1;
        int capitalHeight = width >= 2 ? 2 : 1;
        int total = plinthHeight + shaft + capitalHeight;

        if (base.getY() + total + 1 > level.getMaxY() || !isOpenGround(level, base, min - 1, max + 1, total)) {
            return false;
        }

        // The plinth is one wider than the shaft all round and sits a block into the sand.
        BlockPos plinthBottom = base.below();
        for (int x = min - 1; x <= max + 1; x++) {
            for (int z = min - 1; z <= max + 1; z++) {
                footing(level, plinthBottom.offset(x, -1, z), stone.plain());
                for (int y = 0; y <= plinthHeight - 1; y++) {
                    level.setBlock(plinthBottom.offset(x, y, z), plinth(style, stone, y), Block.UPDATE_CLIENTS);
                }
            }
        }

        BlockPos shaftBottom = plinthBottom.above(plinthHeight);
        int standing = style == Style.RUINED ? 2 + random.nextInt(Math.max(1, shaft - 2)) : shaft;

        for (int y = 0; y < standing; y++) {
            for (int x = min; x <= max; x++) {
                for (int z = min; z <= max; z++) {
                    // A fallen pillar's top layer breaks off unevenly. Only the top one, so nothing
                    // is left hanging over a gap.
                    if (style == Style.RUINED && y == standing - 1 && random.nextInt(2) == 0) {
                        continue;
                    }
                    level.setBlock(shaftBottom.offset(x, y, z), shaftBlock(style, stone, y), Block.UPDATE_CLIENTS);
                }
            }
        }

        if (style == Style.RUINED) {
            scatterRubble(level, random, base, stone, min, max, width);
            return true;
        }

        BlockPos capitalBottom = shaftBottom.above(shaft);
        for (int y = 0; y < capitalHeight; y++) {
            // The lower capital layer is as wide as the plinth; a second one steps back in.
            int spread = y == 0 ? 1 : 0;
            for (int x = min - spread; x <= max + spread; x++) {
                for (int z = min - spread; z <= max + spread; z++) {
                    level.setBlock(capitalBottom.offset(x, y, z), capital(style, stone, y, capitalHeight), Block.UPDATE_CLIENTS);
                }
            }
        }

        // A slab on top of a one wide pillar reads as a finished column rather than a stick.
        if (width == 1) {
            BlockState slab = (style == Style.SMOOTH ? stone.smoothSlab() : stone.cutSlab()).setValue(SlabBlock.TYPE, SlabType.BOTTOM);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    level.setBlock(capitalBottom.offset(x, capitalHeight, z), slab, Block.UPDATE_CLIENTS);
                }
            }
        }

        return true;
    }

    private static BlockState plinth(Style style, Stone stone, int y) {
        return switch (style) {
            case SMOOTH -> y == 0 ? stone.smooth() : stone.chiseled();
            case CARVED -> stone.chiseled();
            default -> stone.cut();
        };
    }

    private static BlockState shaftBlock(Style style, Stone stone, int y) {
        return switch (style) {
            case SMOOTH -> y % 4 == 3 ? stone.chiseled() : stone.smooth();
            case CARVED -> y % 3 == 2 ? stone.chiseled() : stone.cut();
            default -> stone.plain();
        };
    }

    private static BlockState capital(Style style, Stone stone, int y, int height) {
        boolean top = y == height - 1;
        return switch (style) {
            case SMOOTH -> top ? stone.smooth() : stone.chiseled();
            case CARVED -> top ? stone.cut() : stone.chiseled();
            default -> top && height > 1 ? stone.chiseled() : stone.cut();
        };
    }

    /**
     * Fills down under the plinth until it meets something solid, so a pillar on a dune slope
     * stands on sandstone rather than hanging over a gap.
     */
    private static void footing(WorldGenLevel level, BlockPos top, BlockState state) {
        for (int y = 0; y < MAX_FOOTING; y++) {
            BlockPos at = top.below(y);
            BlockState existing = level.getBlockState(at);
            if (!existing.isAir() && existing.getFluidState().isEmpty()) {
                return;
            }
            level.setBlock(at, state, Block.UPDATE_CLIENTS);
        }
    }

    /** Blocks knocked off a fallen pillar, lying on the sand around its base. */
    private static void scatterRubble(WorldGenLevel level, RandomSource random, BlockPos base, Stone stone, int min, int max, int width) {
        int pieces = 2 + random.nextInt(2 + width);
        List<BlockState> rubble = List.of(stone.plain(), stone.cut(), stone.plainSlab().setValue(SlabBlock.TYPE, SlabType.BOTTOM));

        for (int i = 0; i < pieces; i++) {
            int x = min - 2 - random.nextInt(2) + random.nextInt(max - min + 6);
            int z = min - 2 - random.nextInt(2) + random.nextInt(max - min + 6);
            BlockPos at = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, base.offset(x, 0, z));
            BlockState below = level.getBlockState(at.below());
            if (level.isEmptyBlock(at) && (below.is(Blocks.SAND) || below.is(Blocks.RED_SAND))) {
                level.setBlock(at, rubble.get(random.nextInt(rubble.size())), Block.UPDATE_CLIENTS);
            }
        }
    }

    /**
     * Sand under the whole plinth, no more than {@link #MAX_SLOPE} blocks off level, and nothing
     * but air, plants or dune sand where the pillar goes - so a pillar never cuts into a well, a
     * temple or another pillar.
     */
    private static boolean isOpenGround(WorldGenLevel level, BlockPos base, int min, int max, int height) {
        for (int x = min; x <= max; x++) {
            for (int z = min; z <= max; z++) {
                int ground = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, base.getX() + x, base.getZ() + z);
                if (Math.abs(ground - base.getY()) > MAX_SLOPE) {
                    return false;
                }

                if (!isSand(level.getBlockState(new BlockPos(base.getX() + x, ground - 1, base.getZ() + z)))) {
                    return false;
                }

                for (int y = 0; y <= height; y++) {
                    BlockState state = level.getBlockState(base.offset(x, y, z));
                    if (!state.isAir() && !state.canBeReplaced() && !isSand(state)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean isSand(BlockState state) {
        return state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);
    }
}
