package com.grim3212.assorted.world.common.gen.feature;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.collect.Lists;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.lib.WorldLootTables;
import com.grim3212.assorted.world.common.util.RuinUtil;
import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class PyramidFeature extends Feature<NoFeatureConfig> {

	private int maxHeight;
	private int type;

	private int placedSpawners;
	private int placedChests;
	private int spawnerSkipCount;
	private List<BlockPos> spawnerList;

	public PyramidFeature(Codec<NoFeatureConfig> codec) {
		super(codec);
		this.spawnerList = Lists.newArrayList();
	}

	@Override
	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		if (pos.getY() == -1) {
			return false;
		}

		this.maxHeight = 2 * (4 + rand.nextInt(16));
		this.type = rand.nextInt(2);

		pos = pos.down(maxHeight / 2);

		int halfWidth = halfWidth(maxHeight);
		int colHeight = 0;

		BlockPos newPos;
		Map<BlockPos, Block> states = new HashMap<>();

		for (int x = -halfWidth; x <= halfWidth; x++) {
			for (int z = -halfWidth; z <= halfWidth; z++) {
				colHeight = getColumnHeight(x, z);
				for (int y = -1; y <= colHeight; y++) {
					newPos = new BlockPos(x, y, z);

					states.put(pos.add(newPos), blockToPlace(rand, newPos, colHeight));
				}
			}
		}

		// Outside the triple for is actually saving a lot of time
		// 38 size was generating in about ~16s
		// Now it is generating in about ~2s
		Iterator<Entry<BlockPos, Block>> iterator = states.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<BlockPos, Block> entry = iterator.next();
			BlockPos posToPlace = entry.getKey();
			BlockState stateToPlace = entry.getValue().getDefaultState();

			reader.setBlockState(posToPlace, stateToPlace, 2);

			if (stateToPlace.getBlock() == Blocks.CHEST) {
				TileEntity te = reader.getTileEntity(posToPlace);

				if (te instanceof ChestTileEntity) {
					ChestTileEntity tileentitychest = (ChestTileEntity) te;
					tileentitychest.setLootTable(WorldLootTables.CHESTS_PYRAMID, rand.nextLong());
				}

			} else if (stateToPlace.getBlock() == Blocks.SPAWNER) {
				TileEntity te = reader.getTileEntity(posToPlace);

				if (te instanceof MobSpawnerTileEntity) {
					MobSpawnerTileEntity tileentitymobspawner = (MobSpawnerTileEntity) te;
					EntityType<?> type = RuinUtil.getRandomRuneMob(rand);
					if (type == null) {
						type = EntityType.ZOMBIE;
					}
					tileentitymobspawner.getSpawnerBaseLogic().setEntityType(type);
				}
			}

		}

		return true;
	}

	private Block blockToPlace(Random random, BlockPos pos, int colHeight) {
		if (pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0) {
			return randomRune(random);
		}
		if (placeStone(random, pos, colHeight)) {
			if (type == 1) {
				return Blocks.SANDSTONE;
			}
			if (random.nextInt(10) == 2) {
				return Blocks.SAND;
			} else {
				return Blocks.SANDSTONE;
			}
		}
		if (placeSpawner(random, pos)) {
			return Blocks.SPAWNER;
		}
		if (placeChest(random, pos)) {
			return Blocks.CHEST;
		} else {
			return Blocks.AIR;
		}
	}

	private Block randomRune(Random random) {
		return WorldBlocks.runeBlocks()[random.nextInt(WorldBlocks.runeBlocks().length)];
	}

	private boolean placeStone(Random random, BlockPos pos, int colHeight) {
		int y = pos.getY();

		if (y == -1) {
			return true;
		}

		if ((y % 6) == 0 && y > 4) {
			if (type == 1) {
				return random.nextInt(100) < 95;
			} else {
				return random.nextInt(14) < 11;
			}
		}

		if (colHeight - y < 3) {
			if (type == 1) {
				return random.nextInt(100) < 85;
			} else {
				return random.nextInt(5) < 4;
			}
		}

		if (Math.max(Math.abs(pos.getX()), Math.abs(pos.getZ())) % 8 == 0) {
			if (type == 1) {
				return random.nextInt(100) < 92;
			} else {
				return random.nextInt(3) < 2;
			}
		}

		return false;
	}

	private boolean placeSpawner(Random random, BlockPos pos) {
		if ((pos.getY() == 0 || (pos.getY() - 1) % 6 == 0 && pos.getY() > 4) && placedSpawners < maxHeight / 4) {
			if (spawnerSkipCount == 0) {
				if (random.nextInt(98) < 2) {
					boolean flag = false;
					for (int idx = 0; idx < spawnerList.size(); idx++) {
						double d = spawnerList.get(idx).distanceSq(pos.up(pos.getY()));
						if (d < 6D) {
							flag = true;
						}
					}

					if (!flag) {
						placedSpawners++;
						spawnerList.add(pos.up(pos.getY()));
						return true;
					}
				}
				spawnerSkipCount++;
			} else {
				spawnerSkipCount++;
				if (spawnerSkipCount >= 64) {
					spawnerSkipCount = 0;
				}
			}
		}
		return false;
	}

	private boolean placeChest(Random random, BlockPos pos) {
		if ((pos.getY() == 0 || (pos.getY() - 1) % 6 == 0 && pos.getY() > 4) && placedChests < placedSpawners * 2 && random.nextInt(28) < 3) {
			placedChests++;
			return true;
		} else {
			return false;
		}
	}

	private int getColumnHeight(int x, int z) {
		return maxHeight - (Math.max(Math.abs(x), Math.abs(z)) / 2) * 2;
	}

	public static int halfWidth(int height) {
		return 2 * (height / 2) - 1;
	}

}
