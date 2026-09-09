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

    private static ResourceKey<LootTable> create(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
    }
}
