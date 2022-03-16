package com.grim3212.assorted.world.common.gen.feature;

import java.util.function.Supplier;

import com.grim3212.assorted.world.AssortedWorld;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WorldFeatures {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, AssortedWorld.MODID);

	public static final RegistryObject<Feature<NoneFeatureConfiguration>> RUIN = createFeature("ruin", () -> new RuinFeature(NoneFeatureConfiguration.CODEC));
	public static final RegistryObject<Feature<NoneFeatureConfiguration>> SPIRE = createFeature("spire", () -> new SpireFeature(NoneFeatureConfiguration.CODEC));

	private static <F extends Feature<?>> RegistryObject<F> createFeature(String name, Supplier<F> feature) {
		return FEATURES.register(name, feature);
	}
}
