package com.grim3212.assorted.world.common.gen.wip.structure;

import java.util.Random;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.gen.wip.WorldStructureGenerator;
import com.grim3212.assorted.world.common.handler.WorldConfig;
import com.grim3212.assorted.world.common.lib.WorldLootTables;
import com.grim3212.assorted.world.common.util.RuinUtil;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;

public class RuinStructureGenerator extends WorldStructureGenerator {
	private int radius;
	private int skip;
	private int type;

	private int skipCounter;
	private boolean skipper;
	private int torchSkip;
	private int numTorches;
	private boolean placedChest;
	private boolean placedSpawn;
	private boolean runePlaced;

	public RuinStructureGenerator(String structName, int radius, int skip, int type) {
		super(structName);

		this.radius = radius;
		this.skip = skip;
		this.type = type;

		if (type == 9) {
			type = 7;
		}
		if (type == 7) {
			radius = radius + 2;
		}
		numTorches = 0;
		torchSkip = 0;
		skipCounter = 0;
		skipper = false;
		placedChest = false;
		placedSpawn = false;
		runePlaced = false;
	}

	@Override
	public boolean generate(ServerWorld worldIn, Random random, BlockPos pos) {
		AssortedWorld.LOGGER.info("Generating ruin at : " + pos.toString());

		if (pos.getY() == 0) {
			AssortedWorld.LOGGER.info("Failed y= 0 : " + pos.toString());
			return false;
		}
		AssortedWorld.LOGGER.info("is clear : " + pos.toString());

		if (isAreaClear(worldIn, pos)) {
			AssortedWorld.LOGGER.info("cleared : " + pos.toString());

			int xOff = pos.getX() - radius;
			int zOff = pos.getZ() - radius;
			int size = radius * 2 + 1;

			if (skip != 0) {
				skipCounter = random.nextInt(skip);
			}

			for (int x = 0; x < size; x++) {
				for (int z = 0; z < size; z++) {
					int radOff = 0;
					int rad = (int) ((double) radius * 0.66666666666666663D);
					if (type == 1 || type == 5) {
						radOff = random.nextInt(rad) - random.nextInt(rad * 2);
					}

					BlockPos newPos = new BlockPos(xOff + x, pos.getY(), zOff + z);
					if (RuinUtil.distanceBetween(pos.getX(), pos.getZ(), newPos.getX(), newPos.getZ()) == radius + radOff) {
						// fillWater(worldIn, newPos);
						if (skip != 0) {
							if (!skipper) {
								generateColumn(worldIn, random, newPos);
							}
							if (skipCounter == skip) {
								skipCounter = 0;
								skipper = !skipper;
							} else {
								skipCounter++;
							}
						} else {
							generateColumn(worldIn, random, newPos);
						}
						continue;
					}
					if (RuinUtil.distanceBetween(pos.getX(), pos.getZ(), newPos.getX(), newPos.getZ()) < radius) {
						// fillWater(worldIn, newPos);
						// clearArea(worldIn, random, newPos);
					}
				}
			}

			return true;
		} else {
			AssortedWorld.LOGGER.info("area is not clear : " + pos.toString());
			return false;
		}
	}

	private boolean isAreaClear(ServerWorld world, BlockPos pos) {
		int xOff = pos.getX() - radius;
		int zOff = pos.getZ() - radius;
		int size = radius * 2 + 1;
		for (int x = 0; x < size; x++) {
			for (int z = 0; z < size; z++) {
				if (RuinUtil.distanceBetween(pos.getX(), pos.getZ(), xOff + x, zOff + z) > radius) {
					continue;
				}
				BlockPos newPos = world.getHeight(Type.OCEAN_FLOOR_WG, new BlockPos(xOff + x, pos.getY(), zOff + z));
				if (newPos.getY() == 0) {
					return false;
				}
				if (Math.abs(pos.getY() - newPos.getY()) > 3) {
					return false;
				}
				if (world.getLight(newPos) < 12) {
					return false;
				}
			}

		}

		return true;
	}

	private void fillWater(ServerWorld world, BlockPos pos) {
		pos = world.getHeight(Type.WORLD_SURFACE_WG, pos);
		if (pos.getY() == 0) {
			return;
		}
		int newY = pos.getY();
		for (boolean flag = false; !flag; newY--) {
			BlockPos newPos = new BlockPos(pos.getX(), newY, pos.getZ());
			BlockState state = world.getBlockState(newPos);

			if (state.getBlock() == Blocks.WATER || world.getBlockState(newPos.down()).getBlock() == Blocks.ICE) {
				world.setBlockState(newPos, Blocks.SAND.getDefaultState());
				continue;
			}
			if (state.isSolid()) {
				flag = true;
			}
		}
	}

	private void generateColumn(ServerWorld world, Random random, BlockPos pos) {
		int y = pos.getY();
		pos = world.getHeight(Type.WORLD_SURFACE_WG, pos);
		int topY = pos.getY();

		for (; topY < y; topY++) {
			if (!runePlaced && (double) random.nextFloat() <= WorldConfig.COMMON.runeChance.get()) {
				runePlaced = true;
				world.setBlockState(pos, WorldBlocks.runeBlocks()[random.nextInt(WorldBlocks.runeBlocks().length)].getDefaultState());
				continue;
			}
			int blockType = random.nextInt(30);
			if (blockType <= 13) {
				world.setBlockState(pos, Blocks.MOSSY_COBBLESTONE.getDefaultState());
				continue;
			}
			if (blockType <= 25) {
				world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
			}
		}

		if (topY > y + 3) {
			pos = new BlockPos(pos.getX(), y, pos.getZ());
		}
		for (int off = 0; off < 5; off++) {
			if (!world.isAirBlock(pos.up(off)) && !(world.getBlockState(pos.up(off)).getBlock() instanceof LeavesBlock)) {
				continue;
			}
			int blockType = random.nextInt(30 + off * 10);
			if (blockType < 13) {
				world.setBlockState(pos.up(off), Blocks.MOSSY_COBBLESTONE.getDefaultState());
				continue;
			}
			if (blockType < 25) {
				world.setBlockState(pos.up(off), Blocks.COBBLESTONE.getDefaultState());
				continue;
			}
			BlockState stateUp = world.getBlockState(pos.up(off));
			if (off == 0 || type == 7 || !stateUp.isSolidSide(world, pos.up(off), Direction.UP) || numTorches >= 8) {
				continue;
			}
			if (torchSkip < 8) {
				torchSkip++;
			} else {
				world.setBlockState(pos.up(off), Blocks.TORCH.getDefaultState());
				numTorches++;
				torchSkip = 0;
			}
		}

	}

	private void clearArea(ServerWorld world, Random random, BlockPos pos) {
		int y = pos.getY();
		pos = world.getHeight(Type.WORLD_SURFACE_WG, pos);
		int topY = pos.getY();
		if (type == 2 || type == 6) {
			for (; topY < y; topY++) {
				int blockType = random.nextInt(3);
				if (blockType == 1) {
					world.setBlockState(pos, Blocks.MOSSY_COBBLESTONE.getDefaultState());
					continue;
				}
				if (blockType == 2) {
					world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
				}
			}

		}
		if (topY > y + 3) {
			pos = new BlockPos(pos.getX(), y, pos.getZ());
		}
		for (int off = 0; off < 5; off++) {
			if (!world.isAirBlock(pos.up(off))) {
				world.setBlockState(pos.up(off), Blocks.AIR.getDefaultState());
			}
		}

		if (type == 3 || type == 7) {
			int blockType = random.nextInt(3);
			if (type == 7) {
				blockType = random.nextInt(1);
			}
			if (blockType == 0) {
				world.setBlockState(pos.up(5), Blocks.MOSSY_COBBLESTONE.getDefaultState());
			} else if (blockType == 1) {
				world.setBlockState(pos.up(5), Blocks.COBBLESTONE.getDefaultState());
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

	private void genChest(ServerWorld world, Random random, BlockPos pos) {
		if (!placedChest) {
			world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.byHorizontalIndex(random.nextInt(4))), 2);
			ChestTileEntity tileentitychest = (ChestTileEntity) world.getTileEntity(pos);
			tileentitychest.setLootTable(WorldLootTables.CHESTS_RUIN, random.nextLong());

			placedChest = true;
		}
	}

	private void genMobSpawner(ServerWorld world, Random random, BlockPos pos) {
		if (!placedSpawn) {
			world.setBlockState(pos, Blocks.SPAWNER.getDefaultState(), 2);
			MobSpawnerTileEntity tileentitymobspawner = (MobSpawnerTileEntity) world.getTileEntity(pos);
			EntityType<?> type = RuinUtil.getRandomRuneMob(random);
			if (type == null) {
				type = EntityType.ZOMBIE;
			}

			tileentitymobspawner.getSpawnerBaseLogic().setEntityType(type);
			placedSpawn = true;
		}
	}
}