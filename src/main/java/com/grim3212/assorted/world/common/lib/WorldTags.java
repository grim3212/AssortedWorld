package com.grim3212.assorted.world.common.lib;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class WorldTags {

	public static class Blocks {
		public static final IOptionalNamedTag<Block> ORES_RANDOMITE = forgeTag("ores/randomite");

		private static IOptionalNamedTag<Block> forgeTag(String name) {
			return BlockTags.createOptional(new ResourceLocation("forge", name));
		}
	}
	
	public static class Items {
		public static final IOptionalNamedTag<Item> ORES_RANDOMITE = forgeTag("ores/randomite");

		private static IOptionalNamedTag<Item> forgeTag(String name) {
			return ItemTags.createOptional(new ResourceLocation("forge", name));
		}
	}
}
