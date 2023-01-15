package com.grim3212.assorted.world.common.gen.structure;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.gen.structure.fountain.FountainPiece;
import com.grim3212.assorted.world.common.gen.structure.fountain.FountainStructure;
import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidPiece;
import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidStructure;
import com.grim3212.assorted.world.common.gen.structure.snowball.SnowballPiece;
import com.grim3212.assorted.world.common.gen.structure.snowball.SnowballStructure;
import com.grim3212.assorted.world.common.gen.structure.waterdome.WaterDomePiece;
import com.grim3212.assorted.world.common.gen.structure.waterdome.WaterDomeStructure;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class WorldStructures {
	public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, AssortedWorld.MODID);
	public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(Registries.STRUCTURE_PIECE, AssortedWorld.MODID);

	public static final RegistryObject<StructureType<SnowballStructure>> SNOWBALL_STRUCTURE_TYPE = STRUCTURE_TYPES.register("snowball", () -> () -> SnowballStructure.CODEC);
	public static final RegistryObject<StructurePieceType> SNOWBALL_STRUCTURE_PIECE = STRUCTURE_PIECES.register("snowball_piece", () -> SnowballPiece::new);

	public static final RegistryObject<StructureType<PyramidStructure>> PYRAMID_STRUCTURE_TYPE = STRUCTURE_TYPES.register("pyramid", () -> () -> PyramidStructure.CODEC);
	public static final RegistryObject<StructurePieceType> PYRAMID_STRUCTURE_PIECE = STRUCTURE_PIECES.register("pyramid_piece", () -> PyramidPiece::new);

	public static final RegistryObject<StructureType<FountainStructure>> FOUNTAIN_STRUCTURE_TYPE = STRUCTURE_TYPES.register("fountain", () -> () -> FountainStructure.CODEC);
	public static final RegistryObject<StructurePieceType> FOUNTAIN_STRUCTURE_PIECE = STRUCTURE_PIECES.register("fountain_piece", () -> FountainPiece::new);

	public static final RegistryObject<StructureType<WaterDomeStructure>> WATER_DOME_STRUCTURE_TYPE = STRUCTURE_TYPES.register("water_dome", () -> () -> WaterDomeStructure.CODEC);
	public static final RegistryObject<StructurePieceType> WATER_DOME_STRUCTURE_PIECE = STRUCTURE_PIECES.register("water_dome_piece", () -> WaterDomePiece::new);

	public static boolean validBiomeOnTop(Structure.GenerationContext context, Heightmap.Types type) {
		int i = context.chunkPos().getMiddleBlockX();
		int j = context.chunkPos().getMiddleBlockZ();
		int k = context.chunkGenerator().getFirstOccupiedHeight(i, j, type, context.heightAccessor(), context.randomState());
		Holder<Biome> holder = context.chunkGenerator().getBiomeSource().getNoiseBiome(QuartPos.fromBlock(i), QuartPos.fromBlock(k), QuartPos.fromBlock(j), context.randomState().sampler());
		return context.validBiome().test(holder);
	}
}
