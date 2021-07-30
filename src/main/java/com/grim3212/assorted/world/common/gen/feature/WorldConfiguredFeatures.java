package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.handler.WorldConfig;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.HeightmapConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class WorldConfiguredFeatures {

	public static final RangeDecoratorConfiguration RANGE_BOTTOM_TO_164 = new RangeDecoratorConfiguration(UniformHeight.of(VerticalAnchor.bottom(), VerticalAnchor.absolute(164)));
	public static final ConfiguredDecorator<?> HEIGHTMAP_SQUARED = FeatureDecorator.HEIGHTMAP.configured(new HeightmapConfiguration(Heightmap.Types.MOTION_BLOCKING)).squared();

	public static ConfiguredFeature<?, ?> RUIN = WorldFeatures.RUIN.get().configured(NoneFeatureConfiguration.NONE).decorated(HEIGHTMAP_SQUARED).rarity(WorldConfig.COMMON.ruinChance.get()).count(WorldConfig.COMMON.ruinTries.get());
	public static ConfiguredFeature<?, ?> SPIRE = WorldFeatures.SPIRE.get().configured(NoneFeatureConfiguration.NONE).decorated(HEIGHTMAP_SQUARED).rarity(WorldConfig.COMMON.spireChance.get()).count(WorldConfig.COMMON.spireTries.get());
	public static ConfiguredFeature<?, ?> ORE_RANDOMITE = Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, WorldBlocks.RANDOMITE_ORE.get().defaultBlockState(), 8)).range(RANGE_BOTTOM_TO_164).squared().count(12);

	public static void registerConfiguredFeatures() {
		Registry<ConfiguredFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_FEATURE;
		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "ruin"), RUIN);
		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "spire"), SPIRE);
		Registry.register(registry, new ResourceLocation(AssortedWorld.MODID, "ore_randomite"), ORE_RANDOMITE);
	}
}
