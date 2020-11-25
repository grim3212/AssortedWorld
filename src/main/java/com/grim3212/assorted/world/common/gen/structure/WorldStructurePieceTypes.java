package com.grim3212.assorted.world.common.gen.structure;

import com.grim3212.assorted.world.common.gen.structure.fountain.FountainStructurePiece;
import com.grim3212.assorted.world.common.gen.structure.pyramid.PyramidStructurePiece;
import com.grim3212.assorted.world.common.gen.structure.waterdome.WaterDomeStructurePiece;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;

public class WorldStructurePieceTypes {

	public static IStructurePieceType PYRAMID = PyramidStructurePiece::new;
	public static IStructurePieceType FOUNTAIN = FountainStructurePiece::new;
	public static IStructurePieceType WATERDOME = WaterDomeStructurePiece::new;

	public static void registerStructurePieces() {
		Registry.register(Registry.STRUCTURE_PIECE, "pyramid_piece", PYRAMID);
		Registry.register(Registry.STRUCTURE_PIECE, "fountain_piece", FOUNTAIN);
		Registry.register(Registry.STRUCTURE_PIECE, "waterdome_piece", WATERDOME);
	}
}
