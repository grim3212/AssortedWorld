package com.grim3212.assorted.world.common.gen.structure.snowball;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.grim3212.assorted.world.common.gen.structure.WorldStructurePieceTypes;
import com.grim3212.assorted.world.common.util.RuinUtil;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.ScatteredStructurePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SnowballStructurePiece extends ScatteredStructurePiece {

	private final int radius;

	private List<BlockPos> centrePoints;
	private List<Integer> radii;

	public SnowballStructurePiece(Random random, BlockPos pos, int radius) {
		super(WorldStructurePieceTypes.WATERDOME, random, pos.getX(), pos.getY(), pos.getZ(), radius * 2, radius, radius * 2);
		this.radius = radius;
	}

	public SnowballStructurePiece(TemplateManager template, CompoundNBT tagCompound) {
		super(WorldStructurePieceTypes.WATERDOME, tagCompound);
		this.radius = tagCompound.getInt("radius");
	}

	@Override
	protected void readAdditional(CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		tagCompound.putInt("radius", this.radius);
	}

	@Override
	public boolean func_230383_a_(ISeedReader reader, StructureManager structureManager, ChunkGenerator generator, Random rand, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos pos) {
		this.centrePoints = Lists.newArrayList(BlockPos.ZERO);
		this.radii = Lists.newArrayList(radius);

		int rad = radius;
		int newY = 0;

		for (int i = 0; i < 2 + rand.nextInt(6); i++) {
			int off = newY + rad;
			rad -= 3;
			newY = off;
			if (rad < 3 || pos.getY() + newY + rad >= 128) {
				break;
			}

			centrePoints.add(new BlockPos(0, off, 0));
			radii.add(rad);
		}

		Map<BlockPos, Block> blockCache = new HashMap<>();

		for (int idx = 0; idx < centrePoints.size(); idx++) {
			BlockPos point = centrePoints.get(idx);
			int radi = radii.get(idx);

			for (int x = -radi; x <= radi; x++) {
				for (int z = -radi; z <= radi; z++) {
					for (int y = -radi; y <= radi; y++) {
						BlockPos newPoint = new BlockPos(x, y + point.getY(), z);

						if (pos.getY() + (int) newPoint.getY() >= 128) {
							break;
						}
						Block block = blockToPlace(rand, pos, newPoint, point);
						if (block != null) {
							blockCache.put(new BlockPos(pos.getX() + x, pos.getY() + newPoint.getY(), pos.getZ() + z), block);
						}
					}
				}
			}
		}

		blockCache.forEach((p, b) -> reader.setBlockState(p, b.getDefaultState(), 2));

		return true;
	}

	private Block blockToPlace(Random random, BlockPos pos, BlockPos point1, BlockPos point2) {
		int points = 0;
		int places = 0;
		for (int point = 0; point < centrePoints.size(); point++) {
			BlockPos centerPoint = centrePoints.get(point);
			int radi = radii.get(point);

			int distance = (int) Math.round(Math.sqrt(centerPoint.distanceSq(point1)));
			if (distance < radi) {
				places++;
				continue;
			}
			if (distance != radi) {
				continue;
			}
			points++;
		}

		if (places > 0) {
			if (point1.getX() == 0 && point1.getY() == 0 && point1.getZ() == 0) {
				return RuinUtil.randomRune(random);
			}

			if (point1.getX() == 0 && point1.getZ() == 0) {
				return Blocks.ICE;
			}
			if (point2.getY() == 0) {
				if ((Math.abs(point1.getX()) == 1 || Math.abs(point1.getZ()) == 1) && Math.abs(point1.getX()) != Math.abs(point1.getZ()) && Math.abs(point1.getX()) <= 1 && Math.abs(point1.getZ()) <= 1) {
					return Blocks.ICE;
				}
			} else if ((Math.abs(point1.getX()) == 0 || Math.abs(point1.getZ()) == 0) && point1.getY() - point2.getY() == 0) {
				return Blocks.ICE;
			}
			if (point2.getY() == 0 && (double) pos.getY() + point1.getY() < (double) pos.getY()) {
				return Blocks.ICE;
			} else {
				return Blocks.AIR;
			}
		}
		if (points > 0) {
			return Blocks.SNOW_BLOCK;
		} else {
			return null;
		}
	}
}
