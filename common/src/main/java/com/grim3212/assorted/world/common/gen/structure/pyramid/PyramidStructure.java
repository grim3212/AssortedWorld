package com.grim3212.assorted.world.common.gen.structure.pyramid;

import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntBinaryOperator;

public class PyramidStructure extends Structure {
    public static final MapCodec<PyramidStructure> CODEC = simpleCodec(PyramidStructure::new);

    public PyramidStructure(Structure.StructureSettings settings) {
        super(settings);
    }

    /**
     * Where a pyramid's base course sits, and the lowest ground along its outline.
     *
     * @param base   height of the base course; the floor is one below it
     * @param lowest the lowest top block anywhere on the base's outline
     */
    public record Placement(int base, int lowest) {
    }

    /**
     * Picks the height for a pyramid centred on {@code centerX, centerZ}, or nothing if the ground
     * is too steep for one.
     *
     * @param groundTop the top block's height at a column
     */
    public static Optional<Placement> placement(IntBinaryOperator groundTop, int centerX, int centerZ, int maxHeight) {
        int reach = PyramidPiece.halfWidth(maxHeight);
        List<Integer> alongX = samples(centerX, reach);
        List<Integer> alongZ = samples(centerZ, reach);

        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        long total = 0;
        int count = 0;
        for (int[] column : outline(alongX, alongZ, reach)) {
            int top = groundTop.applyAsInt(centerX + column[0], centerZ + column[1]);
            lowest = Math.min(lowest, top);
            highest = Math.max(highest, top);
            total += top;
            count++;
        }

        // A slope steeper than the pyramid is tall would bury the uphill side whole.
        if (highest - lowest > maxHeight) {
            return Optional.empty();
        }

        // How deep it sits on level ground, from the outline and the middle.
        total += groundTop.applyAsInt(centerX, centerZ);
        count++;
        int halfBuried = (int) (total / count) - maxHeight / 2;

        return Optional.of(new Placement(Math.min(halfBuried, lowest - MARGIN), lowest));
    }

    /** Blocks the base course stays below the lowest sampled ground. */
    private static final int MARGIN = 2;

    /** The overworld's noise cell width. */
    private static final int CELL = 4;

    /** Offsets along one edge: both ends, and every column where noise cells meet. */
    private static List<Integer> samples(int center, int reach) {
        List<Integer> offsets = new ArrayList<>();
        for (int t = -reach; t <= reach; t++) {
            if (t == -reach || t == reach || Math.floorMod(center + t, CELL) == 0) {
                offsets.add(t);
            }
        }
        return offsets;
    }

    /** The sampled outline columns, each corner once. */
    private static List<int[]> outline(List<Integer> alongX, List<Integer> alongZ, int reach) {
        List<int[]> columns = new ArrayList<>();
        for (int x : alongX) {
            columns.add(new int[]{x, -reach});
            columns.add(new int[]{x, reach});
        }
        for (int z : alongZ) {
            if (Math.abs(z) != reach) {
                columns.add(new int[]{-reach, z});
                columns.add(new int[]{reach, z});
            }
        }
        return columns;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        if (!WorldStructures.validBiomeOnTop(context, Heightmap.Types.WORLD_SURFACE_WG)) {
            return Optional.empty();
        }

        RandomSource random = context.random();
        int maxHeight = 2 * (4 + random.nextInt(5));
        int type = random.nextInt(2);

        // The piece's box starts on the chunk corner and is twice the height wide, so its centre
        // is the height in from the corner on both axes.
        ChunkPos chunkPos = context.chunkPos();
        int centerX = chunkPos.getMinBlockX() + maxHeight;
        int centerZ = chunkPos.getMinBlockZ() + maxHeight;

        IntBinaryOperator groundTop = (x, z) -> context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        Optional<Placement> placement = placement(groundTop, centerX, centerZ, maxHeight);

        // Not on or next to water: any of the outline at sea level is a shore or a lake.
        if (placement.isEmpty() || placement.get().lowest() < context.chunkGenerator().getSeaLevel()) {
            return Optional.empty();
        }

        int base = placement.get().base();
        if (base - 1 <= context.heightAccessor().getMinY() || base + maxHeight >= context.heightAccessor().getMaxY()) {
            return Optional.empty();
        }

        // The stub stands on the surface: vanilla checks the biome at it, and the base course can be
        // deep enough to reach a cave biome.
        BlockPos corner = new BlockPos(chunkPos.getMinBlockX(), base, chunkPos.getMinBlockZ());
        BlockPos surface = new BlockPos(centerX, groundTop.applyAsInt(centerX, centerZ), centerZ);
        return Optional.of(new Structure.GenerationStub(surface, builder -> builder.addPiece(new PyramidPiece(random, corner, base, maxHeight, type))));
    }

    @Override
    public StructureType<?> type() {
        return WorldStructures.PYRAMID_STRUCTURE_TYPE.get();
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }
}
