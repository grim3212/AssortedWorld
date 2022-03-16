package com.grim3212.assorted.world.common.gen.structure.snowball;

import com.grim3212.assorted.world.AssortedWorld;
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

public class SnowballFeature extends StructureFeature<NoneFeatureConfiguration> {

	public SnowballFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec, PieceGeneratorSupplier.simple(SnowballFeature::checkLocation, SnowballFeature::generatePieces));
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
		BlockPos blockpos = new BlockPos(chunkPos.getMinBlockX(), topLandY + 30, chunkPos.getMinBlockZ());

		int radius = 3 * (3 + context.random().nextInt(5));
		int centerPoints = 2 + context.random().nextInt(6);

		AssortedWorld.LOGGER.debug("Generating Snowball at {} with radius {} and {} center points", blockpos, radius, centerPoints);
		builder.addPiece(new SnowballPiece(context.random(), blockpos, radius, centerPoints));
	}
}
