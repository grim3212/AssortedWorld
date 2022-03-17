package com.grim3212.assorted.world.client.data;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

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

		for (Block rune : WorldBlocks.runeBlocks()) {
			simpleBlock(rune);
		}

		cross(WorldBlocks.GUNPOWDER_REED.get());
	}

	private static String name(Block i) {
		return ForgeRegistries.BLOCKS.getKey(i).getPath();
	}

	private void cross(Block b) {
		String s = name(b);

		getVariantBuilder(b).partialState().setModels(new ConfiguredModel(models().cross(s, blockTexture(b))));
	}
}
