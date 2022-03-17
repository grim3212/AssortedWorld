package com.grim3212.assorted.world.common.gen.structure;

import com.grim3212.assorted.world.common.gen.structure.fountain.FountainPiece;
import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidPiece;
import com.grim3212.assorted.world.common.gen.structure.snowball.SnowballPiece;
import com.grim3212.assorted.world.common.gen.structure.waterdome.WaterDomePiece;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class WorldStructurePieceTypes {

	public static StructurePieceType SNOWBALL = SnowballPiece::new;
	public static StructurePieceType FOUNTAIN = FountainPiece::new;
	public static StructurePieceType PYRAMID = PyramidPiece::new;
	public static StructurePieceType WATER_DOME = WaterDomePiece::new;

	public static void registerStructurePieces() {
		Registry.register(Registry.STRUCTURE_PIECE, "snowball_piece", SNOWBALL);
		Registry.register(Registry.STRUCTURE_PIECE, "fountain_piece", FOUNTAIN);
		Registry.register(Registry.STRUCTURE_PIECE, "pyramid_piece", PYRAMID);
		Registry.register(Registry.STRUCTURE_PIECE, "water_dome_piece", WATER_DOME);
	}
}
