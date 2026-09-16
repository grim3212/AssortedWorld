package com.grim3212.assorted.world.api;

import com.grim3212.assorted.world.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class WorldLootTables {

    // Loot tables are addressed by ResourceKey now rather than a raw id.
    public static final ResourceKey<LootTable> CHESTS_FOUNTAIN = create("chests/fountain");
    public static final ResourceKey<LootTable> CHESTS_PYRAMID = create("chests/pyramid");
    public static final ResourceKey<LootTable> CHESTS_RUIN = create("chests/ruin");

    // One table per water dome ribbing material. The material is rolled 1-in-20 for each of the
    // three precious variants, so cobblestone is the everyday dome and the other three are the
    // rare ones; the loot climbs in the same order.
    public static final ResourceKey<LootTable> CHESTS_WATER_DOME_COBBLESTONE = create("chests/water_dome/cobblestone");
    public static final ResourceKey<LootTable> CHESTS_WATER_DOME_GLOWSTONE = create("chests/water_dome/glowstone");
    public static final ResourceKey<LootTable> CHESTS_WATER_DOME_IRON = create("chests/water_dome/iron");
    public static final ResourceKey<LootTable> CHESTS_WATER_DOME_OBSIDIAN = create("chests/water_dome/obsidian");

    // One table per desert well depth tier. Deeper is a longer swim and a better chest.
    public static final ResourceKey<LootTable> CHESTS_DESERT_WELL_10 = create("chests/desert_well/level_10");
    public static final ResourceKey<LootTable> CHESTS_DESERT_WELL_15 = create("chests/desert_well/level_15");
    public static final ResourceKey<LootTable> CHESTS_DESERT_WELL_20 = create("chests/desert_well/level_20");
    public static final ResourceKey<LootTable> CHESTS_DESERT_WELL_25 = create("chests/desert_well/level_25");
    public static final ResourceKey<LootTable> CHESTS_DESERT_WELL_30 = create("chests/desert_well/level_30");

    private static ResourceKey<LootTable> create(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
    }
}
