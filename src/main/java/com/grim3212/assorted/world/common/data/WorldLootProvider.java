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

		for (Block rune : WorldBlocks.runeBlocks()) {
			this.blocks.add(rune);
		}
	}

	@Override
	public void run(DirectoryCache cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		for (Block b : blocks) {
			tables.put(b.getRegistryName(), genRegular(b));
		}

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			IDataProvider.save(GSON, cache, LootTableManager.serialize(e.getValue().setParamSet(LootParameterSets.BLOCK).build()), path);
		}

		genRandomite(cache);
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	private static LootTable.Builder genRegular(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.lootTableItem(b);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(1)).add(entry).when(SurvivesExplosion.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private void genRandomite(DirectoryCache cache) throws IOException {
		Block randomite = WorldBlocks.RANDOMITE_ORE.get();
		Path randomitePath = getPath(generator.getOutputFolder(), randomite.getRegistryName());
		LootEntry.Builder<?> eggOption = ItemLootEntry.lootTableItem(Items.EGG).setWeight(40);
		LootEntry.Builder<?> coalOption = ItemLootEntry.lootTableItem(Items.COAL).setWeight(30).apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE)).apply(ExplosionDecay.explosionDecay());
		LootEntry.Builder<?> slimeOption = ItemLootEntry.lootTableItem(Items.SLIME_BALL).setWeight(30).apply(SetCount.setCount(RandomValueRange.between(2.0F, 3.0F))).apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE)).apply(ExplosionDecay.explosionDecay());
		LootEntry.Builder<?> quartzOption = ItemLootEntry.lootTableItem(Items.QUARTZ).setWeight(20).apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE)).apply(ExplosionDecay.explosionDecay());
		LootEntry.Builder<?> ironOption = ItemLootEntry.lootTableItem(Blocks.IRON_ORE).setWeight(20);
		LootEntry.Builder<?> goldOption = ItemLootEntry.lootTableItem(Blocks.GOLD_ORE).setWeight(18);
		LootEntry.Builder<?> emeraldOption = ItemLootEntry.lootTableItem(Items.EMERALD).setWeight(18).apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE)).apply(ExplosionDecay.explosionDecay());
		LootEntry.Builder<?> redstoneOption = ItemLootEntry.lootTableItem(Items.REDSTONE).setWeight(16).apply(SetCount.setCount(RandomValueRange.between(4.0F, 5.0F))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)).apply(ExplosionDecay.explosionDecay());
		LootEntry.Builder<?> lapisOption = ItemLootEntry.lootTableItem(Items.LAPIS_LAZULI).setWeight(15).apply(SetCount.setCount(RandomValueRange.between(4.0F, 9.0F))).apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE)).apply(ExplosionDecay.explosionDecay());
		LootEntry.Builder<?> diamondOption = ItemLootEntry.lootTableItem(Items.DIAMOND).setWeight(10).apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE));
		LootEntry.Builder<?> netherDebrisOption = ItemLootEntry.lootTableItem(Blocks.ANCIENT_DEBRIS).setWeight(1);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantRange.exactly(1)).add(coalOption).add(ironOption).add(goldOption).add(diamondOption).add(redstoneOption).add(lapisOption).add(slimeOption).add(emeraldOption).add(quartzOption).add(netherDebrisOption).add(eggOption).when(SurvivesExplosion.survivesExplosion());

		IDataProvider.save(GSON, cache, LootTableManager.serialize(LootTable.lootTable().withPool(pool).setParamSet(LootParameterSets.BLOCK).build()), randomitePath);
	}

	@Override
	public String getName() {
		return "Assorted World loot tables";
	}

}
