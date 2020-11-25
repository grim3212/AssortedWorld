package com.grim3212.assorted.world.common.gen.feature;

import java.util.Random;

import com.grim3212.assorted.world.common.handler.WorldConfig;
import com.grim3212.assorted.world.common.util.RuinUtil;
import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class SpireFeature extends Feature<NoFeatureConfig> {

	private int radius;
	private int height;
	private int type;
	private boolean deathSpire;
	private boolean runePlaced;

	public SpireFeature(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		radius = 2;
		if (WorldConfig.COMMON.spireRadius.get() > 2) {
			radius = rand.nextInt(WorldConfig.COMMON.spireRadius.get() - 2);
		}
		height = (1 + rand.nextInt(WorldConfig.COMMON.spireHeight.get() * 2)) * 5;
		type = rand.nextInt(9);
		deathSpire = rand.nextFloat() < WorldConfig.COMMON.deathSpireChance.get();

		if (pos.getY() == 0) {
			return false;
		}
		if (isAreaClear(reader, pos)) {
			int xOff = pos.getX() - radius;
			int zOff = pos.getZ() - radius;
			int size = radius * 2 + 1;
			for (int x = 0; x < size; x++) {
				for (int z = 0; z < size; z++) {
					int radMod = 0;
					int radCheck = (int) ((double) radius * 0.66666666666666663D);
					if (type == 1 || type == 5) {
						if (radCheck == 0) {
							radCheck = 1;
						}
						radMod = rand.nextInt(radCheck) - rand.nextInt(radCheck * 2);
					}
					BlockPos newPos = new BlockPos(xOff + x, pos.getY(), zOff + z);

					int distance = RuinUtil.distanceBetween(pos.getX(), pos.getZ(), newPos.getX(), newPos.getZ());
					if (distance <= radius + radMod) {
						fillWater(reader, newPos);
						generateColumn(reader, rand, newPos, distance);
						continue;
					}
					if (distance > radius && rand.nextInt(4) == 1) {
						fillWater(reader, newPos);
						clearArea(reader, rand, newPos);
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean isAreaClear(ISeedReader world, BlockPos pos) {
		int xOff = pos.getX() - radius;
		int zOff = pos.getZ() - radius;
		int size = radius * 2 + 1;
		for (int x = 0; x < size; x++) {
			for (int z = 0; z < size; z++) {
				if (RuinUtil.distanceBetween(pos.getX(), pos.getZ(), xOff + x, zOff + z) > radius) {
					continue;
				}
				BlockPos newPos = world.getHeight(Type.WORLD_SURFACE_WG, new BlockPos(xOff + x, pos.getY(), zOff + z));
				if (newPos.getY() == 0) {
					return false;
				}
				if (Math.abs(pos.getY() - newPos.getY()) > 7) {
					return false;
				}
				if (world.getLight(newPos) < 12) {
					return false;
				}
			}

		}

		return true;
	}

	private void fillWater(ISeedReader world, BlockPos pos) {
		pos = world.getHeight(Type.WORLD_SURFACE_WG, pos);
		if (pos.getY() == 0) {
			return;
		}
		int newY = pos.getY();
		for (boolean flag = false; !flag; newY--) {
			BlockPos newPos = new BlockPos(pos.getX(), newY, pos.getZ());
			BlockState state = world.getBlockState(newPos);

			if (state.getBlock() == Blocks.WATER || world.getBlockState(newPos.down()).getBlock() == Blocks.ICE) {
				if (deathSpire) {
					world.setBlockState(newPos, Blocks.NETHERRACK.getDefaultState(), 2);
				} else {
					world.setBlockState(newPos, Blocks.STONE.getDefaultState(), 2);
				}

				continue;
			}
			if (state.isSolid()) {
				flag = true;
			}
		}
	}

	private void generateColumn(ISeedReader world, Random random, BlockPos pos, int heightMod) {
		int startY = pos.getY();

		for (pos = world.getHeight(Type.WORLD_SURFACE_WG, pos); pos.getY() < startY; pos = pos.up()) {
			if (!runePlaced && (double) random.nextFloat() <= WorldConfig.COMMON.runeChance.get()) {
				runePlaced = true;
				world.setBlockState(pos, RuinUtil.randomRune(random).getDefaultState(), 2);
				continue;
			}

			if (deathSpire) {
				float blockType = random.nextFloat();
				if (blockType < 0.01F) {
					world.setBlockState(pos, Blocks.LAVA.getDefaultState(), 2);
					FluidState fluidstate = world.getFluidState(pos);
					if (!fluidstate.isEmpty()) {
						world.getPendingFluidTicks().scheduleTick(pos, fluidstate.getFluid(), 0);
					}
				} else {
					world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 2);
				}
				continue;
			}

			int blockType = random.nextInt(2);
			if (blockType == 1) {
				world.setBlockState(pos, Blocks.STONE.getDefaultState(), 2);
				continue;
			}
			if (blockType == 0) {
				world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
			}
		}

		if (pos.getY() > startY + 7) {
			pos = new BlockPos(pos.getX(), startY, pos.getZ());
		}

		int startHeight = height;
		int mod = (int) ((float) heightMod * (1.0F + random.nextFloat() * 2.0F));
		if (mod == 0) {
			mod = 1;
			startHeight *= 2;
		}
		startHeight /= mod;
		for (int heightOff = 0; heightOff < startHeight; heightOff++) {
			if (!world.isAirBlock(pos.up(heightOff)) && !(world.getBlockState(pos.up(heightOff)).getBlock() instanceof LeavesBlock) && !(BlockTags.LOGS.contains(world.getBlockState(pos.up(heightOff)).getBlock()))) {
				continue;
			}
			if (deathSpire) {
				int check = random.nextInt(2 + heightOff);
				if (check > startHeight / (heightOff + 1)) {
					break;
				}
				float blockType = random.nextFloat();
				if (blockType < 0.01F) {
					world.setBlockState(pos, Blocks.LAVA.getDefaultState(), 2);
					FluidState fluidstate = world.getFluidState(pos);
					if (!fluidstate.isEmpty()) {
						world.getPendingFluidTicks().scheduleTick(pos, fluidstate.getFluid(), 0);
					}
				} else {
					world.setBlockState(pos.up(heightOff), Blocks.NETHERRACK.getDefaultState(), 2);
				}
				continue;
			}
			int check = random.nextInt(2 + heightOff);
			if (check > startHeight / (heightOff + 1)) {
				break;
			}
			world.setBlockState(pos.up(heightOff), Blocks.STONE.getDefaultState(), 2);
		}
	}

	private void clearArea(ISeedReader world, Random random, BlockPos pos) {
		int startY = pos.getY();

		for (pos = world.getHeight(Type.WORLD_SURFACE_WG, pos); pos.getY() < startY; pos = pos.up()) {
			if (deathSpire) {
				float blockType = random.nextFloat();
				if (blockType < 0.01F) {
					world.setBlockState(pos, Blocks.LAVA.getDefaultState(), 2);
					FluidState fluidstate = world.getFluidState(pos);
					if (!fluidstate.isEmpty()) {
						world.getPendingFluidTicks().scheduleTick(pos, fluidstate.getFluid(), 0);
					}
				} else {
					world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 2);
				}
				continue;
			}

			int blockType = random.nextInt(2);
			if (blockType == 1) {
				world.setBlockState(pos, Blocks.STONE.getDefaultState(), 2);
				continue;
			}
			if (blockType == 0) {
				world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
			}
		}
		if (pos.getY() > startY + 7) {
			pos = new BlockPos(pos.getX(), startY, pos.getZ());
		}

		for (int off = 0; off < height; off++) {
			if (!world.isAirBlock(pos.up(off))) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			}
		}
	}

}
