package com.grim3212.assorted.world.common.gen.structure.waterdome;

import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.grim3212.assorted.world.common.util.RuinUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class WaterDomePiece extends ScatteredFeaturePiece {

    private final int radius;
    private final int xOffset;
    private final int zOffset;
    private boolean placeRune;

    public WaterDomePiece(RandomSource random, BlockPos pos, int radius, int xOffset, int zOffset, boolean placeRune) {
        super(WorldStructures.WATER_DOME_STRUCTURE_PIECE.get(), pos.getX(), pos.getY(), pos.getZ(), (radius * 2) + 1, radius, (radius * 2) + 1, getRandomHorizontalDirection(random));
        this.radius = radius;
        this.xOffset = xOffset;
        this.zOffset = zOffset;
        this.placeRune = placeRune;
    }

    public WaterDomePiece(StructurePieceSerializationContext context, CompoundTag tagCompound) {
        super(WorldStructures.WATER_DOME_STRUCTURE_PIECE.get(), tagCompound);
        this.radius = tagCompound.getInt("radius");
        this.xOffset = tagCompound.getInt("xOffset");
        this.zOffset = tagCompound.getInt("zOffset");
        this.placeRune = tagCompound.getBoolean("placeRune");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
        super.addAdditionalSaveData(context, tagCompound);
        tagCompound.putInt("radius", this.radius);
        tagCompound.putInt("xOffset", this.xOffset);
        tagCompound.putInt("zOffset", this.zOffset);
        tagCompound.putBoolean("placeRune", placeRune);
    }

    @Override
    public void postProcess(WorldGenLevel reader, StructureManager structureManager, ChunkGenerator generator, RandomSource rand, BoundingBox bb, ChunkPos chunkPos, BlockPos pos) {
        BlockPos centerPoint = this.getLocatorPosition();
        int i = reader.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, centerPoint.getX(), centerPoint.getZ());

        // Set type
        int type = rand.nextInt(20);

        // Get correct position
        pos = new BlockPos(pos.getX(), i, pos.getZ());

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    BlockPos newPoint = new BlockPos(x + xOffset, y, z + zOffset);

                    Block b = blockToPlace(rand, pos, newPoint, type);
                    if (b == null) {
                        continue;
                    }

                    Block curBlock = reader.getBlockState(pos.offset(newPoint)).getBlock();
                    if (curBlock == Blocks.WATER) {
                        reader.setBlock(pos.offset(newPoint), b.defaultBlockState(), 2);
                    } else {
                        FluidState state = reader.getFluidState(pos.offset(newPoint));
                        if (!state.isEmpty() && (state.getType() == Fluids.FLOWING_WATER || state.getType() == Fluids.WATER)) {
                            reader.setBlock(pos.offset(newPoint), b.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

    }

    private Block blockToPlace(RandomSource random, BlockPos pos, BlockPos point1, int type) {
        int blocks = 0;
        int places = 0;
        int equalPoints = 0;
        BlockPos testPoint = new BlockPos(xOffset, 0, zOffset);

        int distance = (int) Math.round(Mth.sqrt((float) testPoint.distSqr(point1)));
        if (distance < radius) {
            places++;
            if (point1.equals(testPoint) && placeRune) {
                return RuinUtil.randomRune(random);
            }
        }
        if (distance == radius) {
            blocks++;
            if (point1.getX() == testPoint.getX() || point1.getY() == testPoint.getY() || point1.getZ() == testPoint.getZ()) {
                equalPoints++;
            }
        }

        if (places > 0) {
            if (type % 4 != 0)
                ;
            return Blocks.AIR;
        }
        if (blocks > 0) {
            if (equalPoints > 0) {
                if (type == 5) {
                    return Blocks.GLOWSTONE;
                }
                if (type == 10) {
                    return Blocks.IRON_BLOCK;
                }
                if (type == 15) {
                    return Blocks.OBSIDIAN;
                } else {
                    return Blocks.COBBLESTONE;
                }
            } else {
                return Blocks.GLASS;
            }
        } else {
            return null;
        }
    }
}
