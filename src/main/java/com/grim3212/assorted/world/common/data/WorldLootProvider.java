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
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.ExplosionDecay;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.ResourceLocation;

public class WorldLootProvider implements IDataProvider {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator generator;
	private final List<Block> blocks = new ArrayList<>();

	public WorldLootProvider(DataGenerator generator) {
		this.generator = generator;

		this.blocks.add(WorldBlocks.RANDOMITE_ORE.get());
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
		
		genRandomite(cache);
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	private static LootTable.Builder genRegular(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b);
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry).acceptCondition(SurvivesExplosion.builder());
		return LootTable.builder().addLootPool(pool);
	}
	
	private void genRandomite(DirectoryCache cache) throws IOException {
		Block randomite = WorldBlocks.RANDOMITE_ORE.get();
		Path randomitePath = getPath(generator.getOutputFolder(), randomite.getRegistryName());
		LootEntry.Builder<?> eggOption = ItemLootEntry.builder(Items.EGG).weight(40);
		LootEntry.Builder<?> coalOption = ItemLootEntry.builder(Items.COAL).weight(30).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE)).acceptFunction(ExplosionDecay.builder());
		LootEntry.Builder<?> slimeOption = ItemLootEntry.builder(Items.SLIME_BALL).weight(30).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 3.0F))).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE)).acceptFunction(ExplosionDecay.builder());
		LootEntry.Builder<?> quartzOption = ItemLootEntry.builder(Items.QUARTZ).weight(20).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE)).acceptFunction(ExplosionDecay.builder());
		LootEntry.Builder<?> ironOption = ItemLootEntry.builder(Blocks.IRON_ORE).weight(20);
		LootEntry.Builder<?> goldOption = ItemLootEntry.builder(Blocks.GOLD_ORE).weight(18);
		LootEntry.Builder<?> emeraldOption = ItemLootEntry.builder(Items.EMERALD).weight(18).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE)).acceptFunction(ExplosionDecay.builder());
		LootEntry.Builder<?> redstoneOption = ItemLootEntry.builder(Items.REDSTONE).weight(16).acceptFunction(SetCount.builder(RandomValueRange.of(4.0F, 5.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)).acceptFunction(ExplosionDecay.builder());
		LootEntry.Builder<?> lapisOption = ItemLootEntry.builder(Items.LAPIS_LAZULI).weight(15).acceptFunction(SetCount.builder(RandomValueRange.of(4.0F, 9.0F))).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE)).acceptFunction(ExplosionDecay.builder());
		LootEntry.Builder<?> diamondOption = ItemLootEntry.builder(Items.DIAMOND).weight(10).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE));
		LootEntry.Builder<?> netherDebrisOption = ItemLootEntry.builder(Blocks.ANCIENT_DEBRIS).weight(1);
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1))
				.addEntry(coalOption)
				.addEntry(ironOption)
				.addEntry(goldOption)
				.addEntry(diamondOption)
				.addEntry(redstoneOption)
				.addEntry(lapisOption)
				.addEntry(slimeOption)
				.addEntry(emeraldOption)
				.addEntry(quartzOption)
				.addEntry(netherDebrisOption)
				.addEntry(eggOption)
				.acceptCondition(SurvivesExplosion.builder());
		
		IDataProvider.save(GSON, cache, LootTableManager.toJson(LootTable.builder().addLootPool(pool).setParameterSet(LootParameterSets.BLOCK).build()), randomitePath);
	}

	@Override
	public String getName() {
		return "Assorted World loot tables";
	}

}
