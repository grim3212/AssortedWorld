package com.grim3212.assorted.world.common.gen.structure;

import com.grim3212.assorted.world.common.gen.structure.fountain.FountainStructurePiece;
import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidStructurePiece;
import com.grim3212.assorted.world.common.gen.structure.snowball.SnowballStructurePiece;
import com.grim3212.assorted.world.common.gen.structure.waterdome.WaterDomeStructurePiece;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;

public class WorldStructurePieceTypes {

	public static StructurePieceType PYRAMID = PyramidStructurePiece::new;
	public static StructurePieceType FOUNTAIN = FountainStructurePiece::new;
	public static StructurePieceType WATERDOME = WaterDomeStructurePiece::new;
	public static StructurePieceType SNOWBALL = SnowballStructurePiece::new;

	public static void registerStructurePieces() {
		Registry.register(Registry.STRUCTURE_PIECE, "pyramid_piece", PYRAMID);
		Registry.register(Registry.STRUCTURE_PIECE, "fountain_piece", FOUNTAIN);
		Registry.register(Registry.STRUCTURE_PIECE, "waterdome_piece", WATERDOME);
		Registry.register(Registry.STRUCTURE_PIECE, "snowball_piece", SNOWBALL);
	}
}
