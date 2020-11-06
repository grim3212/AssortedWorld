package com.grim3212.assorted.world.common.gen.wip.structure;

import java.util.Random;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.gen.wip.WorldStructure;
import com.grim3212.assorted.world.common.handler.WorldConfig;
import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.server.ServerWorld;

public class RuinStructure extends WorldStructure {

	public RuinStructure(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	@Override
	protected boolean generateStructureInChunk(long seed, ServerWorld world, int chunkX, int chunkZ, int height) {
		Random random = new Random(seed);
		boolean atLeastOne = false;

		AssortedWorld.LOGGER.info("Try ruin at");

		for (int i = 0; i < WorldConfig.COMMON.ruinTries.get(); i++) {
			int x = chunkX * 16 + 8 + random.nextInt(16);
			int z = chunkZ * 16 + 8 + random.nextInt(16);
			BlockPos pos = new BlockPos(x, height, z);

			if (checkStructures(world, pos) && generateRuin(random, world, pos)) {
				atLeastOne = true;
			}
		}

		AssortedWorld.LOGGER.info("Failed to generate atleast one ruin");
		return atLeastOne;
	}

	private boolean generateRuin(Random random, ServerWorld world, BlockPos pos) {
		int radius = 3 + random.nextInt(5);
		int skip = random.nextInt(4);
		int type = random.nextInt(9);
		int size = radius * 2 + 1;

		if (new RuinStructureGenerator(getStructureName(), radius, skip, type).generate(world, random, pos)) {
			BlockPos start = new BlockPos(pos.getX() - radius, pos.getY(), pos.getZ() - radius);

			AssortedWorld.LOGGER.info("Generated ruin at : " + start.toString());

			// save it
			addBBSave(world, new MutableBoundingBox(start.getX(), start.getY() - radius, start.getZ(), start.getX() + size, start.getY() + size, start.getZ() + size));
			return true;
		}
		AssortedWorld.LOGGER.info("Failed to generate ruin : " + pos.toString());
		return false;
	}

	@Override
	protected boolean canGenerateInChunk(ServerWorld world, Random rand, int chunkX, int chunkZ) {
		if (rand.nextInt(1) == 0) {
			BlockPos pos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
			Category biome = world.getBiomeManager().getBiome(pos).getCategory();

			return true;// biome == Category.PLAINS;
		}

		return false;
	}

	@Override
	protected boolean canGenerate() {
		return WorldConfig.COMMON.ruinChance.get() > 0;
	}

	@Override
	protected String getStructureName() {
		return "Ruins";
	}
}
