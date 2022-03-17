package com.grim3212.assorted.world.common.gen.structure.fountain;

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

public class FountainFeature extends StructureFeature<NoneFeatureConfiguration> {

	public FountainFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec, PieceGeneratorSupplier.simple(FountainFeature::checkLocation, FountainFeature::generatePieces));
	}

	@Override
	public GenerationStep.Decoration step() {
		return GenerationStep.Decoration.SURFACE_STRUCTURES;
	}

	private static <C extends FeatureConfiguration> boolean checkLocation(PieceGeneratorSupplier.Context<C> context) {
		if (!context.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG)) {
			return false;
		} else {
			return context.getLowestY(12, 15) >= context.chunkGenerator().getSeaLevel();
		}
	}

	private static void generatePieces(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context) {
		ChunkPos chunkPos = context.chunkPos();
		BlockPos middlePos = chunkPos.getMiddleBlockPosition(0);
		int topLandY = context.chunkGenerator().getFirstFreeHeight(middlePos.getX(), middlePos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
		BlockPos blockpos = new BlockPos(chunkPos.getMinBlockX(), topLandY, chunkPos.getMinBlockZ());

		int height = 4 * (3 + context.random().nextInt(3));
		int type = context.random().nextInt(2);

		// AssortedWorld.LOGGER.debug("Generating Fountain at {} with height {} and of type {}", blockpos, height, type);
		builder.addPiece(new FountainPiece(context.random(), blockpos, height, type));
	}
}
