package com.grim3212.assorted.world.common.gen.structure.waterdome;

import com.google.common.collect.Lists;
import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.grim3212.assorted.world.common.util.RuinUtil;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class WaterDomePiece extends ScatteredFeaturePiece {

    private static final Codec<List<BlockPos>> BLOCK_POS_LIST_CODEC = BlockPos.CODEC.listOf();

    private final int radius;
    private final int xOffset;
    private final int zOffset;

    // Only the first piece of a dome carries the structure's single rune, at its own centre. Later
    // overlapping pieces cannot clear it again because they only replace water.
    private final boolean placesRune;
    private final int runeIndex;

    // Rolled once per dome so every piece is ribbed with the same material and every chest in the
    // dome draws from the same table.
    private final WaterDomeType domeType;

    // Local offsets on the piece's floor course that get a loot chest. Decided at construction and
    // serialised, because postProcess runs once per chunk the piece overlaps.
    private final List<BlockPos> chestOffsets;

    public WaterDomePiece(RandomSource random, BlockPos pos, int radius, int xOffset, int zOffset, boolean placesRune, int runeIndex, WaterDomeType domeType, int chestCount) {
        super(WorldStructures.WATER_DOME_STRUCTURE_PIECE.get(), pos.getX(), pos.getY(), pos.getZ(), (radius * 2) + 1, radius, (radius * 2) + 1, getRandomHorizontalDirection(random));
        this.radius = radius;
        this.xOffset = xOffset;
        this.zOffset = zOffset;
        this.placesRune = placesRune;
        this.runeIndex = runeIndex;
        this.domeType = domeType;
        this.chestOffsets = rollChestOffsets(random, radius, chestCount);
    }

    public WaterDomePiece(StructurePieceSerializationContext context, CompoundTag tagCompound) {
        super(WorldStructures.WATER_DOME_STRUCTURE_PIECE.get(), tagCompound);
        this.radius = tagCompound.getIntOr("radius", 0);
        this.xOffset = tagCompound.getIntOr("xOffset", 0);
        this.zOffset = tagCompound.getIntOr("zOffset", 0);
        this.placesRune = tagCompound.getBooleanOr("placesRune", false);
        this.runeIndex = tagCompound.getIntOr("runeIndex", 0);
        this.domeType = WaterDomeType.byOrdinal(tagCompound.getIntOr("domeType", 0));
        this.chestOffsets = tagCompound.read("chestOffsets", BLOCK_POS_LIST_CODEC).orElse(List.of());
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
        super.addAdditionalSaveData(context, tagCompound);
        tagCompound.putInt("radius", this.radius);
        tagCompound.putInt("xOffset", this.xOffset);
        tagCompound.putInt("zOffset", this.zOffset);
        tagCompound.putBoolean("placesRune", this.placesRune);
        tagCompound.putInt("runeIndex", this.runeIndex);
        tagCompound.putInt("domeType", this.domeType.ordinal());
        tagCompound.store("chestOffsets", BLOCK_POS_LIST_CODEC, this.chestOffsets);
    }

    @Override
    public void postProcess(WorldGenLevel reader, StructureManager structureManager, ChunkGenerator generator, RandomSource rand, BoundingBox bb, ChunkPos chunkPos, BlockPos pos) {
        BlockPos centerPoint = this.getLocatorPosition();
        int i = reader.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, centerPoint.getX(), centerPoint.getZ());

        // Get correct position
        pos = new BlockPos(pos.getX(), i, pos.getZ());

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    BlockPos newPoint = new BlockPos(x + xOffset, y, z + zOffset);

                    // Placed without the water check below: pos.y comes from this piece's own
                    // locator, so on a sloping seabed the dome's centre is not always water and the
                    // rune would be dropped.
                    if (this.placesRune && x == 0 && y == 0 && z == 0) {
                        reader.setBlock(pos.offset(newPoint), RuinUtil.runeAt(this.runeIndex).defaultBlockState(), 2);
                        continue;
                    }

                    Block b = blockToPlace(pos, newPoint, this.domeType);
                    if (b == null) {
                        continue;
                    }

                    Block curBlock = reader.getBlockState(pos.offset(newPoint)).getBlock();

                    // Chests stand where the dome is hollow, on its floor course. The pieces of a
                    // dome overlap and are processed in order, so by the time this one runs another
                    // may already have carved the spot: a chest replaces air as well as water,
                    // unlike the rest of the dome. It never replaces a chest, so a later pass over
                    // the same piece cannot re-roll loot a player has taken.
                    if (b == Blocks.AIR && this.chestOffsets.contains(new BlockPos(x, y, z))) {
                        if (curBlock == Blocks.AIR || isWater(reader, pos.offset(newPoint), curBlock)) {
                            setBlockState(reader, pos.offset(newPoint), Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, chestFacing()), rand);
                        }
                        continue;
                    }

                    if (isWater(reader, pos.offset(newPoint), curBlock)) {
                        setBlockState(reader, pos.offset(newPoint), b.defaultBlockState(), rand);
                    }
                }
            }
        }

    }

    /** The dome only ever eats water — anything already solid there is left as terrain. */
    private boolean isWater(WorldGenLevel reader, BlockPos pos, Block curBlock) {
        if (curBlock == Blocks.WATER) {
            return true;
        }

        FluidState state = reader.getFluidState(pos);
        return !state.isEmpty() && (state.getType() == Fluids.FLOWING_WATER || state.getType() == Fluids.WATER);
    }

    private void setBlockState(WorldGenLevel reader, BlockPos pos, BlockState state, RandomSource rand) {
        reader.setBlock(pos, state, 2);

        if (state.getBlock() == Blocks.CHEST && reader.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            chest.setLootTable(this.domeType.chestLoot(), rand.nextLong());
        }
    }

    /** The piece's own orientation, so a chest's facing does not shift between postProcess passes. */
    private Direction chestFacing() {
        Direction orientation = this.getOrientation();
        return orientation == null || orientation.getAxis().isVertical() ? Direction.NORTH : orientation;
    }

    /**
     * Picks distinct spots on the floor course (local y 0, the lowest water block above the seabed)
     * that sit well inside the shell, avoiding the centre column reserved for the rune.
     */
    private static List<BlockPos> rollChestOffsets(RandomSource random, int radius, int chestCount) {
        List<BlockPos> offsets = Lists.newArrayList();
        if (chestCount <= 0) {
            return offsets;
        }

        // Two blocks in from the shell, so a chest never ends up embedded in the dome wall.
        int maxOffset = Math.max(1, radius - 2);
        for (int idx = 0; idx < chestCount; idx++) {
            for (int attempt = 0; attempt < 16; attempt++) {
                int x = random.nextInt((maxOffset * 2) + 1) - maxOffset;
                int z = random.nextInt((maxOffset * 2) + 1) - maxOffset;

                // (0, 0) is the rune's column, and no two chests share a spot.
                if ((x == 0 && z == 0) || (x * x) + (z * z) > maxOffset * maxOffset) {
                    continue;
                }

                BlockPos offset = new BlockPos(x, 0, z);
                if (offsets.contains(offset)) {
                    continue;
                }

                offsets.add(offset);
                break;
            }
        }

        return offsets;
    }

    private Block blockToPlace(BlockPos pos, BlockPos point1, WaterDomeType type) {
        int blocks = 0;
        int places = 0;
        int equalPoints = 0;
        BlockPos testPoint = new BlockPos(xOffset, 0, zOffset);

        int distance = (int) Math.round(Mth.sqrt((float) testPoint.distSqr(point1)));
        if (distance < radius) {
            places++;
        }
        if (distance == radius) {
            blocks++;
            if (point1.getX() == testPoint.getX() || point1.getY() == testPoint.getY() || point1.getZ() == testPoint.getZ()) {
                equalPoints++;
            }
        }

        if (places > 0) {
            return Blocks.AIR;
        }
        if (blocks > 0) {
            if (equalPoints > 0) {
                return type.ribBlock();
            } else {
                return Blocks.GLASS;
            }
        } else {
            return null;
        }
    }
}
