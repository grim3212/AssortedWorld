package com.grim3212.assorted.world.common.gen.feature;

import java.util.function.Supplier;

import com.grim3212.assorted.world.AssortedWorld;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldFeatures {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, AssortedWorld.MODID);

	public static final RegistryObject<Feature<NoFeatureConfig>> RUIN = createFeature("ruin", () -> new RuinFeature(NoFeatureConfig.CODEC));
	public static final RegistryObject<Feature<NoFeatureConfig>> SPIRE = createFeature("spire", () -> new SpireFeature(NoFeatureConfig.CODEC));

	private static <F extends Feature<?>> RegistryObject<F> createFeature(String name, Supplier<F> feature) {
		return FEATURES.register(name, feature);
	}
}
