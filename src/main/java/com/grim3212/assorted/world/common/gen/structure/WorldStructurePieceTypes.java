package com.grim3212.assorted.world.common.gen.structure;

import com.grim3212.assorted.world.common.gen.structure.snowball.SnowballPiece;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class WorldStructurePieceTypes {

	public static StructurePieceType SNOWBALL = SnowballPiece::new;

	public static void registerStructurePieces() {
		Registry.register(Registry.STRUCTURE_PIECE, "snowball_piece", SNOWBALL);
	}
}
