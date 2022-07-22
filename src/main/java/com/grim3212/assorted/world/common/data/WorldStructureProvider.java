package com.grim3212.assorted.world.common.data;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.gen.WorldBiomeTags;
import com.grim3212.assorted.world.common.gen.structure.fountain.FountainStructure;
import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidStructure;
import com.grim3212.assorted.world.common.gen.structure.snowball.SnowballStructure;
import com.grim3212.assorted.world.common.gen.structure.waterdome.WaterDomeStructure;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;

public class WorldStructureProvider {

	private static final ResourceLocation SNOWBALL_KEY = new ResourceLocation(AssortedWorld.MODID, "snowball");
	private static final ResourceLocation PYRAMID_KEY = new ResourceLocation(AssortedWorld.MODID, "pyramid");
	private static final ResourceLocation FOUNTAIN_KEY = new ResourceLocation(AssortedWorld.MODID, "fountain");
	private static final ResourceLocation WATER_DOME_KEY = new ResourceLocation(AssortedWorld.MODID, "water_dome");

	public static final ResourceKey<Structure> SNOWBALL_RESOURCE_KEY = structureResourceKey(SNOWBALL_KEY);
	public static final ResourceKey<Structure> PYRAMID_RESOURCE_KEY = structureResourceKey(PYRAMID_KEY);
	public static final ResourceKey<Structure> FOUNTAIN_RESOURCE_KEY = structureResourceKey(FOUNTAIN_KEY);
	public static final ResourceKey<Structure> WATER_DOME_RESOURCE_KEY = structureResourceKey(WATER_DOME_KEY);

	private static ResourceKey<Structure> structureResourceKey(ResourceLocation key) {
		return ResourceKey.create(Registry.STRUCTURE_REGISTRY, key);
	}

	public static DataProvider getStructures(final DataGenerator generator, final ExistingFileHelper existingFileHelper, final RegistryAccess registries, final RegistryOps<JsonElement> ops) {
		return JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, AssortedWorld.MODID, ops, Registry.STRUCTURE_REGISTRY, getStructures(registries));
	}

	private static Map<ResourceLocation, Structure> getStructures(RegistryAccess registries) {
		Map<ResourceLocation, Structure> map = new HashMap<>();

		Registry<Biome> biomes = registries.registryOrThrow(Registry.BIOME_REGISTRY);

		map.put(SNOWBALL_KEY, new SnowballStructure(new Structure.StructureSettings(biomes.getOrCreateTag(WorldBiomeTags.HAS_SNOWBALL), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
		map.put(PYRAMID_KEY, new PyramidStructure(new Structure.StructureSettings(biomes.getOrCreateTag(WorldBiomeTags.HAS_PYRAMID), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
		map.put(FOUNTAIN_KEY, new FountainStructure(new Structure.StructureSettings(biomes.getOrCreateTag(WorldBiomeTags.HAS_FOUNTAIN), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
		map.put(WATER_DOME_KEY, new WaterDomeStructure(new Structure.StructureSettings(biomes.getOrCreateTag(WorldBiomeTags.HAS_WATER_DOME), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));

		return map;
	}

	public static DataProvider getStructureSets(final DataGenerator generator, final ExistingFileHelper existingFileHelper, final RegistryAccess registries, final RegistryOps<JsonElement> ops) {
		return JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, AssortedWorld.MODID, ops, Registry.STRUCTURE_SET_REGISTRY, getStructureSets(registries));
	}

	private static Map<ResourceLocation, StructureSet> getStructureSets(RegistryAccess registries) {
		Map<ResourceLocation, StructureSet> map = new HashMap<>();

		Registry<Structure> structures = registries.registryOrThrow(Registry.STRUCTURE_REGISTRY);

		map.put(SNOWBALL_KEY, new StructureSet(structures.getOrCreateHolderOrThrow(SNOWBALL_RESOURCE_KEY), new RandomSpreadStructurePlacement(36, 21, RandomSpreadType.LINEAR, 737462782)));
		map.put(PYRAMID_KEY, new StructureSet(structures.getOrCreateHolderOrThrow(PYRAMID_RESOURCE_KEY), new RandomSpreadStructurePlacement(36, 10, RandomSpreadType.LINEAR, 827612344)));
		map.put(FOUNTAIN_KEY, new StructureSet(structures.getOrCreateHolderOrThrow(FOUNTAIN_RESOURCE_KEY), new RandomSpreadStructurePlacement(32, 10, RandomSpreadType.LINEAR, 983497234)));
		map.put(WATER_DOME_KEY, new StructureSet(structures.getOrCreateHolderOrThrow(WATER_DOME_RESOURCE_KEY), new RandomSpreadStructurePlacement(32, 14, RandomSpreadType.LINEAR, 432432568)));

		return map;
	}

}
