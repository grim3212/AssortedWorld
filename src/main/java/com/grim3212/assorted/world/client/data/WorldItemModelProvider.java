package com.grim3212.assorted.world.client.data;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

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
		genericBlock(WorldBlocks.RANDOMITE.get());
	}

	private ItemModelBuilder genericBlock(Block b) {
		String name = name(b);
		return withExistingParent(name, prefix("block/" + name));
	}

	private static String name(Block i) {
		return Registry.BLOCK.getKey(i).getPath();
	}

	private ResourceLocation prefix(String name) {
		return new ResourceLocation(AssortedWorld.MODID, name);
	}
}
