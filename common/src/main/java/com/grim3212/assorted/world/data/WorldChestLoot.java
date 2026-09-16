package com.grim3212.assorted.world.data;

import com.grim3212.assorted.world.api.WorldLootTables;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class WorldChestLoot implements LootTableSubProvider {

    private final HolderLookup.Provider registries;

    public WorldChestLoot(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(WorldLootTables.CHESTS_FOUNTAIN, LootTable.lootTable().withPool(treasurePool().add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(20)).add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE).setWeight(2)).add(EmptyLootItem.emptyItem().setWeight(15))).withPool(fountainJunkPool()));

        output.accept(WorldLootTables.CHESTS_PYRAMID, LootTable.lootTable().withPool(treasurePool().add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(20)).add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE).setWeight(2))).withPool(junkPool()));

        output.accept(WorldLootTables.CHESTS_RUIN, LootTable.lootTable().withPool(treasurePool()).withPool(junkPool()));

        waterDomeChests(output);
        desertWellChests(output);
    }

    /**
     * The five desert well chests, one per depth tier. The shallow wells are the rubbish someone
     * threw down them and the deep ones are what was worth hiding at the bottom of a 30 block shaft.
     * <p>
     * Ported from GrimPack's tables, which rolled counts from 0 and so could hand out a chest full
     * of nothing; these start at 1.
     */
    private void desertWellChests(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(WorldLootTables.CHESTS_DESERT_WELL_10, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(2.0F, 8.0F))
                        .add(LootItem.lootTableItem(Items.BONE).setWeight(20).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.STRING).setWeight(20).apply(count(1.0F, 2.0F)))
                        .add(LootItem.lootTableItem(Items.COBBLESTONE).setWeight(20).apply(count(1.0F, 6.0F)))
                        .add(LootItem.lootTableItem(Items.DIRT).setWeight(20).apply(count(1.0F, 6.0F)))
                        .add(LootItem.lootTableItem(Items.COBWEB).setWeight(10).apply(count(1.0F, 2.0F)))));

        output.accept(WorldLootTables.CHESTS_DESERT_WELL_15, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(2.0F, 8.0F))
                        .add(LootItem.lootTableItem(Items.LEATHER).setWeight(20).apply(count(1.0F, 4.0F)))
                        .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(15).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.SLIME_BALL).setWeight(20).apply(count(1.0F, 4.0F)))
                        .add(LootItem.lootTableItem(Items.WHEAT_SEEDS).setWeight(20).apply(count(1.0F, 4.0F)))));

        output.accept(WorldLootTables.CHESTS_DESERT_WELL_20, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(3.0F, 9.0F))
                        .add(LootItem.lootTableItem(Items.MELON_SEEDS).setWeight(20).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.PUMPKIN_SEEDS).setWeight(20).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(15).apply(count(1.0F, 4.0F)))
                        .add(LootItem.lootTableItem(Items.SLIME_BALL).setWeight(15).apply(count(1.0F, 5.0F)))
                        .add(LootItem.lootTableItem(Items.QUARTZ).setWeight(15).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.FISHING_ROD).setWeight(8))));

        output.accept(WorldLootTables.CHESTS_DESERT_WELL_25, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(3.0F, 10.0F))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(15).apply(count(1.0F, 4.0F)))
                        .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(15).apply(count(1.0F, 6.0F)))
                        .add(LootItem.lootTableItem(Items.MELON_SEEDS).setWeight(20).apply(count(1.0F, 5.0F)))
                        .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(15).apply(count(1.0F, 5.0F)))
                        .add(LootItem.lootTableItem(Items.QUARTZ).setWeight(15).apply(count(1.0F, 4.0F)))
                        .add(LootItem.lootTableItem(Items.BLAZE_ROD).setWeight(10).apply(count(1.0F, 5.0F)))
                        .add(LootItem.lootTableItem(Items.GHAST_TEAR).setWeight(5).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(5).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.BOOK).setWeight(8).apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries)))));

        output.accept(WorldLootTables.CHESTS_DESERT_WELL_30, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4.0F, 12.0F))
                        .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(20).apply(count(1.0F, 7.0F)))
                        .add(LootItem.lootTableItem(Items.BLAZE_ROD).setWeight(15).apply(count(1.0F, 6.0F)))
                        .add(LootItem.lootTableItem(Items.NETHER_WART).setWeight(15).apply(count(1.0F, 5.0F)))
                        .add(LootItem.lootTableItem(Items.QUARTZ).setWeight(15).apply(count(1.0F, 5.0F)))
                        .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(10).apply(count(1.0F, 4.0F)))
                        .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(8).apply(count(1.0F, 4.0F)))
                        .add(LootItem.lootTableItem(Items.EMERALD).setWeight(8).apply(count(1.0F, 4.0F)))
                        .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(8).apply(count(1.0F, 4.0F)))
                        .add(LootItem.lootTableItem(Items.BOOK).setWeight(8).apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries)))
                        .add(LootItem.lootTableItem(Items.DIAMOND_PICKAXE).setWeight(5).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(20.0F, 30.0F))))
                        .add(LootItem.lootTableItem(Items.NETHER_STAR).setWeight(1))));
    }

    /**
     * The four water dome chests, one per ribbing material. The three rare domes carry the payout
     * and the rune odds climb with the tier; all share one seabed junk pool.
     */
    private void waterDomeChests(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(WorldLootTables.CHESTS_WATER_DOME_COBBLESTONE, LootTable.lootTable()
                .withPool(oceanJunkPool(ConstantValue.exactly(4.0F)))
                .withPool(runePool(ConstantValue.exactly(1.0F), 32))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(2.0F, 4.0F))
                        .add(EmptyLootItem.emptyItem().setWeight(25))
                        .add(LootItem.lootTableItem(Items.IRON_NUGGET).setWeight(20).apply(count(2.0F, 6.0F)))
                        .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(15).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(15).apply(count(2.0F, 6.0F)))
                        .add(LootItem.lootTableItem(Items.COAL).setWeight(15).apply(count(1.0F, 6.0F)))
                        .add(LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS).setWeight(10).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.LAPIS_LAZULI).setWeight(10).apply(count(1.0F, 5.0F)))
                        .add(LootItem.lootTableItem(Items.EMERALD).setWeight(5).apply(count(1.0F, 2.0F)))
                        .add(LootItem.lootTableItem(Items.FISHING_ROD).setWeight(5))
                        .add(LootItem.lootTableItem(Items.BOOK).setWeight(5).apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries)))));

        output.accept(WorldLootTables.CHESTS_WATER_DOME_GLOWSTONE, LootTable.lootTable()
                .withPool(oceanJunkPool(ConstantValue.exactly(4.0F)))
                .withPool(runePool(UniformGenerator.between(1.0F, 2.0F), 16))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(3.0F, 5.0F))
                        .add(LootItem.lootTableItem(Items.GLOWSTONE_DUST).setWeight(20).apply(count(4.0F, 10.0F)))
                        .add(LootItem.lootTableItem(Items.LAPIS_LAZULI).setWeight(15).apply(count(4.0F, 9.0F)))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(12).apply(count(2.0F, 5.0F)))
                        .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(12).apply(count(2.0F, 6.0F)))
                        .add(LootItem.lootTableItem(Items.AMETHYST_SHARD).setWeight(10).apply(count(2.0F, 5.0F)))
                        .add(LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS).setWeight(10).apply(count(2.0F, 5.0F)))
                        .add(LootItem.lootTableItem(Items.SEA_LANTERN).setWeight(10).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.EMERALD).setWeight(10).apply(count(1.0F, 4.0F)))
                        .add(LootItem.lootTableItem(Items.BOOK).setWeight(8).apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries)))
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(5))));

        output.accept(WorldLootTables.CHESTS_WATER_DOME_IRON, LootTable.lootTable()
                .withPool(oceanJunkPool(ConstantValue.exactly(4.0F)))
                .withPool(runePool(ConstantValue.exactly(2.0F), 8))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(3.0F, 6.0F))
                        .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(20).apply(count(4.0F, 12.0F)))
                        .add(LootItem.lootTableItem(Items.IRON_BLOCK).setWeight(15).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(15).apply(count(3.0F, 8.0F)))
                        .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(12).apply(count(1.0F, 4.0F)))
                        .add(LootItem.lootTableItem(Items.NAUTILUS_SHELL).setWeight(12).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.EMERALD).setWeight(12).apply(count(2.0F, 6.0F)))
                        .add(LootItem.lootTableItem(Items.SPONGE).setWeight(10).apply(count(1.0F, 4.0F)))
                        .add(LootItem.lootTableItem(Items.BOOK).setWeight(10).apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries)))
                        .add(LootItem.lootTableItem(Items.IRON_PICKAXE).setWeight(8).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(20.0F, 30.0F))))
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(8))
                        .add(LootItem.lootTableItem(Items.DIAMOND_HORSE_ARMOR).setWeight(5))));

        output.accept(WorldLootTables.CHESTS_WATER_DOME_OBSIDIAN, LootTable.lootTable()
                .withPool(oceanJunkPool(ConstantValue.exactly(4.0F)))
                .withPool(runePool(UniformGenerator.between(2.0F, 3.0F), 0))
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4.0F, 7.0F))
                        .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(15).apply(count(3.0F, 8.0F)))
                        .add(LootItem.lootTableItem(Items.OBSIDIAN).setWeight(15).apply(count(4.0F, 12.0F)))
                        .add(LootItem.lootTableItem(Items.EMERALD).setWeight(12).apply(count(4.0F, 10.0F)))
                        .add(LootItem.lootTableItem(Items.NAUTILUS_SHELL).setWeight(12).apply(count(2.0F, 5.0F)))
                        .add(LootItem.lootTableItem(Items.BOOK).setWeight(12).apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries)))
                        .add(LootItem.lootTableItem(Items.ECHO_SHARD).setWeight(8).apply(count(1.0F, 3.0F)))
                        .add(LootItem.lootTableItem(Items.HEART_OF_THE_SEA).setWeight(6))
                        .add(LootItem.lootTableItem(Items.NETHERITE_SCRAP).setWeight(6).apply(count(1.0F, 2.0F)))
                        .add(LootItem.lootTableItem(Items.DIAMOND_SWORD).setWeight(6).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(25.0F, 35.0F))))
                        .add(LootItem.lootTableItem(Items.DIAMOND_BLOCK).setWeight(5).apply(count(1.0F, 2.0F)))
                        .add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE).setWeight(5))
                        .add(LootItem.lootTableItem(Items.TRIDENT).setWeight(4).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(25.0F, 35.0F))))));
    }

    /**
     * Runes on their own roll, so a dome's tier can change how likely they are without disturbing
     * the rest of the table. Every rune is equally weighted (16 in total) and {@code emptyWeight}
     * dilutes them: 32 gives a third of a rune per roll, 0 makes every roll a rune.
     */
    private LootPool.Builder runePool(NumberProvider rolls, int emptyWeight) {
        LootPool.Builder pool = LootPool.lootPool().setRolls(rolls);

        for (Block rune : WorldBlocks.runeBlocks()) {
            pool.add(LootItem.lootTableItem(rune).setWeight(1).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))));
        }

        if (emptyWeight > 0) {
            pool.add(EmptyLootItem.emptyItem().setWeight(emptyWeight));
        }

        return pool;
    }

    /** Silt off the seabed. Shared by all four dome tiers so they still read as the same structure. */
    private LootPool.Builder oceanJunkPool(NumberProvider rolls) {
        return LootPool.lootPool().setRolls(rolls)
                .add(LootItem.lootTableItem(Items.KELP).setWeight(15).apply(count(2.0F, 8.0F)))
                .add(LootItem.lootTableItem(Items.SEAGRASS).setWeight(15).apply(count(2.0F, 8.0F)))
                .add(LootItem.lootTableItem(Items.COD).setWeight(12).apply(count(1.0F, 4.0F)))
                .add(LootItem.lootTableItem(Items.SALMON).setWeight(10).apply(count(1.0F, 4.0F)))
                .add(LootItem.lootTableItem(Items.INK_SAC).setWeight(10).apply(count(1.0F, 5.0F)))
                .add(LootItem.lootTableItem(Items.PRISMARINE_SHARD).setWeight(10).apply(count(1.0F, 5.0F)))
                .add(LootItem.lootTableItem(Items.BONE).setWeight(10).apply(count(1.0F, 6.0F)))
                .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(10).apply(count(1.0F, 6.0F)))
                .add(LootItem.lootTableItem(Items.GLASS_BOTTLE).setWeight(8).apply(count(1.0F, 3.0F)))
                .add(LootItem.lootTableItem(Items.LEATHER).setWeight(8).apply(count(1.0F, 3.0F)));
    }

    private static LootItemConditionalFunction.Builder<?> count(float min, float max) {
        return SetItemCountFunction.setCount(UniformGenerator.between(min, max));
    }

    /**
     * The rune / valuables pool shared by all three chests. Every rune is equally weighted so the
     * odds of any single rune stay independent of how many runes the mod registers.
     */
    private LootPool.Builder treasurePool() {
        LootPool.Builder pool = LootPool.lootPool().setRolls(UniformGenerator.between(2.0F, 4.0F));

        for (Block rune : WorldBlocks.runeBlocks()) {
            pool.add(LootItem.lootTableItem(rune).setWeight(1).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))));
        }

        return pool.add(LootItem.lootTableItem(Items.DIAMOND).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(15).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 5.0F))))
                .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(15).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 7.0F))))
                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(15).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(6).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 6.0F))))
                .add(LootItem.lootTableItem(Items.PUMPKIN_SEEDS).setWeight(25).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 6.0F))))
                .add(LootItem.lootTableItem(Items.MELON_SEEDS).setWeight(25).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 6.0F))))
                .add(LootItem.lootTableItem(Items.BONE).setWeight(25).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 6.0F))))
                .add(LootItem.lootTableItem(Items.SPIDER_EYE).setWeight(25).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(25).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 7.0F))))
                .add(LootItem.lootTableItem(Items.SADDLE).setWeight(20))
                .add(LootItem.lootTableItem(Items.IRON_HORSE_ARMOR).setWeight(15))
                .add(LootItem.lootTableItem(Items.GOLDEN_HORSE_ARMOR).setWeight(10))
                .add(LootItem.lootTableItem(Items.DIAMOND_HORSE_ARMOR).setWeight(5))
                .add(LootItem.lootTableItem(Items.BOOK).setWeight(20).apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries)));
    }

    private LootPool.Builder junkPool() {
        return LootPool.lootPool().setRolls(ConstantValue.exactly(4.0F))
                .add(LootItem.lootTableItem(Items.BONE).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))))
                .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))))
                .add(LootItem.lootTableItem(Items.COBWEB).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))))
                .add(LootItem.lootTableItem(Items.PAPER).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))));
    }

    private LootPool.Builder fountainJunkPool() {
        return LootPool.lootPool().setRolls(ConstantValue.exactly(4.0F))
                .add(LootItem.lootTableItem(Items.BONE).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))))
                .add(LootItem.lootTableItem(Items.GUNPOWDER).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))))
                .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))))
                .add(LootItem.lootTableItem(Items.STRING).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))))
                .add(LootItem.lootTableItem(Items.COBWEB).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))))
                .add(LootItem.lootTableItem(Items.COAL).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 6.0F))))
                .add(LootItem.lootTableItem(Items.TORCH).setWeight(8).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 6.0F))))
                .add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 10.0F))));
    }
}
