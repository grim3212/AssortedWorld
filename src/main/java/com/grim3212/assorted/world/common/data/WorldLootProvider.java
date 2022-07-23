package com.grim3212.assorted.world.common.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grim3212.assorted.world.common.block.WorldBlocks;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldLootProvider implements DataProvider {

	private final DataGenerator generator;
	private final List<Block> blocks = new ArrayList<>();

	public WorldLootProvider(DataGenerator generator) {
		this.generator = generator;

		for (Block rune : WorldBlocks.runeBlocks()) {
			this.blocks.add(rune);
		}
	}

	private ResourceLocation key(Block b) {
		return ForgeRegistries.BLOCKS.getKey(b);
	}

	@Override
	public void run(CachedOutput cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		for (Block b : blocks) {
			tables.put(key(b), genRegular(b));
		}

		tables.put(WorldBlocks.GUNPOWDER_REED.getId(), genRegular(WorldBlocks.GUNPOWDER_REED.get()));

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			DataProvider.saveStable(cache, LootTables.serialize(e.getValue().setParamSet(LootContextParamSets.BLOCK).build()), path);
		}

		genRandomite(cache, WorldBlocks.RANDOMITE_ORE.getId());
		genRandomite(cache, WorldBlocks.DEEPSLATE_RANDOMITE_ORE.getId());
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	private static LootTable.Builder genRegular(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private void genRandomite(CachedOutput cache, ResourceLocation block) throws IOException {
		Path randomitePath = getPath(generator.getOutputFolder(), block);
		LootPoolEntryContainer.Builder<?> eggOption = LootItem.lootTableItem(Items.EGG).setWeight(40);
		LootPoolEntryContainer.Builder<?> coalOption = LootItem.lootTableItem(Items.COAL).setWeight(30).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)).apply(ApplyExplosionDecay.explosionDecay());
		LootPoolEntryContainer.Builder<?> slimeOption = LootItem.lootTableItem(Items.SLIME_BALL).setWeight(30).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)).apply(ApplyExplosionDecay.explosionDecay());
		LootPoolEntryContainer.Builder<?> quartzOption = LootItem.lootTableItem(Items.QUARTZ).setWeight(20).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)).apply(ApplyExplosionDecay.explosionDecay());
		LootPoolEntryContainer.Builder<?> ironOption = LootItem.lootTableItem(Blocks.IRON_ORE).setWeight(20);
		LootPoolEntryContainer.Builder<?> goldOption = LootItem.lootTableItem(Blocks.GOLD_ORE).setWeight(18);
		LootPoolEntryContainer.Builder<?> emeraldOption = LootItem.lootTableItem(Items.EMERALD).setWeight(18).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)).apply(ApplyExplosionDecay.explosionDecay());
		LootPoolEntryContainer.Builder<?> redstoneOption = LootItem.lootTableItem(Items.REDSTONE).setWeight(16).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 5.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)).apply(ApplyExplosionDecay.explosionDecay());
		LootPoolEntryContainer.Builder<?> lapisOption = LootItem.lootTableItem(Items.LAPIS_LAZULI).setWeight(15).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 9.0F))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)).apply(ApplyExplosionDecay.explosionDecay());
		LootPoolEntryContainer.Builder<?> diamondOption = LootItem.lootTableItem(Items.DIAMOND).setWeight(10).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE));
		LootPoolEntryContainer.Builder<?> netherDebrisOption = LootItem.lootTableItem(Blocks.ANCIENT_DEBRIS).setWeight(1);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(coalOption).add(ironOption).add(goldOption).add(diamondOption).add(redstoneOption).add(lapisOption).add(slimeOption).add(emeraldOption).add(quartzOption).add(netherDebrisOption).add(eggOption).when(ExplosionCondition.survivesExplosion());

		DataProvider.saveStable(cache, LootTables.serialize(LootTable.lootTable().withPool(pool).setParamSet(LootContextParamSets.BLOCK).build()), randomitePath);
	}

	@Override
	public String getName() {
		return "Assorted World loot tables";
	}

}
