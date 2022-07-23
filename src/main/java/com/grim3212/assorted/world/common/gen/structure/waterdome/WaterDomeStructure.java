package com.grim3212.assorted.world.common.gen.structure.waterdome;

import java.util.Optional;

import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.grim3212.assorted.world.common.handler.WorldConfig;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WaterDomeStructure extends Structure {

	public static final Codec<WaterDomeStructure> CODEC = simpleCodec(WaterDomeStructure::new);

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

		int xOff = 0;
		int zOff = 0;
		int numPieces = 0;
		for (int idx = 0; idx < 4 + rand.nextInt(1 + WorldConfig.COMMON.waterDomePieceMod.get()); idx++) {
			int x = xOff + l * (1 + (1 + rad / 2) + rand.nextInt(1 + rad / 2));
			int z = zOff + i1 * (1 + (1 + rad / 2) + rand.nextInt(1 + rad / 2));
			rad = 3 + rand.nextInt(maxRadius);
			xOff = x;
			zOff = z;

			numPieces++;
			builder.addPiece(new WaterDomePiece(context.random(), blockpos.offset(x, 0, z), rad, x, z, numPieces == 1));
		}
	}
}
