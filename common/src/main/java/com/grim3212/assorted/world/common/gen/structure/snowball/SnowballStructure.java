package com.grim3212.assorted.world.common.gen.structure.snowball;

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

public class SnowballStructure extends Structure {

	public static final Codec<SnowballStructure> CODEC = simpleCodec(SnowballStructure::new);

	public SnowballStructure(Structure.StructureSettings settings) {
		super(settings);
	}

	private static boolean extraSpawningChecks(Structure.GenerationContext context) {
		if (!WorldStructures.validBiomeOnTop(context, Heightmap.Types.WORLD_SURFACE_WG)) {
			return false;
		}
		return Structure.getLowestY(context, 12, 15) >= context.chunkGenerator().getSeaLevel();
	}

	@Override
	public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
		if (!SnowballStructure.extraSpawningChecks(context)) {
			return Optional.empty();
		}

		return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, (piecesBuilder) -> {
			generatePieces(piecesBuilder, context);
		});
	}

	@Override
	public StructureType<?> type() {
		return WorldStructures.SNOWBALL_STRUCTURE_TYPE.get();
	}

	@Override
	public GenerationStep.Decoration step() {
		return GenerationStep.Decoration.SURFACE_STRUCTURES;
	}

	private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
		ChunkPos chunkPos = context.chunkPos();
		BlockPos middlePos = chunkPos.getMiddleBlockPosition(0);
		int topLandY = context.chunkGenerator().getFirstFreeHeight(middlePos.getX(), middlePos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
		BlockPos blockpos = new BlockPos(chunkPos.getMinBlockX(), topLandY + 30, chunkPos.getMinBlockZ());

		int radius = (2 * (2 + context.random().nextInt(5))) + 3;
		int centerPoints = 2 + context.random().nextInt(6);

		builder.addPiece(new SnowballPiece(context.random(), blockpos, radius, centerPoints));
	}
}
