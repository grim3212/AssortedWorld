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

import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldStructures {
	public static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, AssortedWorld.MODID);

	public static final RegistryObject<Structure<NoFeatureConfig>> PYRAMID = setupStructure("pyramid", () -> (new PyramidStructure(NoFeatureConfig.field_236558_a_)));
	public static final RegistryObject<Structure<NoFeatureConfig>> FOUNTAIN = setupStructure("fountain", () -> (new FountainStructure(NoFeatureConfig.field_236558_a_)));
	public static final RegistryObject<Structure<NoFeatureConfig>> WATERDOME = setupStructure("waterdome", () -> (new WaterDomeStructure(NoFeatureConfig.field_236558_a_)));
	public static final RegistryObject<Structure<NoFeatureConfig>> SNOWBALL = setupStructure("snowball", () -> (new SnowballStructure(NoFeatureConfig.field_236558_a_)));

	private static <T extends Structure<?>> RegistryObject<T> setupStructure(String name, Supplier<T> structure) {
		return STRUCTURES.register(name, structure);
	}

	public static void setupStructures() {
		setupStructure(PYRAMID.get(), new StructureSeparationSettings(WorldConfig.COMMON.pyramidMaxChunkDistance.get(), (int) (WorldConfig.COMMON.pyramidMaxChunkDistance.get() * 0.42f), 323656344), false);
		setupStructure(FOUNTAIN.get(), new StructureSeparationSettings(WorldConfig.COMMON.fountainMaxChunkDistance.get(), (int) (WorldConfig.COMMON.fountainMaxChunkDistance.get() * 0.42f), 983497234), true);
		setupStructure(WATERDOME.get(), new StructureSeparationSettings(WorldConfig.COMMON.waterDomeMaxChunkDistance.get(), (int) (WorldConfig.COMMON.waterDomeMaxChunkDistance.get() * 0.42f), 432432568), false);
		setupStructure(SNOWBALL.get(), new StructureSeparationSettings(WorldConfig.COMMON.snowBallMaxChunkDistance.get(), (int) (WorldConfig.COMMON.snowBallMaxChunkDistance.get() * 0.42f), 737462782), false);
		WorldStructurePieceTypes.registerStructurePieces();
	}

	public static <F extends Structure<?>> void setupStructure(F structure, StructureSeparationSettings structureSeparationSettings, boolean transformSurroundingLand) {
		Structure.NAME_STRUCTURE_BIMAP.put(structure.getRegistryName().toString(), structure);

		if (transformSurroundingLand) {
			Structure.field_236384_t_ = ImmutableList.<Structure<?>>builder().addAll(Structure.field_236384_t_).add(structure).build();
		}

		DimensionStructuresSettings.field_236191_b_ = ImmutableMap.<Structure<?>, StructureSeparationSettings>builder().putAll(DimensionStructuresSettings.field_236191_b_).put(structure, structureSeparationSettings).build();
	}

}
