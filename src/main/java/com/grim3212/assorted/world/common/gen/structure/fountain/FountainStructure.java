package com.grim3212.assorted.world.common.gen.structure.fountain;

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
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FountainStructure extends Structure<NoFeatureConfig> {

	public FountainStructure(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public GenerationStage.Decoration getDecorationStage() {
		return GenerationStage.Decoration.SURFACE_STRUCTURES;
	}

	@Override
	protected boolean func_230363_a_(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig) {
		int landHeight = chunkGenerator.getNoiseHeight(chunkX << 4, chunkZ << 4, Heightmap.Type.WORLD_SURFACE_WG);
		return landHeight > 20;
	}

	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory() {
		return FountainStructure.Start::new;
	}

	public static class Start extends StructureStart<NoFeatureConfig> {

		public Start(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn) {
			super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
		}

		@Override
		public void func_230364_a_(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator, TemplateManager templateManager, int chunkX, int chunkZ, Biome biome, NoFeatureConfig config) {
			int x = (chunkX << 4) + 7;
			int z = (chunkZ << 4) + 7;
			int y = chunkGenerator.getNoiseHeightMinusOne(x, z, Type.WORLD_SURFACE_WG);

			int height = 4 * (3 + rand.nextInt(3));
			int type = rand.nextInt(2);

			FountainStructurePiece fountainpiece = new FountainStructurePiece(this.rand, new BlockPos(x, y, z), height, type);
			this.components.add(fountainpiece);
			this.recalculateStructureSize();
		}

	}
}
