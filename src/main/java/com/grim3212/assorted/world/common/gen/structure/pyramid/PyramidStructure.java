package com.grim3212.assorted.world.common.gen.structure.pyramid;

import java.util.Optional;

import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class PyramidStructure extends Structure {
	public static final Codec<PyramidStructure> CODEC = simpleCodec(PyramidStructure::new);

	public PyramidStructure(Structure.StructureSettings settings) {
		super(settings);
	}

	private static boolean extraSpawningChecks(Structure.GenerationContext context) {
		if (!WorldStructures.validBiomeOnTop(context, Heightmap.Types.WORLD_SURFACE_WG)) {
			return false;
		}
		return Structure.getLowestY(context, 21, 21) >= context.chunkGenerator().getSeaLevel();
	}

	@Override
	public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
		if (!PyramidStructure.extraSpawningChecks(context)) {
			return Optional.empty();
		}

		return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, (piecesBuilder) -> {
			generatePieces(piecesBuilder, context);
		});
	}

	@Override
	public StructureType<?> type() {
		return WorldStructures.PYRAMID_STRUCTURE_TYPE.get();
	}

	@Override
	public GenerationStep.Decoration step() {
		return GenerationStep.Decoration.SURFACE_STRUCTURES;
	}

	private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
		ChunkPos chunkPos = context.chunkPos();
		BlockPos middlePos = chunkPos.getMiddleBlockPosition(0);
		int topLandY = context.chunkGenerator().getFirstFreeHeight(middlePos.getX(), middlePos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
		BlockPos blockpos = new BlockPos(chunkPos.getMinBlockX(), topLandY, chunkPos.getMinBlockZ());

		int maxHeight = 2 * (4 + context.random().nextInt(5));
		int type = context.random().nextInt(2);

		// AssortedWorld.LOGGER.debug("Generating Pyramid at {} with maxHeight {} and of
		// type {}", blockpos, maxHeight, type);
		builder.addPiece(new PyramidPiece(context.random(), blockpos, maxHeight, type));
	}
}
