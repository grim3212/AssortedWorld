package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.api.WorldLootTables;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;

/**
 * A desert well with a water shaft under it and a chest at the bottom. The shaft is 10, 15, 20, 25
 * or 30 blocks deep and the loot climbs with the depth, so how far down you are willing to swim is
 * what decides the payout.
 * <p>
 * Where these generate, vanilla's own desert well is taken out unless the config keeps it.
 */
public class DesertWellFeature extends Feature<NoneFeatureConfiguration> {

    /** Depth tiers, in blocks. Each has its own loot table, in the same order. */
    private static final List<Integer> DEPTHS = List.of(10, 15, 20, 25, 30);

    private static final List<ResourceKey<LootTable>> LOOT = List.of(WorldLootTables.CHESTS_DESERT_WELL_10, WorldLootTables.CHESTS_DESERT_WELL_15, WorldLootTables.CHESTS_DESERT_WELL_20, WorldLootTables.CHESTS_DESERT_WELL_25, WorldLootTables.CHESTS_DESERT_WELL_30);

    /** Casing blocks that face the water, as offsets from the shaft's centre column. */
    private static final List<BlockPos> SHAFT_WALL = List.of(new BlockPos(1, 0, 1), new BlockPos(1, 0, -1), new BlockPos(-1, 0, 1), new BlockPos(-1, 0, -1),
            new BlockPos(2, 0, 0), new BlockPos(-2, 0, 0), new BlockPos(0, 0, 2), new BlockPos(0, 0, -2));

    public DesertWellFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        int tier = random.nextInt(DEPTHS.size());
        int depth = DEPTHS.get(tier);

        // Sand all the way around the rim, so a well never ends up half over a cliff, and room for
        // the shaft plus the chest chamber under it.
        if (origin.getY() - depth - 2 <= level.getMinY() || !onSand(level, origin) || !isOpen(level, origin)) {
            return false;
        }

        BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
        BlockState slab = Blocks.SANDSTONE_SLAB.defaultBlockState();
        BlockState water = Blocks.WATER.defaultBlockState();

        BlockPos base = origin.below();

        // The pad the well stands on, two blocks thick so the shaft has walls from the start.
        for (int y = 0; y >= -1; y--) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    level.setBlock(base.offset(x, y, z), sandstone, Block.UPDATE_CLIENTS);
                }
            }
        }

        // The shaft: a plus-shaped column of water with a sandstone casing around it.
        for (int y = 0; y < depth; y++) {
            BlockPos at = base.below(y);
            level.setBlock(at, water, Block.UPDATE_CLIENTS);
            for (Direction side : Direction.Plane.HORIZONTAL) {
                level.setBlock(at.relative(side), water, Block.UPDATE_CLIENTS);
            }

            if (y == 0) {
                continue;
            }

            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    // Everything in the 5x5 that is not the plus of water: the corners of the inner
                    // ring and the whole outer ring.
                    if (Math.abs(x) + Math.abs(z) > 1) {
                        level.setBlock(at.offset(x, 0, z), sandstone, Block.UPDATE_CLIENTS);
                    }
                }
            }
        }

        placeChest(level, random, base.below(depth), sandstone, LOOT.get(tier));

        // Vanilla's well is the only source of some pottery sherds, so these carry its suspicious
        // sand too: two in the shaft wall just under the water, where vanilla puts them.
        for (int i = 0; i < 2; i++) {
            BlockPos wall = SHAFT_WALL.get(random.nextInt(SHAFT_WALL.size()));
            placeSuspiciousSand(level, base.offset(wall.getX(), -1 - i, wall.getZ()));
        }

        // The rim above ground, with a slab in the middle of each side to step over.
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    level.setBlock(origin.offset(x, 0, z), sandstone, Block.UPDATE_CLIENTS);
                }
            }
        }
        for (Direction side : Direction.Plane.HORIZONTAL) {
            level.setBlock(origin.relative(side, 2), slab, Block.UPDATE_CLIENTS);
        }

        // Four corner pillars holding up a slab roof.
        for (int y = 0; y <= 2; y++) {
            for (int x = -1; x <= 1; x += 2) {
                for (int z = -1; z <= 1; z += 2) {
                    level.setBlock(origin.offset(x, y, z), sandstone, Block.UPDATE_CLIENTS);
                }
            }
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                level.setBlock(origin.offset(x, 3, z), x == 0 && z == 0 ? sandstone : slab, Block.UPDATE_CLIENTS);
            }
        }

        return true;
    }

    private static void placeSuspiciousSand(WorldGenLevel level, BlockPos pos) {
        level.setBlock(pos, Blocks.SUSPICIOUS_SAND.defaultBlockState(), Block.UPDATE_ALL);
        level.getBlockEntity(pos, BlockEntityTypes.BRUSHABLE_BLOCK).ifPresent(sand -> sand.setLootTable(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY, pos.asLong()));
    }

    /** A chest at the bottom of the shaft, walled in so the water above does not pour past it. */
    private static void placeChest(WorldGenLevel level, RandomSource random, BlockPos pos, BlockState sandstone, ResourceKey<LootTable> loot) {
        level.setBlock(pos.below(), sandstone, Block.UPDATE_CLIENTS);
        for (Direction side : Direction.Plane.HORIZONTAL) {
            level.setBlock(pos.relative(side), sandstone, Block.UPDATE_CLIENTS);
        }

        level.setBlock(pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH), Block.UPDATE_CLIENTS);
        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            chest.setLootTable(loot, random.nextLong());
        }
    }

    /**
     * Nothing but air, plants or dune sand where the rim, pillars and roof go, so a well never
     * builds into a pillar, a temple or another well.
     */
    private static boolean isOpen(WorldGenLevel level, BlockPos origin) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 0; y <= 4; y++) {
                    BlockState state = level.getBlockState(origin.offset(x, y, z));
                    if (!state.isAir() && !state.canBeReplaced() && !state.is(Blocks.SAND)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /** Sand under the whole 5x5 the well sits on. */
    private static boolean onSand(WorldGenLevel level, BlockPos origin) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (!level.getBlockState(origin.offset(x, -1, z)).is(Blocks.SAND)) {
                    return false;
                }
            }
        }

        return true;
    }
}
