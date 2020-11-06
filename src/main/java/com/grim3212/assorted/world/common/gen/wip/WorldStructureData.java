package com.grim3212.assorted.world.common.gen.wip;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Lists;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;

public class WorldStructureData {

	private final String structName;
	private final List<MutableBoundingBox> bbs = Lists.newArrayList();
	// I honestly don't know if we need a concurrent hashset, but can't be too
	// sure for compatibility
	private final Map<ChunkPos, Long> chunksToGenerate = new ConcurrentHashMap<>();

	public WorldStructureData(String structName) {
		this.structName = structName;
	}

	public String getStructName() {
		return structName;
	}

	public List<MutableBoundingBox> getStructures() {
		return bbs;
	}

	public void markChunkForGeneration(int chunkX, int chunkZ, long seed) {
		chunksToGenerate.put(new ChunkPos(chunkX, chunkZ), seed);
	}

	public Optional<Long> getSeedForChunkToGenerate(int chunkX, int chunkZ) {
		return Optional.ofNullable(chunksToGenerate.get(new ChunkPos(chunkX, chunkZ)));
	}

	public boolean markChunkAsGenerated(int chunkX, int chunkZ) {
		return chunksToGenerate.remove(new ChunkPos(chunkX, chunkZ)) != null;
	}
}