package com.grim3212.assorted.world.common.data;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.lib.WorldTags;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class WorldBlockTagProvider extends BlockTagsProvider {

	public WorldBlockTagProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
		super(generatorIn, AssortedWorld.MODID, existingFileHelper);
	}

	@Override
	protected void registerTags() {
		this.getOrCreateBuilder(Tags.Blocks.ORES).addTag(WorldTags.Blocks.ORES_RANDOMITE);
		this.getOrCreateBuilder(WorldTags.Blocks.ORES_RANDOMITE).add(WorldBlocks.RANDOMITE_ORE.get());
	}

	@Override
	public String getName() {
		return "Assorted World block tags";
	}
}
