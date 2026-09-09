package com.grim3212.assorted.world.common.gen.structure.waterdome;

import com.grim3212.assorted.world.WorldCommonMod;
import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.grim3212.assorted.world.common.util.RuinUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class WaterDomeStructure extends Structure {

    public static final MapCodec<WaterDomeStructure> CODEC = simpleCodec(WaterDomeStructure::new);

    public WaterDomeStructure(Structure.StructureSettings settings) {
        super(settings);
    }

    private static boolean extraSpawningChecks(Structure.GenerationContext context) {
        if (!WorldStructures.validBiomeOnTop(context, Heightmap.Types.OCEAN_FLOOR_WG)) {
            return false;
        }

        int i = context.chunkPos().getMiddleBlockX();
        int j = context.chunkPos().getMiddleBlockZ();
        int k = context.chunkGenerator().getFirstOccupiedHeight(i, j, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        if (k == -1 || k > context.chunkGenerator().getSeaLevel() - 11) {
            return false;
        }

        return true;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        if (!WaterDomeStructure.extraSpawningChecks(context)) {
            return Optional.empty();
        }

        return onTopOfChunkCenter(context, Heightmap.Types.OCEAN_FLOOR_WG, (piecesBuilder) -> {
            generatePieces(piecesBuilder, context);
        });
    }

    @Override
    public StructureType<?> type() {
        return WorldStructures.WATER_DOME_STRUCTURE_TYPE.get();
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        BlockPos middlePos = chunkPos.getMiddleBlockPosition(0);
        int topOceanY = context.chunkGenerator().getFirstFreeHeight(middlePos.getX(), middlePos.getZ(), Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        BlockPos blockpos = new BlockPos(chunkPos.getMinBlockX(), topOceanY, chunkPos.getMinBlockZ());

        int maxRadius = 3 + context.random().nextInt(5);

        WorldgenRandom rand = context.random();

        int l = 0;
        int i1;
        for (i1 = 0; l == 0 && i1 == 0; i1 = -1 + rand.nextInt(3)) {
            l = -1 + rand.nextInt(3);
        }

        int rad = 3 + rand.nextInt(maxRadius);

        // The piece count is rolled once. It used to be re-rolled on every iteration of the loop
        // condition, which capped the dome well below the configured waterDomePieceMod.
        int pieceCount = 4 + rand.nextInt(1 + WorldCommonMod.COMMON_CONFIG.waterDomePieceMod.get());
        int[] xs = new int[pieceCount];
        int[] zs = new int[pieceCount];
        int[] rads = new int[pieceCount];

        int xOff = 0;
        int zOff = 0;
        for (int idx = 0; idx < pieceCount; idx++) {
            int x = xOff + l * (1 + (1 + rad / 2) + rand.nextInt(1 + rad / 2));
            int z = zOff + i1 * (1 + (1 + rad / 2) + rand.nextInt(1 + rad / 2));
            rad = 3 + rand.nextInt(maxRadius);
            xOff = x;
            zOff = z;

            xs[idx] = x;
            zs[idx] = z;
            rads[idx] = rad;
        }

        // Exactly one rune per dome, in the first piece. It is rolled here rather than in
        // postProcess so it survives the piece being processed once per chunk it overlaps.
        int runeIndex = RuinUtil.randomRuneIndex(rand);

        // The ribbing material used to be re-rolled inside every postProcess pass, which meant a
        // dome's pieces disagreed with each other. One roll per dome, and it decides the loot too.
        WaterDomeType domeType = WaterDomeType.random(rand);

        // A dome either has loot or it does not; the ones that do get one or two chests, spread
        // over randomly chosen pieces.
        int[] chestsPerPiece = new int[pieceCount];
        if (rand.nextDouble() < WorldCommonMod.COMMON_CONFIG.waterDomeChestChance.get()) {
            int chestCount = 1 + rand.nextInt(2);
            for (int idx = 0; idx < chestCount; idx++) {
                chestsPerPiece[rand.nextInt(pieceCount)]++;
            }
        }

        for (int idx = 0; idx < pieceCount; idx++) {
            builder.addPiece(new WaterDomePiece(context.random(), blockpos.offset(xs[idx], 0, zs[idx]), rads[idx], xs[idx], zs[idx], idx == 0, runeIndex, domeType, chestsPerPiece[idx]));
        }
    }
}
