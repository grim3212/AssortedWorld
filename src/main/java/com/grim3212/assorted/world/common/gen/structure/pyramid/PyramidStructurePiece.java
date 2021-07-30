package com.grim3212.assorted.world.common.gen.structure.pyramid;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.grim3212.assorted.world.common.gen.structure.WorldStructurePieceTypes;
import com.grim3212.assorted.world.common.lib.WorldLootTables;
import com.grim3212.assorted.world.common.util.RuinUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraftforge.registries.ForgeRegistries;

public class PyramidStructurePiece extends ScatteredFeaturePiece {

	private final int maxHeight;
	private final int type;

	private int placedSpawners;
	private int placedChests;
	private int spawnerSkipCount;
	private List<BlockPos> spawnerList;
	private boolean initialLoad;
	private Map<BlockPos, ResourceLocation> pyramid;

	public PyramidStructurePiece(Random random, BlockPos pos, int maxHeight, int type) {
		super(WorldStructurePieceTypes.PYRAMID, pos.getX(), pos.getY(), pos.getZ(), maxHeight, maxHeight, maxHeight, getRandomHorizontalDirection(random));
		this.maxHeight = maxHeight;
		this.type = type;
		this.spawnerList = Lists.newArrayList();
		this.initialLoad = true;
		this.pyramid = Maps.newHashMap();
	}

	public PyramidStructurePiece(ServerLevel level, CompoundTag tagCompound) {
		super(WorldStructurePieceTypes.PYRAMID, tagCompound);
		this.maxHeight = tagCompound.getInt("maxHeight");
		this.type = tagCompound.getInt("type");
		this.initialLoad = false;
		this.spawnerList = Lists.newArrayList();

		this.pyramid = Maps.newHashMap();
		ListTag blocks = tagCompound.getList("pyramid", 10);
		for (int i = 0; i < blocks.size(); i++) {
			CompoundTag blockCompound = (CompoundTag) blocks.get(i);
			BlockPos pos = NbtUtils.readBlockPos(blockCompound.getCompound("pos"));
			ResourceLocation block = new ResourceLocation(blockCompound.getString("block"));
			this.pyramid.put(pos, block);
		}
	}

	@Override
	protected void addAdditionalSaveData(ServerLevel level, CompoundTag tagCompound) {
		super.addAdditionalSaveData(level, tagCompound);
		tagCompound.putInt("maxHeight", this.maxHeight);
		tagCompound.putInt("type", this.type);

		ListTag blocks = new ListTag();
		this.pyramid.forEach((p, s) -> {
			CompoundTag compound = new CompoundTag();
			compound.put("pos", NbtUtils.writeBlockPos(p));
			compound.putString("block", s.toString());
			blocks.add(compound);
		});
		tagCompound.put("pyramid", blocks);
	}

	@Override
	public boolean postProcess(WorldGenLevel reader, StructureFeatureManager structureManager, ChunkGenerator generator, Random rand, BoundingBox bb, ChunkPos chunkPos, BlockPos pos) {
		if (!this.updateAverageGroundHeight(reader, bb, 0)) {
			return false;
		} else {
			pos = pos.below(maxHeight / 2);

			int halfWidth = halfWidth(maxHeight);
			int colHeight = 0;

			if (initialLoad) {
				BlockPos newPos;
				for (int x = -halfWidth; x <= halfWidth; x++) {
					for (int z = -halfWidth; z <= halfWidth; z++) {
						colHeight = getColumnHeight(x, z);
						for (int y = -1; y <= colHeight; y++) {
							newPos = new BlockPos(x, y, z);

							this.pyramid.put(pos.offset(newPos), blockToPlace(rand, newPos, colHeight).getRegistryName());
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

	private void setBlockState(ServerLevelAccessor world, BlockPos p, ResourceLocation s, Random rand) {
		if (s == Blocks.CHEST.getRegistryName()) {
			setBlockState(world, p, Blocks.CHEST.defaultBlockState(), rand);
		} else if (s == Blocks.SPAWNER.getRegistryName()) {
			setBlockState(world, p, Blocks.SPAWNER.defaultBlockState(), rand);
		} else if (s == Blocks.SANDSTONE.getRegistryName()) {
			setBlockState(world, p, Blocks.SANDSTONE.defaultBlockState(), rand);
		} else if (s == Blocks.SAND.getRegistryName()) {
			setBlockState(world, p, Blocks.SAND.defaultBlockState(), rand);
		} else if (s == Blocks.AIR.getRegistryName()) {
			setBlockState(world, p, Blocks.AIR.defaultBlockState(), rand);
		} else {
			// Hopefully ends up a bit more performant having this as a fallback
			setBlockState(world, p, ForgeRegistries.BLOCKS.getValue(s).defaultBlockState(), rand);
		}
	}

	private void setBlockState(ServerLevelAccessor world, BlockPos p, BlockState s, Random rand) {
		world.setBlock(p, s, 2);

		if (this.initialLoad)
			if (s.getBlock() == Blocks.CHEST) {
				BlockEntity te = world.getBlockEntity(p);

				if (te instanceof ChestBlockEntity) {
					((ChestBlockEntity) te).setLootTable(WorldLootTables.CHESTS_PYRAMID, rand.nextLong());
				}

			} else if (s.getBlock() == Blocks.SPAWNER) {
				BlockEntity te = world.getBlockEntity(p);

				if (te instanceof SpawnerBlockEntity) {
					EntityType<?> type = RuinUtil.getRandomRuneMob(rand);
					if (type == null) {
						type = EntityType.ZOMBIE;
					}
					((SpawnerBlockEntity) te).getSpawner().setEntityId(type);
				}
			}
	}

	private Block blockToPlace(Random random, BlockPos pos, int colHeight) {
		if (pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0) {
			return RuinUtil.randomRune(random);
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
						double d = spawnerList.get(idx).distSqr(pos.above(pos.getY()));
						if (d < 6D) {
							flag = true;
						}
					}

					if (!flag) {
						placedSpawners++;
						spawnerList.add(pos.above(pos.getY()));
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