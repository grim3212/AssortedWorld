package com.grim3212.assorted.world.common.data;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.lib.WorldTags;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class WorldItemTagProvider extends ItemTagsProvider {

	public WorldItemTagProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagProvider, AssortedWorld.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		this.copy(WorldTags.Blocks.ORES_RANDOMITE, WorldTags.Items.ORES_RANDOMITE);
		this.copy(WorldTags.Blocks.RUNES, WorldTags.Items.RUNES);

	}

	@Override
	public String getName() {
		return "Assorted World item tags";
	}
}
