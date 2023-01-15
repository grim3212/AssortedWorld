package com.grim3212.assorted.world.common.data;

import java.util.concurrent.CompletableFuture;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.lib.WorldTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class WorldBlockTagProvider extends BlockTagsProvider {

	public WorldBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, AssortedWorld.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		this.tag(Tags.Blocks.ORES).addTag(WorldTags.Blocks.ORES_RANDOMITE);
		this.tag(WorldTags.Blocks.ORES_RANDOMITE).add(WorldBlocks.RANDOMITE_ORE.get(), WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get());
		this.tag(WorldTags.Blocks.RUNES).add(WorldBlocks.runeBlocks());

		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(WorldBlocks.runeBlocks());
		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(WorldBlocks.RANDOMITE_ORE.get(), WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get());
		this.tag(BlockTags.NEEDS_STONE_TOOL).add(WorldBlocks.RANDOMITE_ORE.get(), WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get());

		this.tag(BlockTags.MINEABLE_WITH_AXE).add(WorldBlocks.GUNPOWDER_REED.get());
	}

	@Override
	public String getName() {
		return "Assorted World block tags";
	}
}
