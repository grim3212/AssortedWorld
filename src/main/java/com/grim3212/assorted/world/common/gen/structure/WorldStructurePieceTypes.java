package com.grim3212.assorted.world.common.gen.structure;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;

public class WorldStructurePieceTypes {

	public static IStructurePieceType PYRAMID = PyramidStructurePiece::new;

	public static void registerStructurePieces() {
		Registry.register(Registry.STRUCTURE_PIECE, "pyramid_piece", PYRAMID);
	}
}
