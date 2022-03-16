package com.grim3212.assorted.world.common.lib;

import com.grim3212.assorted.world.AssortedWorld;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class WorldTags {

	public static class Blocks {
		public static final TagKey<Block> ORES_RANDOMITE = forgeTag("ores/randomite");
		public static final TagKey<Block> RUNES = tag("runes");

		private static TagKey<Block> forgeTag(String name) {
			return BlockTags.create(new ResourceLocation("forge", name));
		}

		private static TagKey<Block> tag(String name) {
			return BlockTags.create(prefix(name));
		}
	}

	public static class Items {
		public static final TagKey<Item> ORES_RANDOMITE = forgeTag("ores/randomite");
		public static final TagKey<Item> RUNES = tag("runes");

		private static TagKey<Item> forgeTag(String name) {
			return ItemTags.create(new ResourceLocation("forge", name));
		}

		private static TagKey<Item> tag(String name) {
			return ItemTags.create(prefix(name));
		}
	}

	private static ResourceLocation prefix(String name) {
		return new ResourceLocation(AssortedWorld.MODID, name);
	}
}
