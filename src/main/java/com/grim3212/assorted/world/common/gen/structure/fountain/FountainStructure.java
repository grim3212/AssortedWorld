package com.grim3212.assorted.world.common.gen.structure.fountain;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class FountainStructure extends StructureFeature<NoneFeatureConfiguration> {

	public FountainStructure(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public GenerationStep.Decoration step() {
		return GenerationStep.Decoration.SURFACE_STRUCTURES;
	}

	@Override
	protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long seed, WorldgenRandom chunkRandom, ChunkPos chunkPos, Biome biome, ChunkPos chunkPos2, NoneFeatureConfiguration featureConfig, LevelHeightAccessor heightAccessor) {
		int landHeight = chunkGenerator.getFirstFreeHeight(chunkPos.x << 4, chunkPos.z << 4, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor);
		return landHeight > 20;
	}

	@Override
	public StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
		return FountainStructure.Start::new;
	}

	public static class Start extends StructureStart<NoneFeatureConfiguration> {

		public Start(StructureFeature<NoneFeatureConfiguration> structureIn, ChunkPos chunkPos, int referenceIn, long seedIn) {
			super(structureIn, chunkPos, referenceIn, seedIn);
		}

		@Override
		public void generatePieces(RegistryAccess dynamicRegistries, ChunkGenerator chunkGenerator, StructureManager templateManager, ChunkPos chunkPos, Biome biome, NoneFeatureConfiguration config, LevelHeightAccessor heightAccessor) {
			int x = (chunkPos.x << 4) + 7;
			int z = (chunkPos.z << 4) + 7;
			int y = chunkGenerator.getFirstOccupiedHeight(x, z, Types.WORLD_SURFACE_WG, heightAccessor);

			int height = 4 * (3 + random.nextInt(3));
			int type = random.nextInt(2);

			FountainStructurePiece fountainpiece = new FountainStructurePiece(this.random, new BlockPos(x, y, z), height, type);
			this.addPiece(fountainpiece);
		}

	}
}
