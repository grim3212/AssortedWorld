package com.grim3212.assorted.world.common.gen.wip;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.google.common.collect.Maps;
import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.handler.WorldConfig;
import com.mojang.serialization.Codec;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.server.ServerWorld;

public abstract class WorldStructure extends Feature<NoFeatureConfig> {

	protected static final int[] BASE_OFFSETS = new int[] { -1, 1, -1, 1 };
	protected static final int[] BASIC_2_OFFSETS = new int[] { -2, 2, -2, 2 };
	protected static final int[] BASIC_3_OFFSETS = new int[] { -3, 3, -3, 3 };

	protected Map<ResourceLocation, WorldStructureStorage> structureData;

	public WorldStructure(Codec<NoFeatureConfig> codec) {
		super(codec);
		this.structureData = Maps.newHashMap();
	}

	protected abstract String getStructureName();

	@Override
	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		// Is the structure allowed to generate
		if (!canGenerate()) {
			return false;
		}

		ServerWorld world = reader.getWorld();

		// is the structure allowed in this dimension
		if (!canGenerateInDimension(world)) {
			return false;
		}

		// predetermine the seed so the generation is the same no matter when
		// the structure is generated
		long generationSeed = rand.nextLong() ^ getStructureName().hashCode();

		int chunkX = pos.getX() >> 4;
		int chunkZ = pos.getZ() >> 4;
		BlockPos generatePos = reader.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos);

		// Is the structure allowed to generate in this chunk
		if (!canGenerateInChunk(world, rand, chunkX, chunkZ)) {
			WorldStructureData structureData = getStructureData(world);

			int[] offsets = getChunkOffsets();
			if (offsets.length != 4)
				throw new IllegalArgumentException("Chunk offsets length must be 4 got '" + offsets.length + "'");

			// check if we need to generate in one of the surrounding chunks
			for (int x = chunkX + offsets[0]; x <= chunkX + offsets[1]; x++) {
				for (int z = chunkZ + offsets[2]; z <= chunkZ + offsets[3]; z++) {
					Optional<Long> optionalGenerationSeed = structureData.getSeedForChunkToGenerate(x, z);
					if (optionalGenerationSeed.isPresent() && areSurroundingChunksLoaded(x, z, reader)) {
						generateStructureInChunk(optionalGenerationSeed.get(), world, x, z, generatePos.getY());
						return structureData.markChunkAsGenerated(x, z);
					}
				}
			}

			return false;
		}

		// Make sure that the chunks are laoded
		if (!areSurroundingChunksLoaded(chunkX, chunkZ, reader)) {
			AssortedWorld.LOGGER.info("Chunks aren't loaded");
			getStructureData(world).markChunkForGeneration(chunkX, chunkZ, generationSeed);
			return true;
		}

		AssortedWorld.LOGGER.info("Lets try and generate a structure");
		// Try and generate structure in chunk
		return generateStructureInChunk(generationSeed, world, chunkX, chunkZ, generatePos.getY());
	}

	public boolean isStructureAt(ServerWorld world, BlockPos pos) {
		for (MutableBoundingBox data : getStructureData(world).getStructures()) {
			if (data.isVecInside(pos)) {
				return true;
			}
		}
		return false;
	}

	protected WorldStructureStorage getStructureStorage(ServerWorld world) {
		ResourceLocation dimensionId = world.getDimensionKey().getRegistryName();
		if (!structureData.containsKey(dimensionId)) {
			WorldStructureStorage data = WorldStructureStorage.getStructureStorage(world);
			structureData.put(dimensionId, data);
		}

		return structureData.get(dimensionId);
	}

	protected WorldStructureData getStructureData(ServerWorld world) {
		return getStructureStorage(world).getStructureData(getStructureName());
	}

	protected void addBBSave(ServerWorld world, MutableBoundingBox bb) {
		getStructureStorage(world).addBBSave(getStructureName(), bb);
	}

	/**
	 * Must return 4 integers for chunk offsets to check that the area is loaded
	 * before we generate
	 * 
	 * 0: xNeg, 1: xPos, 2: zNeg, 3: zPos
	 * 
	 * @return int[4]
	 */
	protected int[] getChunkOffsets() {
		return BASE_OFFSETS;
	}

	protected abstract boolean generateStructureInChunk(long seed, ServerWorld world, int chunkX, int chunkZ, int height);

	protected abstract boolean canGenerateInChunk(ServerWorld world, Random rand, int chunkX, int chunkZ);

	protected abstract boolean canGenerate();

	protected boolean canGenerateInDimension(ServerWorld world) {
		if (world.getDimensionType().isSame(world.func_241828_r().func_230520_a_().getOrThrow(DimensionType.OVERWORLD))) {
			return true;
		}
		return false;
	}

	protected boolean checkStructures(ServerWorld world, BlockPos pos) {
		if (WorldConfig.COMMON.checkForStructures.get()) {
			return !getStructureStorage(world).isStructureAt(pos);
		}

		return true;
	}

	private boolean areSurroundingChunksLoaded(int chunkX, int chunkZ, ISeedReader seedReader) {
		int[] offsets = getChunkOffsets();
		if (offsets.length != 4)
			throw new IllegalArgumentException("Chunk offsets length must be 4 got '" + offsets.length + "'");

		for (int x = chunkX + offsets[0]; x <= chunkX + offsets[1]; x++) {
			for (int z = chunkZ + offsets[2]; z <= chunkZ + offsets[3]; z++) {
				IChunk ichunk = seedReader.getChunk(x, z);
				if (!(ichunk instanceof Chunk)) {
					return false;
				}
			}
		}
		return true;
	}
}
