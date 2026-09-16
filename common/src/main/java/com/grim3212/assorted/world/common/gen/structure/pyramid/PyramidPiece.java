package com.grim3212.assorted.world.common.gen.structure.pyramid;

import com.google.common.collect.Lists;
import com.grim3212.assorted.world.api.WorldLootTables;
import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.grim3212.assorted.world.common.util.RuinUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PyramidPiece extends ScatteredFeaturePiece {

    /** {@link #baseY} of a piece saved before the height was chosen when the structure is planned. */
    private static final int LEGACY_BASE = Integer.MIN_VALUE;

    private final int maxHeight;
    private final int type;
    private final int runeIndex;

    /** The height of the base course, chosen by {@link PyramidStructure#placement}. */
    private final int baseY;

    private List<BlockPos> placedSpawners;
    private List<BlockPos> placedChests;

    /**
     * @param corner the box's low corner in x and z
     * @param baseY  the base course's height; the box runs from the floor below it to the tip
     */
    public PyramidPiece(RandomSource random, BlockPos corner, int baseY, int maxHeight, int type) {
        super(WorldStructures.PYRAMID_STRUCTURE_PIECE.get(), corner.getX(), baseY - 1, corner.getZ(), maxHeight * 2, maxHeight + 2, maxHeight * 2, getRandomHorizontalDirection(random));
        this.maxHeight = maxHeight;
        this.type = type;
        this.baseY = baseY;
        this.runeIndex = RuinUtil.randomRuneIndex(random);
        this.placedSpawners = Lists.newArrayList();
        this.placedChests = Lists.newArrayList();
    }

    public PyramidPiece(StructurePieceSerializationContext context, CompoundTag tagCompound) {
        super(WorldStructures.PYRAMID_STRUCTURE_PIECE.get(), tagCompound);
        this.maxHeight = tagCompound.getIntOr("maxHeight", 0);
        this.type = tagCompound.getIntOr("type", 0);
        this.runeIndex = tagCompound.getIntOr("runeIndex", 0);
        this.baseY = tagCompound.getIntOr("baseY", LEGACY_BASE);

        // Not read back any more: postProcess regenerates them from a position-seeded source, so
        // they are working state for one pass rather than something that has to survive a reload.
        this.placedSpawners = Lists.newArrayList();
        this.placedChests = Lists.newArrayList();
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
        super.addAdditionalSaveData(context, tagCompound);
        tagCompound.putInt("maxHeight", this.maxHeight);
        tagCompound.putInt("type", this.type);
        tagCompound.putInt("runeIndex", this.runeIndex);
        if (this.baseY != LEGACY_BASE) {
            tagCompound.putInt("baseY", this.baseY);
        }
    }

    @Override
    public void postProcess(WorldGenLevel reader, StructureManager structureManager, ChunkGenerator generator, RandomSource rand, BoundingBox bb, ChunkPos chunkPos, BlockPos pos) {
        BlockPos offSetPos;
        if (this.baseY != LEGACY_BASE) {
            BlockPos centre = this.getBoundingBox().getCenter();
            offSetPos = new BlockPos(centre.getX(), this.baseY, centre.getZ());
        } else {
            // A pyramid saved before its height was chosen up front finishes the way it started,
            // from the average ground of the first chunk it generated in, so the halves agree.
            if (!this.updateAverageGroundHeight(reader, bb, 0)) {
                return;
            }
            // From the box, and only after updateAverageGroundHeight above has moved it: the pos
            // argument is read before postProcess runs, so it carries the pre-move height on the
            // first pass and the post-move height on every later one.
            offSetPos = RuinUtil.pieceOrigin(this.getBoundingBox()).below(maxHeight / 2);
        }

        Map<BlockPos, Block> blockCache = new HashMap<>();

        int halfWidth = halfWidth(maxHeight);
        int colHeight = 0;

        // Every pass works the whole pyramid out again, from a source that depends only on
        // where it stands. The spawner and chest positions used to be generated once and
        // carried in NBT precisely because a second pass would have re-rolled them differently;
        // making the passes agree is what lets each of them write only its own chunk.
        RandomSource pieceRandom = RuinUtil.pieceRandom(reader, this.getBoundingBox());
        this.placedSpawners.clear();
        this.placedChests.clear();

        BlockPos newPos;
        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int z = -halfWidth; z <= halfWidth; z++) {
                colHeight = getColumnHeight(x, z);
                for (int y = -1; y <= colHeight; y++) {
                    newPos = new BlockPos(x, y, z);

                    blockCache.put(offSetPos.offset(newPos), blockToPlace(pieceRandom, newPos, colHeight));
                }
            }
        }

        // Written outside the triple loop (a size 38 pyramid went from ~16s to ~2s), and only
        // inside the chunk being generated: writing further is an unsafe terrain read.
        blockCache.forEach((p, b) -> {
            if (bb.isInside(p)) {
                setBlockState(reader, p, b.defaultBlockState(), rand);
            }
        });

        // Like vanilla's desert pyramid, fill down under the floor until it meets ground. The
        // height is chosen from the terrain before caves and ravines are carved, and one cut under
        // the footprint afterwards would otherwise leave the pyramid hanging over a hole.
        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int z = -halfWidth; z <= halfWidth; z++) {
                BlockPos below = offSetPos.offset(x, -2, z);
                if (!bb.isInside(below)) {
                    continue;
                }
                while (below.getY() > reader.getMinY() && isHollow(reader.getBlockState(below))) {
                    reader.setBlock(below, Blocks.SANDSTONE.defaultBlockState(), 2);
                    below = below.below();
                }
            }
        }
    }

    /** Air, fluid, or a plant or snow layer a solid block may take the place of. */
    private static boolean isHollow(BlockState state) {
        return state.isAir() || !state.getFluidState().isEmpty() || state.canBeReplaced();
    }

    private void setBlockState(WorldGenLevel world, BlockPos p, BlockState s, RandomSource rand) {
        world.setBlock(p, s, 2);

        if (s.getBlock() == Blocks.CHEST) {
            BlockEntity te = world.getBlockEntity(p);

            if (te instanceof ChestBlockEntity) {
                ((ChestBlockEntity) te).setLootTable(WorldLootTables.CHESTS_PYRAMID, rand.nextLong());
            }

        } else if (s.getBlock() == Blocks.SUSPICIOUS_SAND) {
            world.getBlockEntity(p, BlockEntityTypes.BRUSHABLE_BLOCK).ifPresent(sand -> sand.setLootTable(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY, p.asLong()));

        } else if (s.getBlock() == Blocks.SPAWNER) {
            BlockEntity te = world.getBlockEntity(p);

            if (te instanceof SpawnerBlockEntity) {
                EntityType<?> type = RuinUtil.getRandomRuneMob(rand);
                if (type == null) {
                    type = EntityTypes.ZOMBIE;
                }
                ((SpawnerBlockEntity) te).setEntityId(type, rand);
            }
        }
    }

    private Block blockToPlace(RandomSource random, BlockPos pos, int colHeight) {
        // Exactly one rune per pyramid, at the centre of the base course regardless of size.
        if (pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0) {
            return RuinUtil.runeAt(this.runeIndex);
        }
        if (suspiciousSand(random, pos)) {
            return Blocks.SUSPICIOUS_SAND;
        }
        if (placeStone(random, pos, colHeight)) {
            if (type == 1) {
                return Blocks.SANDSTONE;
            }
            if (random.nextInt(10) == 2) {
                return Blocks.SAND;
            } else {
                return Blocks.SANDSTONE;
            }
        }
        if (placeSpawner(random, pos, colHeight)) {
            return Blocks.SPAWNER;
        }
        if (placeChest(random, pos, colHeight)) {
            return Blocks.CHEST;
        } else {
            return Blocks.AIR;
        }
    }

    /**
     * Suspicious sand worked into the floor around the rune, about six of the 24 blocks in the 5x5
     * it sits in - vanilla's desert pyramid scatters 5 to 7 of its own.
     * <p>
     * The floor course rather than the one the rune stands on: that one is open, and a brushable
     * block is {@code Fallable}, so an unsupported one falls and takes its sherd with it.
     */
    public static boolean suspiciousSand(RandomSource random, BlockPos pos) {
        if (pos.getY() != -1 || Math.max(Math.abs(pos.getX()), Math.abs(pos.getZ())) > 2) {
            return false;
        }

        // Straight under the rune stays sandstone, so the rune is not sitting on something brushable.
        return !(pos.getX() == 0 && pos.getZ() == 0) && random.nextInt(4) == 0;
    }

    private boolean placeStone(RandomSource random, BlockPos pos, int colHeight) {
        int y = pos.getY();

        if (y == -1) {
            return true;
        }

        if ((y % 6) == 0 && y > 4) {
            if (type == 1) {
                return random.nextInt(100) < 95;
            } else {
                return random.nextInt(14) < 11;
            }
        }

        if (colHeight - y < 3) {
            if (type == 1) {
                return random.nextInt(100) < 85;
            } else {
                return random.nextInt(5) < 4;
            }
        }

        if (Math.max(Math.abs(pos.getX()), Math.abs(pos.getZ())) % 8 == 0) {
            if (type == 1) {
                return random.nextInt(100) < 92;
            } else {
                return random.nextInt(3) < 2;
            }
        }

        return false;
    }

    private boolean placeSpawner(RandomSource random, BlockPos pos, int colHeight) {
        if (pos.getY() == 0 && placedSpawners.size() < maxHeight / 3) {
            if (random.nextInt(98) < 2) {
                boolean flag = false;
                for (int idx = 0; idx < placedSpawners.size(); idx++) {
                    int distance = (int) Math.round(Math.sqrt(placedSpawners.get(idx).distSqr(pos.above(pos.getY()))));
                    // Each spawner should not be within 6 blocks of each other
                    if (distance < 6) {
                        flag = true;
                    }
                }

                if (!flag) {
                    placedSpawners.add(pos.above(pos.getY()));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean placeChest(RandomSource random, BlockPos pos, int colHeight) {
        if (pos.getY() == 0 && placedChests.size() < placedSpawners.size() * 2 && random.nextInt(28) < 3) {
            boolean flag = false;
            for (int idx = 0; idx < placedChests.size(); idx++) {
                int distance = (int) Math.round(Math.sqrt(placedChests.get(idx).distSqr(pos.above(pos.getY()))));
                // Each spawner should not be within 3 blocks of each other
                if (distance < 3) {
                    flag = true;
                }
            }

            if (!flag) {
                placedChests.add(pos.above(pos.getY()));
                return true;
            }
        }
        return false;
    }

    private int getColumnHeight(int x, int z) {
        return maxHeight - (Math.max(Math.abs(x), Math.abs(z)) / 2) * 2;
    }

    public static int halfWidth(int height) {
        return 2 * (height / 2) - 1;
    }
}