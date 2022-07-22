package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.common.handler.WorldConfig;
import com.grim3212.assorted.world.common.util.RuinUtil;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.FluidState;

public class SpireFeature extends Feature<NoneFeatureConfiguration> {

	private int radius;
	private int height;
	private int type;
	private boolean deathSpire;
	private boolean runePlaced;

	public SpireFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		RandomSource rand = context.random();
		BlockPos pos = context.origin();
		WorldGenLevel level = context.level();

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
		if (isAreaClear(level, pos)) {
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
						fillWater(level, newPos);
						generateColumn(level, rand, newPos, distance);
						continue;
					}
					if (distance > radius && rand.nextInt(4) == 1) {
						fillWater(level, newPos);
						clearArea(level, rand, newPos);
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean isAreaClear(WorldGenLevel level, BlockPos pos) {
		int xOff = pos.getX() - radius;
		int zOff = pos.getZ() - radius;
		int size = radius * 2 + 1;
		for (int x = 0; x < size; x++) {
			for (int z = 0; z < size; z++) {
				if (RuinUtil.distanceBetween(pos.getX(), pos.getZ(), xOff + x, zOff + z) > radius) {
					continue;
				}
				BlockPos newPos = level.getHeightmapPos(Types.WORLD_SURFACE_WG, new BlockPos(xOff + x, pos.getY(), zOff + z));
				if (newPos.getY() == 0) {
					return false;
				}
				if (Math.abs(pos.getY() - newPos.getY()) > 7) {
					return false;
				}
				if (level.getMaxLocalRawBrightness(newPos) < 12) {
					return false;
				}
			}

		}

		return true;
	}

	private void fillWater(WorldGenLevel level, BlockPos pos) {
		pos = level.getHeightmapPos(Types.WORLD_SURFACE_WG, pos);
		if (pos.getY() == 0) {
			return;
		}
		int newY = pos.getY();
		for (boolean flag = false; !flag; newY--) {
			BlockPos newPos = new BlockPos(pos.getX(), newY, pos.getZ());
			BlockState state = level.getBlockState(newPos);

			if (state.getBlock() == Blocks.WATER || level.getBlockState(newPos.below()).getBlock() == Blocks.ICE) {
				if (deathSpire) {
					level.setBlock(newPos, Blocks.NETHERRACK.defaultBlockState(), 2);
				} else {
					level.setBlock(newPos, Blocks.STONE.defaultBlockState(), 2);
				}

				continue;
			}
			if (state.canOcclude()) {
				flag = true;
			}
		}
	}

	private void generateColumn(WorldGenLevel level, RandomSource random, BlockPos pos, int heightMod) {
		int startY = pos.getY();

		for (pos = level.getHeightmapPos(Types.WORLD_SURFACE_WG, pos); pos.getY() < startY; pos = pos.above()) {
			if (!runePlaced && (double) random.nextFloat() <= WorldConfig.COMMON.runeChance.get()) {
				runePlaced = true;
				level.setBlock(pos, RuinUtil.randomRune(random).defaultBlockState(), 2);
				continue;
			}

			if (deathSpire) {
				float blockType = random.nextFloat();
				if (blockType < 0.01F) {
					level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 2);
					FluidState fluidstate = level.getFluidState(pos);
					if (!fluidstate.isEmpty()) {
						level.scheduleTick(pos, fluidstate.getType(), 0);
					}
				} else {
					level.setBlock(pos, Blocks.NETHERRACK.defaultBlockState(), 2);
				}
				continue;
			}

			int blockType = random.nextInt(2);
			if (blockType == 1) {
				level.setBlock(pos, Blocks.STONE.defaultBlockState(), 2);
				continue;
			}
			if (blockType == 0) {
				level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 2);
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
			if (!level.isEmptyBlock(pos.above(heightOff)) && !(level.getBlockState(pos.above(heightOff)).getBlock() instanceof LeavesBlock) && !(level.getBlockState(pos.above(heightOff)).is(BlockTags.LOGS))) {
				continue;
			}
			if (deathSpire) {
				int check = random.nextInt(2 + heightOff);
				if (check > startHeight / (heightOff + 1)) {
					break;
				}
				float blockType = random.nextFloat();
				if (blockType < 0.01F) {
					level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 2);
					FluidState fluidstate = level.getFluidState(pos);
					if (!fluidstate.isEmpty()) {
						level.scheduleTick(pos, fluidstate.getType(), 0);
					}
				} else {
					level.setBlock(pos.above(heightOff), Blocks.NETHERRACK.defaultBlockState(), 2);
				}
				continue;
			}
			int check = random.nextInt(2 + heightOff);
			if (check > startHeight / (heightOff + 1)) {
				break;
			}
			level.setBlock(pos.above(heightOff), Blocks.STONE.defaultBlockState(), 2);
		}
	}

	private void clearArea(WorldGenLevel level, RandomSource random, BlockPos pos) {
		int startY = pos.getY();

		for (pos = level.getHeightmapPos(Types.WORLD_SURFACE_WG, pos); pos.getY() < startY; pos = pos.above()) {
			if (deathSpire) {
				float blockType = random.nextFloat();
				if (blockType < 0.01F) {
					level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 2);
					FluidState fluidstate = level.getFluidState(pos);
					if (!fluidstate.isEmpty()) {
						level.scheduleTick(pos, fluidstate.getType(), 0);
					}
				} else {
					level.setBlock(pos, Blocks.NETHERRACK.defaultBlockState(), 2);
				}
				continue;
			}

			int blockType = random.nextInt(2);
			if (blockType == 1) {
				level.setBlock(pos, Blocks.STONE.defaultBlockState(), 2);
				continue;
			}
			if (blockType == 0) {
				level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 2);
			}
		}
		if (pos.getY() > startY + 7) {
			pos = new BlockPos(pos.getX(), startY, pos.getZ());
		}

		for (int off = 0; off < height; off++) {
			if (!level.isEmptyBlock(pos.above(off))) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
			}
		}
	}

}
