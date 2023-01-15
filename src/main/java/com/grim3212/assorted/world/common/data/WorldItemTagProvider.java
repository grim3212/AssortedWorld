package com.grim3212.assorted.world.common.data;

import java.util.concurrent.CompletableFuture;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.lib.WorldTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class WorldItemTagProvider extends ItemTagsProvider {

	public WorldItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, TagsProvider<Block> blockTags, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, AssortedWorld.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		this.copy(WorldTags.Blocks.ORES_RANDOMITE, WorldTags.Items.ORES_RANDOMITE);
		this.copy(WorldTags.Blocks.RUNES, WorldTags.Items.RUNES);
	}

	@Override
	public String getName() {
		return "Assorted World item tags";
	}
}
