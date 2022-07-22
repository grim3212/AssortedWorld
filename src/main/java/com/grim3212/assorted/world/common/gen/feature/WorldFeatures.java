package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.AssortedWorld;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WorldFeatures {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, AssortedWorld.MODID);

	public static final RegistryObject<Feature<NoneFeatureConfiguration>> RUIN_FEATURE = FEATURES.register("ruin", () -> new RuinFeature(NoneFeatureConfiguration.CODEC));
	public static final RegistryObject<Feature<NoneFeatureConfiguration>> SPIRE_FEATURE = FEATURES.register("spire", () -> new SpireFeature(NoneFeatureConfiguration.CODEC));

}
