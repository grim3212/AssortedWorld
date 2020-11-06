package com.grim3212.assorted.world.common.gen.wip;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public abstract class WorldStructureGenerator {

	/**
	 * The name of the Structure for this Generator
	 */
	protected final String structName;

	public WorldStructureGenerator(String structName) {
		this.structName = structName;
	}

	public abstract boolean generate(ServerWorld worldIn, Random random, BlockPos pos);
}
