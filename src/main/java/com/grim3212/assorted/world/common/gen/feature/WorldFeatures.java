package com.grim3212.assorted.world.common.gen.feature;

import java.util.function.Supplier;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.gen.wip.structure.RuinStructure;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldFeatures {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, AssortedWorld.MODID);

	public static final RegistryObject<Feature<NoFeatureConfig>> RUIN = createFeature("ruin", () -> new RuinFeature(NoFeatureConfig.field_236558_a_));
	public static final RegistryObject<Feature<NoFeatureConfig>> PYRAMID = createFeature("pyramid", () -> new PyramidFeature(NoFeatureConfig.field_236558_a_));
	
	
	public static final RegistryObject<Feature<NoFeatureConfig>> RUIN_TEST = createFeature("ruin_test", () -> new RuinStructure(NoFeatureConfig.field_236558_a_));

	private static <F extends Feature<?>> RegistryObject<F> createFeature(String name, Supplier<F> feature) {
		return FEATURES.register(name, feature);
	}
}
