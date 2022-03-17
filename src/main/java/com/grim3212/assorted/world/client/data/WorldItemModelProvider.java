package com.grim3212.assorted.world.client.data;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldItemModelProvider extends ItemModelProvider {

	public WorldItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, AssortedWorld.MODID, existingFileHelper);
	}

	@Override
	public String getName() {
		return "Assorted World item models";
	}

	@Override
	protected void registerModels() {
		genericBlock(WorldBlocks.RANDOMITE_ORE.get());

		for (Block rune : WorldBlocks.runeBlocks()) {
			genericBlock(rune);
		}
	}

	private ItemModelBuilder genericBlock(Block b) {
		String name = name(b);
		return withExistingParent(name, prefix("block/" + name));
	}

	private static String name(Block i) {
		return ForgeRegistries.BLOCKS.getKey(i).getPath();
	}

	private ResourceLocation prefix(String name) {
		return new ResourceLocation(AssortedWorld.MODID, name);
	}
}
