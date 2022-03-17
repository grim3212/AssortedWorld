package com.grim3212.assorted.world.common.gen.structure;

import java.util.function.Supplier;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.gen.structure.fountain.FountainFeature;
import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidFeature;
import com.grim3212.assorted.world.common.gen.structure.snowball.SnowballFeature;
import com.grim3212.assorted.world.common.gen.structure.waterdome.WaterDomeFeature;

import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WorldStructureFeatures {
	public static final DeferredRegister<StructureFeature<?>> STRUCTURE_FEATURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, AssortedWorld.MODID);

	public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> SNOWBALL = register("snowball", () -> (new SnowballFeature(NoneFeatureConfiguration.CODEC)));
	public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> FOUNTAIN = register("fountain", () -> (new FountainFeature(NoneFeatureConfiguration.CODEC)));
	public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> PYRAMID = register("pyramid", () -> (new PyramidFeature(NoneFeatureConfiguration.CODEC)));
	public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> WATER_DOME = register("water_dome", () -> (new WaterDomeFeature(NoneFeatureConfiguration.CODEC)));

	private static <T extends StructureFeature<?>> RegistryObject<T> register(String name, Supplier<T> structure) {
		return STRUCTURE_FEATURES.register(name, structure);
	}
}
