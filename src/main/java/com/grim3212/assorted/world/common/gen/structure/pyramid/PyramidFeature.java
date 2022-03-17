package com.grim3212.assorted.world.common.gen.structure.pyramid;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class PyramidFeature extends StructureFeature<NoneFeatureConfiguration> {
	public PyramidFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec, PieceGeneratorSupplier.simple(PyramidFeature::checkLocation, PyramidFeature::generatePieces));
	}

	@Override
	public GenerationStep.Decoration step() {
		return GenerationStep.Decoration.SURFACE_STRUCTURES;
	}

	private static <C extends FeatureConfiguration> boolean checkLocation(PieceGeneratorSupplier.Context<C> context) {
		if (!context.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG)) {
			return false;
		} else {
			return context.getLowestY(21, 21) >= context.chunkGenerator().getSeaLevel();
		}
	}

	private static void generatePieces(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context) {
		ChunkPos chunkPos = context.chunkPos();
		BlockPos middlePos = chunkPos.getMiddleBlockPosition(0);
		int topLandY = context.chunkGenerator().getFirstFreeHeight(middlePos.getX(), middlePos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
		BlockPos blockpos = new BlockPos(chunkPos.getMinBlockX(), topLandY, chunkPos.getMinBlockZ());

		int maxHeight = 2 * (4 + context.random().nextInt(5));
		int type = context.random().nextInt(2);

		// AssortedWorld.LOGGER.debug("Generating Pyramid at {} with maxHeight {} and of type {}", blockpos, maxHeight, type);
		builder.addPiece(new PyramidPiece(context.random(), blockpos, maxHeight, type));
	}
}
