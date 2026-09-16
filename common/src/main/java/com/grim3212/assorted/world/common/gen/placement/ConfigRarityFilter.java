package com.grim3212.assorted.world.common.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

/**
 * Vanilla's {@link net.minecraft.world.level.levelgen.placement.RarityFilter} with the chance read
 * from the config instead of baked into the placed feature json, so a part can be made rarer or
 * turned off (rarity 0) without a datapack. The json only names the part; {@link WorldPlacements}
 * maps that to the config value.
 */
public class ConfigRarityFilter extends PlacementFilter {

    public static final MapCodec<ConfigRarityFilter> CODEC = Codec.STRING.fieldOf("part").xmap(ConfigRarityFilter::new, filter -> filter.part);

    private final String part;

    private ConfigRarityFilter(String part) {
        this.part = part;
    }

    public static ConfigRarityFilter onAverageOnceEvery(String part) {
        return new ConfigRarityFilter(part);
    }

    @Override
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos origin) {
        int rarity = WorldPlacements.rarity(this.part);

        // 0 is off, and so is a part name no config value was registered for - a datapack naming one
        // this version does not have should generate nothing rather than every chunk.
        return rarity > 0 && random.nextFloat() < 1.0F / rarity;
    }

    @Override
    public PlacementModifierType<?> type() {
        return WorldPlacements.CONFIG_RARITY.get();
    }
}
