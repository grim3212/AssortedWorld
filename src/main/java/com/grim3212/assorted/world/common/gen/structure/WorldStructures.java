package com.grim3212.assorted.world.common.gen.structure;

import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.gen.structure.fountain.FountainStructure;
import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidStructure;
import com.grim3212.assorted.world.common.gen.structure.snowball.SnowballStructure;
import com.grim3212.assorted.world.common.gen.structure.waterdome.WaterDomeStructure;
import com.grim3212.assorted.world.common.handler.WorldConfig;

import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldStructures {
	public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, AssortedWorld.MODID);

	public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> PYRAMID = setupStructure("pyramid", () -> (new PyramidStructure(NoneFeatureConfiguration.CODEC)));
	public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> FOUNTAIN = setupStructure("fountain", () -> (new FountainStructure(NoneFeatureConfiguration.CODEC)));
	public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> WATERDOME = setupStructure("waterdome", () -> (new WaterDomeStructure(NoneFeatureConfiguration.CODEC)));
	public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> SNOWBALL = setupStructure("snowball", () -> (new SnowballStructure(NoneFeatureConfiguration.CODEC)));

	private static <T extends StructureFeature<?>> RegistryObject<T> setupStructure(String name, Supplier<T> structure) {
		return STRUCTURES.register(name, structure);
	}

	public static void setupStructures() {
		setupStructure(PYRAMID.get(), new StructureFeatureConfiguration(WorldConfig.COMMON.pyramidMaxChunkDistance.get(), (int) (WorldConfig.COMMON.pyramidMaxChunkDistance.get() * 0.42f), 323656344), false);
		setupStructure(FOUNTAIN.get(), new StructureFeatureConfiguration(WorldConfig.COMMON.fountainMaxChunkDistance.get(), (int) (WorldConfig.COMMON.fountainMaxChunkDistance.get() * 0.42f), 983497234), true);
		setupStructure(WATERDOME.get(), new StructureFeatureConfiguration(WorldConfig.COMMON.waterDomeMaxChunkDistance.get(), (int) (WorldConfig.COMMON.waterDomeMaxChunkDistance.get() * 0.42f), 432432568), false);
		setupStructure(SNOWBALL.get(), new StructureFeatureConfiguration(WorldConfig.COMMON.snowBallMaxChunkDistance.get(), (int) (WorldConfig.COMMON.snowBallMaxChunkDistance.get() * 0.42f), 737462782), false);
		WorldStructurePieceTypes.registerStructurePieces();
	}

	public static <F extends StructureFeature<?>> void setupStructure(F structure, StructureFeatureConfiguration structureSeparationSettings, boolean transformSurroundingLand) {
		StructureFeature.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString(), structure);

		if (transformSurroundingLand) {
			StructureFeature.NOISE_AFFECTING_FEATURES = ImmutableList.<StructureFeature<?>>builder().addAll(StructureFeature.NOISE_AFFECTING_FEATURES).add(structure).build();
		}

		StructureSettings.DEFAULTS = ImmutableMap.<StructureFeature<?>, StructureFeatureConfiguration>builder().putAll(StructureSettings.DEFAULTS).put(structure, structureSeparationSettings).build();
	}

}
