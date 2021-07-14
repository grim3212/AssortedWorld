package com.grim3212.assorted.world.common.gen.structure.waterdome;

import com.mojang.serialization.Codec;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class WaterDomeStructure extends Structure<NoFeatureConfig> {

	public WaterDomeStructure(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	@Override
	protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig) {
		int x = (chunkX << 4) + 7;
		int z = (chunkZ << 4) + 7;
		int y = chunkGenerator.getFirstOccupiedHeight(x, z, Type.OCEAN_FLOOR_WG);

		if (y == -1 || y > chunkGenerator.getSeaLevel() - 11) {
			return false;
		}
		return true;
	}

	@Override
	public GenerationStage.Decoration step() {
		return GenerationStage.Decoration.SURFACE_STRUCTURES;
	}

	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory() {
		return WaterDomeStructure.Start::new;
	}

	public static class Start extends StructureStart<NoFeatureConfig> {

		public Start(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn) {
			super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
		}

		@Override
		public void generatePieces(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator, TemplateManager templateManager, int chunkX, int chunkZ, Biome biome, NoFeatureConfig config) {
			int x = (chunkX << 4) + 7;
			int z = (chunkZ << 4) + 7;
			int y = chunkGenerator.getFirstOccupiedHeight(x, z, Type.OCEAN_FLOOR_WG);

			int radius = 3 + this.random.nextInt(5);

			WaterDomeStructurePiece waterdomepiece = new WaterDomeStructurePiece(this.random, new BlockPos(x, y, z), radius);
			this.pieces.add(waterdomepiece);
			this.calculateBoundingBox();
		}

	}
}
