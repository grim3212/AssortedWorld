package com.grim3212.assorted.world.common.gen.feature;

import java.util.Random;

import com.grim3212.assorted.world.common.handler.WorldConfig;
import com.grim3212.assorted.world.common.lib.WorldLootTables;
import com.grim3212.assorted.world.common.util.RuinUtil;
import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class RuinFeature extends Feature<NoFeatureConfig> {

	private int skipCounter;
	private boolean skipper;
	private int torchSkip;
	private int numTorches;
	private boolean placedChest;
	private boolean placedSpawn;
	private boolean runePlaced;

	private int radius;
	private int skip;
	private int type;

	public RuinFeature(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		this.radius = 3 + rand.nextInt(5);
		this.skip = rand.nextInt(4);
		this.type = rand.nextInt(9);

		if (this.type == 9) {
			this.type = 7;
		}
		if (this.type == 7) {
			this.radius += 2;
		}

		if (pos.getY() == 0) {
			return false;
		}
		if (isAreaClear(reader, pos)) {
			int xOff = pos.getX() - radius;
			int zOff = pos.getZ() - radius;
			int size = radius * 2 + 1;

			if (skip != 0) {
				skipCounter = rand.nextInt(skip);
			}

			for (int x = 0; x < size; x++) {
				for (int z = 0; z < size; z++) {
					int radOff = 0;
					int rad = (int) ((double) radius * 0.66666666666666663D);
					if (type == 1 || type == 5) {
						radOff = rand.nextInt(rad) - rand.nextInt(rad * 2);
					}

					BlockPos newPos = new BlockPos(xOff + x, pos.getY(), zOff + z);
					if (RuinUtil.distanceBetween(pos.getX(), pos.getZ(), newPos.getX(), newPos.getZ()) == radius + radOff) {
						fillWater(reader, newPos);
						if (skip != 0) {
							if (!skipper) {
								generateColumn(reader, rand, newPos);
							}
							if (skipCounter == skip) {
								skipCounter = 0;
								skipper = !skipper;
							} else {
								skipCounter++;
							}
						} else {
							generateColumn(reader, rand, newPos);
						}
						continue;
					}
					if (RuinUtil.distanceBetween(pos.getX(), pos.getZ(), newPos.getX(), newPos.getZ()) < radius) {
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
				BlockPos newPos = world.getHeightmapPos(Type.WORLD_SURFACE_WG, new BlockPos(xOff + x, pos.getY(), zOff + z));
				if (newPos.getY() == 0) {
					return false;
				}
				if (Math.abs(pos.getY() - newPos.getY()) > 3) {
					return false;
				}
				if (world.getMaxLocalRawBrightness(newPos) < 12) {
					return false;
				}
			}

		}

		return true;
	}

	private void fillWater(ISeedReader world, BlockPos pos) {
		pos = world.getHeightmapPos(Type.WORLD_SURFACE_WG, pos);
		if (pos.getY() == 0) {
			return;
		}
		int newY = pos.getY();
		for (boolean flag = false; !flag; newY--) {
			BlockPos newPos = new BlockPos(pos.getX(), newY, pos.getZ());
			BlockState state = world.getBlockState(newPos);

			if (state.getBlock() == Blocks.WATER || world.getBlockState(newPos.below()).getBlock() == Blocks.ICE) {
				world.setBlock(newPos, Blocks.SAND.defaultBlockState(), 2);
				continue;
			}
			if (state.canOcclude()) {
				flag = true;
			}
		}
	}

	private void generateColumn(ISeedReader world, Random random, BlockPos pos) {
		int y = pos.getY();
		pos = world.getHeightmapPos(Type.WORLD_SURFACE_WG, pos);
		int topY = pos.getY();

		for (; topY < y; topY++) {
			if (!runePlaced && (double) random.nextFloat() <= WorldConfig.COMMON.runeChance.get()) {
				runePlaced = true;
				world.setBlock(pos, RuinUtil.randomRune(random).defaultBlockState(), 2);
				continue;
			}
			int blockType = random.nextInt(30);
			if (blockType <= 13) {
				world.setBlock(pos, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
				continue;
			}
			if (blockType <= 25) {
				world.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 2);
			}
		}

		if (topY > y + 3) {
			pos = new BlockPos(pos.getX(), y, pos.getZ());
		}
		for (int off = 0; off < 5; off++) {
			if (!world.isEmptyBlock(pos.above(off)) && !(world.getBlockState(pos.above(off)).getBlock() instanceof LeavesBlock)) {
				continue;
			}
			int blockType = random.nextInt(30 + off * 10);
			if (blockType < 13) {
				world.setBlock(pos.above(off), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
				continue;
			}
			if (blockType < 25) {
				world.setBlock(pos.above(off), Blocks.COBBLESTONE.defaultBlockState(), 2);
				continue;
			}
			BlockState stateUp = world.getBlockState(pos.above(off));
			if (off == 0 || type == 7 || !stateUp.isFaceSturdy(world, pos.above(off), Direction.UP) || numTorches >= 8) {
				continue;
			}
			if (torchSkip < 8) {
				torchSkip++;
			} else {
				world.setBlock(pos.above(off), Blocks.TORCH.defaultBlockState(), 2);
				numTorches++;
				torchSkip = 0;
			}
		}

	}

	private void clearArea(ISeedReader world, Random random, BlockPos pos) {
		int y = pos.getY();
		pos = world.getHeightmapPos(Type.WORLD_SURFACE_WG, pos);
		int topY = pos.getY();
		if (type == 2 || type == 6) {
			for (; topY < y; topY++) {
				int blockType = random.nextInt(3);
				if (blockType == 1) {
					world.setBlock(pos, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
					continue;
				}
				if (blockType == 2) {
					world.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 2);
				}
			}

		}
		if (topY > y + 3) {
			pos = new BlockPos(pos.getX(), y, pos.getZ());
		}
		for (int off = 0; off < 5; off++) {
			if (!world.isEmptyBlock(pos.above(off))) {
				world.setBlock(pos.above(off), Blocks.AIR.defaultBlockState(), 2);
			}
		}

		if (type == 3 || type == 7) {
			int blockType = random.nextInt(3);
			if (type == 7) {
				blockType = random.nextInt(1);
			}
			if (blockType == 0) {
				world.setBlock(pos.above(5), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
			} else if (blockType == 1) {
				world.setBlock(pos.above(5), Blocks.COBBLESTONE.defaultBlockState(), 2);
			}
			int genType = random.nextInt(10);
			if (type == 7) {
				genType = random.nextInt(20);
			}
			if (genType == 1) {
				generateColumn(world, random, pos);
			} else if (genType > 5 && random.nextInt(50) == 1 && type == 7) {
				genMobSpawner(world, random, pos);
				return;
			}
		}
		if (type == 4 || type == 8) {
			if (random.nextInt(5) == 1) {
				generateColumn(world, random, pos);
			}
		}
		if (random.nextInt(250) == 1 && type < 4) {
			genChest(world, random, pos);
		} else if (type == 7 && placedSpawn && random.nextInt(2) == 1) {
			genChest(world, random, pos);
		}
	}

	private void genChest(ISeedReader world, Random random, BlockPos pos) {
		if (!placedChest) {
			world.setBlock(pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.from2DDataValue(random.nextInt(4))), 2);
			ChestTileEntity tileentitychest = (ChestTileEntity) world.getBlockEntity(pos);
			tileentitychest.setLootTable(WorldLootTables.CHESTS_RUIN, random.nextLong());

			placedChest = true;
		}
	}

	private void genMobSpawner(ISeedReader world, Random random, BlockPos pos) {
		if (!placedSpawn) {
			world.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
			MobSpawnerTileEntity tileentitymobspawner = (MobSpawnerTileEntity) world.getBlockEntity(pos);
			EntityType<?> type = RuinUtil.getRandomRuneMob(random);
			if (type == null) {
				type = EntityType.ZOMBIE;
			}

			tileentitymobspawner.getSpawner().setEntityId(type);
			placedSpawn = true;
		}
	}

}
