package com.grim3212.assorted.world.common.gen.structure;

import java.util.function.Supplier;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.gen.structure.snowball.SnowballFeature;

import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WorldStructureFeatures {
	public static final DeferredRegister<StructureFeature<?>> STRUCTURE_FEATURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, AssortedWorld.MODID);

	public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> SNOWBALL = register("snowball", () -> (new SnowballFeature(NoneFeatureConfiguration.CODEC)));

	private static <T extends StructureFeature<?>> RegistryObject<T> register(String name, Supplier<T> structure) {
		return STRUCTURE_FEATURES.register(name, structure);
	}

	
	public static void registerBuiltInStructures() {
		
	}
}
