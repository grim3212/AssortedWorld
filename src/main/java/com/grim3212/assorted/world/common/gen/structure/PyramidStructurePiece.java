package com.grim3212.assorted.world.common.gen.structure;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.lib.WorldLootTables;
import com.grim3212.assorted.world.common.util.RuinUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.ScatteredStructurePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.registries.ForgeRegistries;

public class PyramidStructurePiece extends ScatteredStructurePiece {

	private final int maxHeight;
	private final int type;

	private int placedSpawners;
	private int placedChests;
	private int spawnerSkipCount;
	private List<BlockPos> spawnerList;
	private boolean initialLoad;
	private Map<BlockPos, ResourceLocation> pyramid;

	public PyramidStructurePiece(Random random, BlockPos pos, int maxHeight, int type) {
		super(WorldStructurePieceTypes.PYRAMID, random, pos.getX(), pos.getY(), pos.getZ(), maxHeight, maxHeight, maxHeight);
		this.maxHeight = maxHeight;
		this.type = type;
		this.spawnerList = Lists.newArrayList();
		this.initialLoad = true;
		this.pyramid = Maps.newHashMap();
	}

	public PyramidStructurePiece(TemplateManager template, CompoundNBT tagCompound) {
		super(WorldStructurePieceTypes.PYRAMID, tagCompound);
		this.maxHeight = tagCompound.getInt("maxHeight");
		this.type = tagCompound.getInt("type");
		this.initialLoad = false;
		this.spawnerList = Lists.newArrayList();

		this.pyramid = Maps.newHashMap();
		ListNBT blocks = tagCompound.getList("pyramid", 10);
		for (int i = 0; i < blocks.size(); i++) {
			CompoundNBT blockCompound = (CompoundNBT) blocks.get(i);
			BlockPos pos = NBTUtil.readBlockPos(blockCompound.getCompound("pos"));
			ResourceLocation block = new ResourceLocation(blockCompound.getString("block"));
			this.pyramid.put(pos, block);
		}
	}

	@Override
	protected void readAdditional(CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		tagCompound.putInt("maxHeight", this.maxHeight);
		tagCompound.putInt("type", this.type);

		ListNBT blocks = new ListNBT();
		this.pyramid.forEach((p, s) -> {
			CompoundNBT compound = new CompoundNBT();
			compound.put("pos", NBTUtil.writeBlockPos(p));
			compound.putString("block", s.toString());
			blocks.add(compound);
		});
		tagCompound.put("pyramid", blocks);
	}

	@Override
	public boolean func_230383_a_(ISeedReader reader, StructureManager structureManager, ChunkGenerator generator, Random rand, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos pos) {
		if (!this.isInsideBounds(reader, bb, 0)) {
			return false;
		} else {
			pos = pos.down(maxHeight / 2);

			int halfWidth = halfWidth(maxHeight);
			int colHeight = 0;

			if (initialLoad) {
				BlockPos newPos;
				for (int x = -halfWidth; x <= halfWidth; x++) {
					for (int z = -halfWidth; z <= halfWidth; z++) {
						colHeight = getColumnHeight(x, z);
						for (int y = -1; y <= colHeight; y++) {
							newPos = new BlockPos(x, y, z);

							this.pyramid.put(pos.add(newPos), blockToPlace(rand, newPos, colHeight).getRegistryName());
						}
					}
				}
			}

			// Outside the triple for is actually saving a lot of time
			// 38 size was generating in about ~16s
			// Now it is generating in about ~2s
			this.pyramid.forEach((p, s) -> setBlockState(reader, p, s, rand));
			this.initialLoad = false;

			return true;
		}
	}

	private void setBlockState(IServerWorld world, BlockPos p, ResourceLocation s, Random rand) {
		if (s == Blocks.CHEST.getRegistryName()) {
			setBlockState(world, p, Blocks.CHEST.getDefaultState(), rand);
		} else if (s == Blocks.SPAWNER.getRegistryName()) {
			setBlockState(world, p, Blocks.SPAWNER.getDefaultState(), rand);
		} else if (s == Blocks.SANDSTONE.getRegistryName()) {
			setBlockState(world, p, Blocks.SANDSTONE.getDefaultState(), rand);
		} else if (s == Blocks.SAND.getRegistryName()) {
			setBlockState(world, p, Blocks.SAND.getDefaultState(), rand);
		} else if (s == Blocks.AIR.getRegistryName()) {
			setBlockState(world, p, Blocks.AIR.getDefaultState(), rand);
		} else {
			// Hopefully ends up a bit more performant having this as a fallback
			setBlockState(world, p, ForgeRegistries.BLOCKS.getValue(s).getDefaultState(), rand);
		}
	}

	private void setBlockState(IServerWorld world, BlockPos p, BlockState s, Random rand) {
		world.setBlockState(p, s, 2);

		if (this.initialLoad)
			if (s.getBlock() == Blocks.CHEST) {
				TileEntity te = world.getTileEntity(p);

				if (te instanceof ChestTileEntity) {
					((ChestTileEntity) te).setLootTable(WorldLootTables.CHESTS_PYRAMID, rand.nextLong());
				}

			} else if (s.getBlock() == Blocks.SPAWNER) {
				TileEntity te = world.getTileEntity(p);

				if (te instanceof MobSpawnerTileEntity) {
					EntityType<?> type = RuinUtil.getRandomRuneMob(rand);
					if (type == null) {
						type = EntityType.ZOMBIE;
					}
					((MobSpawnerTileEntity) te).getSpawnerBaseLogic().setEntityType(type);
				}
			}
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