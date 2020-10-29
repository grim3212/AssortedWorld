package com.grim3212.assorted.world.common.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grim3212.assorted.world.common.block.WorldBlocks;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.ResourceLocation;

public class WorldLootProvider implements IDataProvider {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator generator;
	private final List<Block> blocks = new ArrayList<>();

	public WorldLootProvider(DataGenerator generator) {
		this.generator = generator;

		this.blocks.add(WorldBlocks.RANDOMITE.get());
	}

	@Override
	public void act(DirectoryCache cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		for (Block b : blocks) {
			tables.put(b.getRegistryName(), genRegular(b));
		}

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			IDataProvider.save(GSON, cache, LootTableManager.toJson(e.getValue().setParameterSet(LootParameterSets.BLOCK).build()), path);
		}
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	private static LootTable.Builder genRegular(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b);
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry).acceptCondition(SurvivesExplosion.builder());
		return LootTable.builder().addLootPool(pool);
	}

	@Override
	public String getName() {
		return "Assorted World loot tables";
	}

}
