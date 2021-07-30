package com.grim3212.assorted.world.common.lib;

import com.grim3212.assorted.world.AssortedWorld;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class WorldTags {

	public static class Blocks {
		public static final IOptionalNamedTag<Block> ORES_RANDOMITE = forgeTag("ores/randomite");
		public static final Tag.Named<Block> RUNES = tag("runes");

		private static IOptionalNamedTag<Block> forgeTag(String name) {
			return BlockTags.createOptional(new ResourceLocation("forge", name));
		}

		private static Tag.Named<Block> tag(String name) {
			return BlockTags.bind(prefix(name).toString());
		}
	}

	public static class Items {
		public static final IOptionalNamedTag<Item> ORES_RANDOMITE = forgeTag("ores/randomite");
		public static final Tag.Named<Item> RUNES = tag("runes");

		private static IOptionalNamedTag<Item> forgeTag(String name) {
			return ItemTags.createOptional(new ResourceLocation("forge", name));
		}

		private static Tag.Named<Item> tag(String name) {
			return ItemTags.bind(prefix(name).toString());
		}
	}

	private static ResourceLocation prefix(String name) {
		return new ResourceLocation(AssortedWorld.MODID, name);
	}
}
