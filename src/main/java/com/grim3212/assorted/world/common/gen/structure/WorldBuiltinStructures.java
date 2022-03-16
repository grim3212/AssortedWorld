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
	
	public static Holder<ConfiguredStructureFeature<?, ?>> SNOWBALL;
	public static Holder<StructureSet> SNOWBALLS;

	public static void registerConfiguredStructures() {
		SNOWBALL = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, WorldBuiltinStructures.SNOWBALL_CONFIGURED_STRUCTURE_FEATURE, WorldStructureFeatures.SNOWBALL.get().configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_IGLOO));
		
		SNOWBALLS = register(SNOWBALL_STRUCTURE_SET, SNOWBALL, new RandomSpreadStructurePlacement(WorldConfig.COMMON.snowBallMaxChunkDistance.get(), (int) (WorldConfig.COMMON.snowBallMaxChunkDistance.get() * 0.42f), RandomSpreadType.LINEAR, 737462782));
	}
	
	
//	public static void register
	
	

	static Holder<StructureSet> register(ResourceKey<StructureSet> key, StructureSet set) {
		return BuiltinRegistries.register(BuiltinRegistries.STRUCTURE_SETS, key, set);
	}

	static Holder<StructureSet> register(ResourceKey<StructureSet> key, Holder<ConfiguredStructureFeature<?, ?>> feature, StructurePlacement placement) {
		return register(key, new StructureSet(feature, placement));
	}

	public static final ResourceKey<ConfiguredStructureFeature<?, ?>> SNOWBALL_CONFIGURED_STRUCTURE_FEATURE = createFeatureKey("snowball");
	public static final ResourceKey<StructureSet> SNOWBALL_STRUCTURE_SET = createStructureSetKey("snowball");

	private static ResourceKey<ConfiguredStructureFeature<?, ?>> createFeatureKey(String s) {
		return ResourceKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, prefix(s));
	}

	private static ResourceKey<StructureSet> createStructureSetKey(String s) {
		return ResourceKey.create(Registry.STRUCTURE_SET_REGISTRY, prefix(s));
	}
	
	static ResourceLocation prefix(String s) {
		return new ResourceLocation(AssortedWorld.MODID, s);
	}
}
