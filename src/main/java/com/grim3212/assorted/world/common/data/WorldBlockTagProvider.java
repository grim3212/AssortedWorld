package com.grim3212.assorted.world.common.data;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.lib.WorldTags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class WorldBlockTagProvider extends BlockTagsProvider {

	public WorldBlockTagProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
		super(generatorIn, AssortedWorld.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(Tags.Blocks.ORES).addTag(WorldTags.Blocks.ORES_RANDOMITE);
		this.tag(WorldTags.Blocks.ORES_RANDOMITE).add(WorldBlocks.RANDOMITE_ORE.get());
		this.tag(WorldTags.Blocks.RUNES).add(WorldBlocks.runeBlocks());

		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(WorldBlocks.runeBlocks());
		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(WorldBlocks.RANDOMITE_ORE.get());
		this.tag(BlockTags.NEEDS_STONE_TOOL).add(WorldBlocks.RANDOMITE_ORE.get());
	}

	@Override
	public String getName() {
		return "Assorted World block tags";
	}
}
