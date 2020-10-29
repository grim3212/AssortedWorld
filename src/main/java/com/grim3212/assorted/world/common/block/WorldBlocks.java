package com.grim3212.assorted.world.common.block;

import java.util.function.Function;
import java.util.function.Supplier;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.item.WorldItems;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AssortedWorld.MODID);
	public static final DeferredRegister<Item> ITEMS = WorldItems.ITEMS;
	
	public static final RegistryObject<Block> RANDOMITE = register("randomite", () -> new Block(Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.6f, 1.0f)));

	private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup) {
		return register(name, sup, block -> item(block));
	}

	private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup, Function<RegistryObject<T>, Supplier<? extends Item>> itemCreator) {
		RegistryObject<T> ret = registerNoItem(name, sup);
		ITEMS.register(name, itemCreator.apply(ret));
		return ret;
	}

	private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<? extends T> sup) {
		return BLOCKS.register(name, sup);
	}

	private static Supplier<BlockItem> item(final RegistryObject<? extends Block> block) {
		return () -> new BlockItem(block.get(), new Item.Properties().group(AssortedWorld.ASSORTED_WORLD_ITEM_GROUP));
	}
}
