package com.grim3212.assorted.world.common.gen.structure.waterdome;

import java.util.Random;

import com.grim3212.assorted.world.common.handler.WorldConfig;
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

public class WaterDomeFeature extends StructureFeature<NoneFeatureConfiguration> {

	public WaterDomeFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec, PieceGeneratorSupplier.simple(WaterDomeFeature::checkLocation, WaterDomeFeature::generatePieces));
	}

	@Override
	public GenerationStep.Decoration step() {
		return GenerationStep.Decoration.SURFACE_STRUCTURES;
	}

	private static <C extends FeatureConfiguration> boolean checkLocation(PieceGeneratorSupplier.Context<C> context) {
		if (!context.validBiomeOnTop(Heightmap.Types.OCEAN_FLOOR_WG)) {
			return false;
		} else {
			int i = context.chunkPos().getMiddleBlockX();
			int j = context.chunkPos().getMiddleBlockZ();
			int k = context.chunkGenerator().getFirstOccupiedHeight(i, j, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor());
			if (k == -1 || k > context.chunkGenerator().getSeaLevel() - 11) {
				return false;
			}

			return true;
		}
	}

	private static void generatePieces(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context) {
		ChunkPos chunkPos = context.chunkPos();
		BlockPos middlePos = chunkPos.getMiddleBlockPosition(0);
		int topOceanY = context.chunkGenerator().getFirstFreeHeight(middlePos.getX(), middlePos.getZ(), Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor());
		BlockPos blockpos = new BlockPos(chunkPos.getMinBlockX(), topOceanY, chunkPos.getMinBlockZ());

		int maxRadius = 3 + context.random().nextInt(5);

		Random rand = context.random();

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

		// AssortedWorld.LOGGER.debug("Generating Water Domes at {} with max radius {} with {} pieces", blockpos, maxRadius, numPieces);
	}
}
