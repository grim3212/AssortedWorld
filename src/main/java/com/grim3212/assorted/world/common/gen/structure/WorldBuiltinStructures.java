package com.grim3212.assorted.world.common.gen.structure;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.handler.WorldConfig;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public class WorldBuiltinStructures {

	public static Holder<StructureSet> SNOWBALLS;
	public static Holder<StructureSet> FOUNTAINS;
	public static Holder<StructureSet> PYRAMIDS;
	public static Holder<StructureSet> WATER_DOMES;

	public static void registerBuiltinStructures() {
		SNOWBALLS = registerBuiltinStructure("snowball", WorldStructureFeatures.SNOWBALL.get().configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_IGLOO), new RandomSpreadStructurePlacement(WorldConfig.COMMON.snowBallSpacing.get(), WorldConfig.COMMON.snowBallSeparation.get(), RandomSpreadType.LINEAR, 737462782));
		FOUNTAINS = registerBuiltinStructure("fountain", WorldStructureFeatures.FOUNTAIN.get().configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_SWAMP_HUT, true), new RandomSpreadStructurePlacement(WorldConfig.COMMON.fountainSpacing.get(), WorldConfig.COMMON.fountainSeparation.get(), RandomSpreadType.LINEAR, 983497234));
		PYRAMIDS = registerBuiltinStructure("pyramid", WorldStructureFeatures.PYRAMID.get().configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_DESERT_PYRAMID), new RandomSpreadStructurePlacement(WorldConfig.COMMON.pyramidSpacing.get(), WorldConfig.COMMON.pyramidSeparation.get(), RandomSpreadType.LINEAR, 827612344));
		WATER_DOMES = registerBuiltinStructure("water_dome", WorldStructureFeatures.WATER_DOME.get().configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.IS_OCEAN), new RandomSpreadStructurePlacement(WorldConfig.COMMON.waterDomeSpacing.get(), WorldConfig.COMMON.waterDomeSeparation.get(), RandomSpreadType.LINEAR, 432432568));
	}

	static Holder<StructureSet> registerBuiltinStructure(String structureName, ConfiguredStructureFeature<?, ?> structureFeature, StructurePlacement placement) {
		final ResourceKey<ConfiguredStructureFeature<?, ?>> featureKey = createFeatureKey(structureName);
		final ResourceKey<StructureSet> structureSetKey = createStructureSetKey(structureName);
		final Holder<ConfiguredStructureFeature<?, ?>> holderStructureFeature = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, featureKey, structureFeature);
		return registerStructureSet(structureSetKey, holderStructureFeature, placement);
	}

	static Holder<StructureSet> registerStructureSet(ResourceKey<StructureSet> key, StructureSet set) {
		return BuiltinRegistries.register(BuiltinRegistries.STRUCTURE_SETS, key, set);
	}

	static Holder<StructureSet> registerStructureSet(ResourceKey<StructureSet> key, Holder<ConfiguredStructureFeature<?, ?>> feature, StructurePlacement placement) {
		return registerStructureSet(key, new StructureSet(feature, placement));
	}

	static ResourceKey<ConfiguredStructureFeature<?, ?>> createFeatureKey(String s) {
		return ResourceKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, prefix(s));
	}

	static ResourceKey<StructureSet> createStructureSetKey(String s) {
		return ResourceKey.create(Registry.STRUCTURE_SET_REGISTRY, prefix(s));
	}

	static ResourceLocation prefix(String s) {
		return new ResourceLocation(AssortedWorld.MODID, s);
	}
}
