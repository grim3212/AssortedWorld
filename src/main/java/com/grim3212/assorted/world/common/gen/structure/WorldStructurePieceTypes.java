package com.grim3212.assorted.world.common.gen.structure;

import java.util.Locale;

import com.grim3212.assorted.world.AssortedWorld;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;

public interface WorldStructurePieceTypes {

	IStructurePieceType PYRAMID = register(PyramidStructurePiece::new, "Pyramid");

	static IStructurePieceType register(IStructurePieceType type, String key) {
		return Registry.register(Registry.STRUCTURE_PIECE, new ResourceLocation(AssortedWorld.MODID, key.toLowerCase(Locale.ROOT)), type);
	}
}
