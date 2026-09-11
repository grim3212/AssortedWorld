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
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PyramidPiece extends ScatteredFeaturePiece {

    private final int maxHeight;
    private final int type;
    private final int runeIndex;

    private List<BlockPos> placedSpawners;
    private List<BlockPos> placedChests;

    public PyramidPiece(RandomSource random, BlockPos pos, int maxHeight, int type) {
        super(WorldStructures.PYRAMID_STRUCTURE_PIECE.get(), pos.getX(), pos.getY() - 1 - maxHeight, pos.getZ(), maxHeight * 2, maxHeight * 2 + 1, maxHeight * 2, getRandomHorizontalDirection(random));
        this.maxHeight = maxHeight;
        this.type = type;
        this.runeIndex = RuinUtil.randomRuneIndex(random);
        this.placedSpawners = Lists.newArrayList();
        this.placedChests = Lists.newArrayList();
    }

    public PyramidPiece(StructurePieceSerializationContext context, CompoundTag tagCompound) {
        super(WorldStructures.PYRAMID_STRUCTURE_PIECE.get(), tagCompound);
        this.maxHeight = tagCompound.getIntOr("maxHeight", 0);
        this.type = tagCompound.getIntOr("type", 0);
        this.runeIndex = tagCompound.getIntOr("runeIndex", 0);

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
    }

    @Override
    public void postProcess(WorldGenLevel reader, StructureManager structureManager, ChunkGenerator generator, RandomSource rand, BoundingBox bb, ChunkPos chunkPos, BlockPos pos) {
        if (this.updateAverageGroundHeight(reader, bb, 0)) {
            Map<BlockPos, Block> blockCache = new HashMap<>();

            // From the box, and only after updateAverageGroundHeight above has moved it: the pos
            // argument is read before postProcess runs, so it carries the pre-move height on the
            // first pass and the post-move height on every later one.
            BlockPos offSetPos = RuinUtil.pieceOrigin(this.getBoundingBox()).below(maxHeight / 2);

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
            // inside the
            // chunk being generated: writing further is an unsafe terrain read.
            blockCache.forEach((p, b) -> {
                if (bb.isInside(p)) {
                    setBlockState(reader, p, b.defaultBlockState(), rand);
                }
            });
        }
    }

    private void setBlockState(WorldGenLevel world, BlockPos p, BlockState s, RandomSource rand) {
        world.setBlock(p, s, 2);

        if (s.getBlock() == Blocks.CHEST) {
            BlockEntity te = world.getBlockEntity(p);

            if (te instanceof ChestBlockEntity) {
                ((ChestBlockEntity) te).setLootTable(WorldLootTables.CHESTS_PYRAMID, rand.nextLong());
            }

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