package com.grim3212.assorted.world.data;

import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WorldBlockLoot extends LibBlockLootProvider {

    private final List<Block> blocks = new ArrayList<>();

    public WorldBlockLoot() {
        super(() -> WorldBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));

        for (Block rune : WorldBlocks.runeBlocks()) {
            this.blocks.add(rune);
        }
    }

    @Override
    public void generate() {
        for (Block b : blocks) {
            this.dropSelf(b);
        }

        this.dropSelf(WorldBlocks.GUNPOWDER_REED.get());

        this.add(WorldBlocks.RANDOMITE_ORE.get(), WorldBlockLoot::createRandomiteTable);
        this.add(WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get(), WorldBlockLoot::createRandomiteTable);
    }

    private static LootTable.Builder createRandomiteTable(Block block) {
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
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(3)).add(coalOption).add(ironOption).add(goldOption).add(diamondOption).add(redstoneOption).add(lapisOption).add(slimeOption).add(emeraldOption).add(quartzOption).add(netherDebrisOption).add(eggOption).when(ExplosionCondition.survivesExplosion());
        return LootTable.lootTable().withPool(pool);
    }
}