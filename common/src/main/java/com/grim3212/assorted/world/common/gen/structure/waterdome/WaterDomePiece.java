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

    /** {@link #floorY} before the first pass has resolved it. */
    private static final int UNRESOLVED_FLOOR = Integer.MIN_VALUE;

    private final int radius;

    // Only the first piece of a dome carries the structure's single rune.
    private final boolean placesRune;
    private final int runeIndex;

    // Rolled once per dome so every piece is ribbed with the same material and every chest in the
    // dome draws from the same table.
    private final WaterDomeType domeType;

    // Local columns of this piece that get a loot chest. Decided at construction and serialised,
    // because postProcess runs once per chunk the piece overlaps.
    private final List<BlockPos> chestOffsets;

    // The seabed height the sphere is centred on. Resolved by whichever pass runs first and then
    // serialised: every pass now writes only its own chunk's slice, and the slices have to agree on
    // where the sphere is or the dome comes out stepped at the chunk borders.
    private int floorY;

    /**
     * @param centre where the lobe's sphere goes. The box is built around it rather than starting
     *               at it, so {@link #getLocatorPosition()} hands the same point back and the
     *               volume written and the volume tested are one and the same. Building the box
     *               from a corner instead would shift every lobe by its own radius and leave the
     *               chain's spacing varying with it — gaps where two small lobes met.
     */
    public WaterDomePiece(RandomSource random, BlockPos centre, int radius, boolean placesRune, int runeIndex, WaterDomeType domeType, int chestCount) {
        super(WorldStructures.WATER_DOME_STRUCTURE_PIECE.get(), centre.getX() - radius, centre.getY() - radius, centre.getZ() - radius, (radius * 2) + 1, (radius * 2) + 1, (radius * 2) + 1, getRandomHorizontalDirection(random));
        this.radius = radius;
        this.placesRune = placesRune;
        this.runeIndex = runeIndex;
        this.domeType = domeType;
        this.chestOffsets = rollChestOffsets(random, radius, chestCount);
        this.floorY = UNRESOLVED_FLOOR;
    }

    public WaterDomePiece(StructurePieceSerializationContext context, CompoundTag tagCompound) {
        super(WorldStructures.WATER_DOME_STRUCTURE_PIECE.get(), tagCompound);
        this.radius = tagCompound.getIntOr("radius", 0);
        this.placesRune = tagCompound.getBooleanOr("placesRune", false);
        this.runeIndex = tagCompound.getIntOr("runeIndex", 0);
        this.domeType = WaterDomeType.byOrdinal(tagCompound.getIntOr("domeType", 0));
        this.chestOffsets = tagCompound.read("chestOffsets", BLOCK_POS_LIST_CODEC).orElse(List.of());
        this.floorY = tagCompound.getIntOr("floorY", UNRESOLVED_FLOOR);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
        super.addAdditionalSaveData(context, tagCompound);
        tagCompound.putInt("radius", this.radius);
        tagCompound.putBoolean("placesRune", this.placesRune);
        tagCompound.putInt("runeIndex", this.runeIndex);
        tagCompound.putInt("domeType", this.domeType.ordinal());
        tagCompound.store("chestOffsets", BLOCK_POS_LIST_CODEC, this.chestOffsets);
        tagCompound.putInt("floorY", this.floorY);
    }

    @Override
    public void postProcess(WorldGenLevel reader, StructureManager structureManager, ChunkGenerator generator, RandomSource rand, BoundingBox bb, ChunkPos chunkPos, BlockPos pos) {
        // The sphere is centred on this piece's own bounding box — the box vanilla tested to decide
        // whether to call us at all. It used to be centred on the *first* piece's box with this
        // piece's offsets applied a second time, which put the blocks a mean of 6 and up to 16
        // blocks outside that box, into chunks nobody had cleared us to touch.
        BlockPos locator = this.getLocatorPosition();
        BlockPos centre = new BlockPos(locator.getX(), floorY(reader, locator), locator.getZ());

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // A whole column is either in this chunk or in none of it, since bb is the chunk's
                // full-height box. Skipping here keeps the column walks below reading only blocks
                // the generator has cleared us for.
                if (!bb.isInside(centre.offset(x, 0, z))) {
                    continue;
                }

                // A chest is a property of the column, not of a fixed height: walk up and take the
                // first hollow, supported block, which is the dome's floor whatever the terrain is
                // doing under it.
                boolean wantsChest = this.chestOffsets.contains(new BlockPos(x, 0, z));

                for (int y = -radius; y <= radius; y++) {
                    BlockPos worldPos = centre.offset(x, y, z);

                    Block b = blockToPlace(x, y, z, this.domeType);
                    if (b == null) {
                        continue;
                    }

                    Block curBlock = reader.getBlockState(worldPos).getBlock();

                    if (wantsChest && b == Blocks.AIR) {
                        // An earlier pass over this piece already placed it. Leave the loot alone.
                        if (curBlock == Blocks.CHEST) {
                            wantsChest = false;
                            continue;
                        }

                        // Air as well as water: the lobes of a dome overlap and are processed in
                        // order, so another may already have hollowed this spot out.
                        if ((curBlock == Blocks.AIR || isWater(reader, worldPos, curBlock)) && isSupport(reader, worldPos.below())) {
                            setBlockState(reader, worldPos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, chestFacing()), rand);
                            wantsChest = false;
                            continue;
                        }
                    }

                    if (isWater(reader, worldPos, curBlock)) {
                        setBlockState(reader, worldPos, b.defaultBlockState(), rand);
                    }
                }
            }
        }

        // After the shell and the hollow exist, so the floor it looks for is the finished one, and
        // only from the chunk that owns the centre column.
        if (this.placesRune && bb.isInside(centre)) {
            placeRune(reader, centre);
        }
    }

    /**
     * The seabed under the centre of the sphere, resolved once. Reading it per pass was safe while
     * every pass rewrote the whole dome, but now that each pass writes only its own chunk a second
     * answer would step the dome at the chunk border — and the shell the first pass laid down is
     * itself motion-blocking, so the second answer would not even be the seabed.
     */
    private int floorY(WorldGenLevel reader, BlockPos locator) {
        if (this.floorY == UNRESOLVED_FLOOR) {
            this.floorY = reader.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, locator.getX(), locator.getZ());
        }

        return this.floorY;
    }

    /**
     * Stands the dome's single rune on the floor of its centre column rather than at the sphere's
     * centre, which is as often buried in the seabed as floating in mid-water. The floor is the
     * lowest hollow, supported block in the column: the seabed where the dome cuts into it, the
     * dome's own shell where it stands clear of it.
     */
    private void placeRune(WorldGenLevel reader, BlockPos centre) {
        Block rune = RuinUtil.runeAt(this.runeIndex);

        // Only the interior of the sphere, which at the centre column is |y| < radius.
        int lowest = -radius + 1;
        int highest = radius - 1;

        // An earlier pass over this piece already placed it. There is exactly one rune per dome.
        for (int y = lowest; y <= highest; y++) {
            if (reader.getBlockState(centre.offset(0, y, 0)).getBlock() == rune) {
                return;
            }
        }

        for (int y = lowest; y <= highest; y++) {
            BlockPos at = centre.offset(0, y, 0);
            Block curBlock = reader.getBlockState(at).getBlock();

            if ((curBlock == Blocks.AIR || isWater(reader, at, curBlock)) && isSupport(reader, at.below())) {
                reader.setBlock(at, rune.defaultBlockState(), 2);
                return;
            }
        }

        // Exactly one rune per dome is a hard requirement, so a centre column that is solid all the
        // way up — a dome buried in a rise on the seabed — still gets one, at the sphere's centre.
        reader.setBlock(centre, rune.defaultBlockState(), 2);
    }

    /** Something a chest or rune can stand on — the seabed under the dome, or the dome's own shell. */
    private boolean isSupport(WorldGenLevel reader, BlockPos pos) {
        BlockState state = reader.getBlockState(pos);
        return !state.isAir() && state.getFluidState().isEmpty();
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
     * Picks distinct columns for this piece's chests, well inside the shell and avoiding the centre
     * column reserved for the rune. Only x and z matter — the y each chest ends up at is found by
     * walking the column in {@code postProcess}; the stored 0 is a placeholder.
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

    /**
     * Hollow inside, glass on the surface, ribs of the dome's own material along the three planes
     * through its centre, and nothing at all outside. Purely geometric — no randomness — so every
     * pass agrees on what belongs at a given block.
     */
    private Block blockToPlace(int x, int y, int z, WaterDomeType type) {
        int distance = (int) Math.round(Mth.sqrt((float) ((x * x) + (y * y) + (z * z))));

        if (distance < radius) {
            return Blocks.AIR;
        }
        if (distance > radius) {
            return null;
        }

        return x == 0 || y == 0 || z == 0 ? type.ribBlock() : Blocks.GLASS;
    }
}
