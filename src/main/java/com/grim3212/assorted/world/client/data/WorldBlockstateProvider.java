package com.grim3212.assorted.world.client.data;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class WorldBlockstateProvider extends BlockStateProvider {

	public WorldBlockstateProvider(DataGenerator generator, ExistingFileHelper exFileHelper) {
		super(generator, AssortedWorld.MODID, exFileHelper);
	}

	@Override
	public String getName() {
		return "Assorted World block states";
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(WorldBlocks.RANDOMITE_ORE.get());
	}
}
