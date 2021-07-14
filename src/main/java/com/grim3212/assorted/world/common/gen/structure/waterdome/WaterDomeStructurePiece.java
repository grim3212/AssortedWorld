package com.grim3212.assorted.world.common.gen.structure.waterdome;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.grim3212.assorted.world.common.gen.structure.WorldStructurePieceTypes;
import com.grim3212.assorted.world.common.handler.WorldConfig;
import com.grim3212.assorted.world.common.util.RuinUtil;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.ScatteredStructurePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class WaterDomeStructurePiece extends ScatteredStructurePiece {

	private final int radius;

	private boolean runePlaced;

	private List<BlockPos> centrePoints;
	private List<Integer> radii;

	public WaterDomeStructurePiece(Random random, BlockPos pos, int radius) {
		super(WorldStructurePieceTypes.WATERDOME, random, pos.getX(), pos.getY(), pos.getZ(), radius * 2, radius, radius * 2);
		this.radius = radius;
		this.runePlaced = false;
	}

	public WaterDomeStructurePiece(TemplateManager template, CompoundNBT tagCompound) {
		super(WorldStructurePieceTypes.WATERDOME, tagCompound);
		this.radius = tagCompound.getInt("radius");
		this.runePlaced = tagCompound.getBoolean("runePlaced");
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT tagCompound) {
		super.addAdditionalSaveData(tagCompound);
		tagCompound.putInt("radius", this.radius);
		tagCompound.putBoolean("runePlaced", runePlaced);
	}

	@Override
	public boolean postProcess(ISeedReader reader, StructureManager structureManager, ChunkGenerator generator, Random rand, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos pos) {
		this.centrePoints = Lists.newArrayList();
		this.radii = Lists.newArrayList();

		int l = 0;
		int i1;
		for (i1 = 0; l == 0 && i1 == 0; i1 = -1 + rand.nextInt(3)) {
			l = -1 + rand.nextInt(3);
		}

		int rad = 3 + rand.nextInt(radius);
		int xOff = 0;
		int zOff = 0;
		for (int idx = 0; idx < 4 + rand.nextInt(1 + WorldConfig.COMMON.waterDomeTries.get() / 2); idx++) {
			int x = xOff + l * (1 + (1 + rad / 2) + rand.nextInt(1 + rad / 2));
			int z = zOff + i1 * (1 + (1 + rad / 2) + rand.nextInt(1 + rad / 2));
			rad = 3 + rand.nextInt(radius);
			xOff = x;
			zOff = z;
			centrePoints.add(new BlockPos(x, 0.0D, z));
			radii.add(rad);
		}

		for (int pIdx = 0; pIdx < centrePoints.size(); pIdx++) {
			BlockPos point = centrePoints.get(pIdx);
			int radi = radii.get(pIdx);

			// Set type
			int type = rand.nextInt(20);

			BlockPos depth = pos.offset(point.getX(), 0, point.getZ());

			// Get start position
			for (Block b = reader.getBlockState(depth.below()).getBlock(); (b == Blocks.GLASS || b == Blocks.AIR || b == Blocks.IRON_BLOCK || b == Blocks.COBBLESTONE || b == Blocks.GLOWSTONE) && depth.getY() > 0; depth = depth.below(), b = reader.getBlockState(depth).getBlock())
				;

			// Get correct position
			pos = new BlockPos(pos.getX(), depth.getY(), pos.getZ());

			for (int x = -radi; x <= radi; x++) {
				for (int z = -radi; z <= radi; z++) {
					for (int y = -radi; y <= radi; y++) {
						BlockPos newPoint = new BlockPos(x + point.getX(), y, z + point.getZ());

						Block b = blockToPlace(rand, pos, newPoint, type);
						if (b == null) {
							continue;
						}

						Block curBlock = reader.getBlockState(pos.offset(newPoint)).getBlock();
						if (curBlock == Blocks.WATER) {
							reader.setBlock(pos.offset(newPoint), b.defaultBlockState(), 2);
						} else {
							FluidState state = reader.getFluidState(pos.offset(newPoint));
							if (!state.isEmpty() && (state.getType() == Fluids.FLOWING_WATER || state.getType() == Fluids.WATER)) {
								reader.setBlock(pos.offset(newPoint), b.defaultBlockState(), 2);
							}
						}
					}
				}
			}
		}

		return true;
	}

	private Block blockToPlace(Random random, BlockPos pos, BlockPos point1, int type) {
		int blocks = 0;
		int places = 0;
		int equalPoints = 0;
		for (int pIdx = 0; pIdx < centrePoints.size(); pIdx++) {
			BlockPos point = centrePoints.get(pIdx);
			int radi = radii.get(pIdx);

			int distance = (int) Math.round(MathHelper.sqrt(point.distSqr(point1)));
			if (distance < radi) {
				places++;
				if (point1.equals(point) && !runePlaced) {
					runePlaced = true;
					return RuinUtil.randomRune(random);
				}
				continue;
			}
			if (distance != radi) {
				continue;
			}
			blocks++;
			if (point1.getX() == point.getX() || point1.getY() == point.getY() || point1.getZ() == point.getZ()) {
				equalPoints++;
			}
		}

		if (places > 0) {
			if (type % 4 != 0)
				;
			return Blocks.AIR;
		}
		if (blocks > 0) {
			if (equalPoints > 0) {
				if (type == 5) {
					return Blocks.GLOWSTONE;
				}
				if (type == 10) {
					return Blocks.IRON_BLOCK;
				}
				if (type == 15) {
					return Blocks.OBSIDIAN;
				} else {
					return Blocks.COBBLESTONE;
				}
			} else {
				return Blocks.GLASS;
			}
		} else {
			return null;
		}
	}
}
