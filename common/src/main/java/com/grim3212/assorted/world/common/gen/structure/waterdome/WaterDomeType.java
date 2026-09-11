package com.grim3212.assorted.world.common.gen.structure.waterdome;

import com.grim3212.assorted.world.api.WorldLootTables;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * The material a dome's ribbing is built from, and the chest loot that comes with it. Cobblestone
 * is 17 in 20 and each precious variant 1 in 20. Rolled once per dome and saved with the piece, so
 * every chunk pass agrees.
 */
public enum WaterDomeType {
    COBBLESTONE(Blocks.COBBLESTONE, WorldLootTables.CHESTS_WATER_DOME_COBBLESTONE),
    GLOWSTONE(Blocks.GLOWSTONE, WorldLootTables.CHESTS_WATER_DOME_GLOWSTONE),
    IRON(Blocks.IRON_BLOCK, WorldLootTables.CHESTS_WATER_DOME_IRON),
    OBSIDIAN(Blocks.OBSIDIAN, WorldLootTables.CHESTS_WATER_DOME_OBSIDIAN);

    private static final WaterDomeType[] VALUES = values();

    private final Block ribBlock;
    private final ResourceKey<LootTable> chestLoot;

    WaterDomeType(Block ribBlock, ResourceKey<LootTable> chestLoot) {
        this.ribBlock = ribBlock;
        this.chestLoot = chestLoot;
    }

    /** The block used for the dome's ribs — the shell positions that are not glass. */
    public Block ribBlock() {
        return this.ribBlock;
    }

    public ResourceKey<LootTable> chestLoot() {
        return this.chestLoot;
    }

    public static WaterDomeType random(RandomSource random) {
        return switch (random.nextInt(20)) {
            case 5 -> GLOWSTONE;
            case 10 -> IRON;
            case 15 -> OBSIDIAN;
            default -> COBBLESTONE;
        };
    }

    /** Reads back what {@link #ordinal()} wrote, falling back to the common dome. */
    public static WaterDomeType byOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : COBBLESTONE;
    }
}
