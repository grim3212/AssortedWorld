package com.grim3212.assorted.world.common.gen.placement;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.WorldCommonMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The placement modifier type behind {@link ConfigRarityFilter}, and the part names its json
 * carries. A part is one thing the config can make rarer or turn off on its own.
 */
public class WorldPlacements {

    public static final RegistryProvider<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES = RegistryProvider.create(Registries.PLACEMENT_MODIFIER_TYPE, Constants.MOD_ID);

    public static final IRegistryObject<PlacementModifierType<ConfigRarityFilter>> CONFIG_RARITY = PLACEMENT_MODIFIER_TYPES.register("config_rarity", () -> () -> ConfigRarityFilter.CODEC);

    public static class Parts {
        public static final String FLOATING_ISLAND = "floating_island";
        public static final String DESERT_WELL = "desert_well";
        public static final String WHEAT_FIELD = "wheat_field";
        public static final String SAPLING = "sapling";
        public static final String TREE_STUMP = "tree_stump";
        public static final String CACTUS_FIELD = "cactus_field";
        public static final String SANDSTONE_PILLAR = "sandstone_pillar";
        public static final String SAND_PIT = "sand_pit";
        public static final String MELON = "melon";
    }

    private static final Map<String, Supplier<Integer>> RARITIES = new HashMap<>();

    /** The configured rarity of a part, or 0 - never generate - for a name nothing registered. */
    public static int rarity(String part) {
        Supplier<Integer> rarity = RARITIES.get(part);
        return rarity == null ? 0 : rarity.get();
    }

    public static void init() {
        RARITIES.put(Parts.FLOATING_ISLAND, WorldCommonMod.COMMON_CONFIG.floatingIslandRarity);
        RARITIES.put(Parts.DESERT_WELL, WorldCommonMod.COMMON_CONFIG.desertWellRarity);
        RARITIES.put(Parts.WHEAT_FIELD, WorldCommonMod.COMMON_CONFIG.wheatFieldRarity);
        RARITIES.put(Parts.SAPLING, WorldCommonMod.COMMON_CONFIG.saplingRarity);
        RARITIES.put(Parts.TREE_STUMP, WorldCommonMod.COMMON_CONFIG.treeStumpRarity);
        RARITIES.put(Parts.CACTUS_FIELD, WorldCommonMod.COMMON_CONFIG.cactusFieldRarity);
        RARITIES.put(Parts.SANDSTONE_PILLAR, WorldCommonMod.COMMON_CONFIG.sandstonePillarRarity);
        RARITIES.put(Parts.SAND_PIT, WorldCommonMod.COMMON_CONFIG.sandPitRarity);
        RARITIES.put(Parts.MELON, WorldCommonMod.COMMON_CONFIG.melonRarity);
    }
}
