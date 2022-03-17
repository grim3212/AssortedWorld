package com.grim3212.assorted.world.common.gen.structure.pyramid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.compress.utils.Lists;

import com.grim3212.assorted.world.common.gen.structure.WorldStructurePieceTypes;
import com.grim3212.assorted.world.common.lib.WorldLootTables;
import com.grim3212.assorted.world.common.util.RuinUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
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
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class PyramidPiece extends ScatteredFeaturePiece {

	private final int maxHeight;
	private final int type;

	private List<BlockPos> placedSpawners;
	private List<BlockPos> placedChests;

	public PyramidPiece(Random random, BlockPos pos, int maxHeight, int type) {
		super(WorldStructurePieceTypes.PYRAMID, pos.getX(), pos.getY() - 1 - maxHeight, pos.getZ(), maxHeight * 2, maxHeight * 2 + 1, maxHeight * 2, getRandomHorizontalDirection(random));
		this.maxHeight = maxHeight;
		this.type = type;
		this.placedSpawners = Lists.newArrayList();
		this.placedChests = Lists.newArrayList();
	}

	public PyramidPiece(StructurePieceSerializationContext context, CompoundTag tagCompound) {
		super(WorldStructurePieceTypes.PYRAMID, tagCompound);
		this.maxHeight = tagCompound.getInt("maxHeight");
		this.type = tagCompound.getInt("type");

		this.placedSpawners = Lists.newArrayList();
		ListTag spawners = tagCompound.getList("placedSpawners", 10);
		for (int i = 0; i < spawners.size(); i++) {
			this.placedSpawners.add(NbtUtils.readBlockPos((CompoundTag) spawners.get(i)));
		}

		this.placedChests = Lists.newArrayList();
		ListTag chests = tagCompound.getList("placedChests", 10);
		for (int i = 0; i < chests.size(); i++) {
			this.placedChests.add(NbtUtils.readBlockPos((CompoundTag) chests.get(i)));
		}
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
		super.addAdditionalSaveData(context, tagCompound);
		tagCompound.putInt("maxHeight", this.maxHeight);
		tagCompound.putInt("type", this.type);

		ListTag spawners = new ListTag();
		this.placedSpawners.forEach((p) -> {
			spawners.add(NbtUtils.writeBlockPos(p));
		});
		tagCompound.put("placedSpawners", spawners);

		ListTag chests = new ListTag();
		this.placedChests.forEach((p) -> {
			chests.add(NbtUtils.writeBlockPos(p));
		});
		tagCompound.put("placedChests", chests);
	}

	@Override
	public void postProcess(WorldGenLevel reader, StructureFeatureManager structureManager, ChunkGenerator generator, Random rand, BoundingBox bb, ChunkPos chunkPos, BlockPos pos) {
		if (this.updateAverageGroundHeight(reader, bb, 0)) {
			Map<BlockPos, Block> blockCache = new HashMap<>();
			BlockPos offSetPos = pos.below(maxHeight / 2);

			int halfWidth = halfWidth(maxHeight);
			int colHeight = 0;

			boolean genBlockEntities = this.placedSpawners.size() == 0 && this.placedChests.size() == 0;

			BlockPos newPos;
			for (int x = -halfWidth; x <= halfWidth; x++) {
				for (int z = -halfWidth; z <= halfWidth; z++) {
					colHeight = getColumnHeight(x, z);
					for (int y = -1; y <= colHeight; y++) {
						newPos = new BlockPos(x, y, z);

						blockCache.put(offSetPos.offset(newPos), blockToPlace(rand, newPos, colHeight, genBlockEntities));
					}
				}
			}

			this.placedSpawners.forEach((p) -> blockCache.put(offSetPos.offset(p), Blocks.SPAWNER));
			this.placedChests.forEach((p) -> blockCache.put(offSetPos.offset(p), Blocks.CHEST));

			// Outside the triple for is actually saving a lot of time
			// 38 size was generating in about ~16s
			// Now it is generating in about ~2s
			blockCache.forEach((p, b) -> setBlockState(reader, p, b.defaultBlockState(), rand));
		}
	}

	private void setBlockState(WorldGenLevel world, BlockPos p, BlockState s, Random rand) {
		world.setBlock(p, s, 2);

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

	private Block blockToPlace(Random random, BlockPos pos, int colHeight, boolean genBlockEntities) {
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
		if (!genBlockEntities) {
			return Blocks.AIR;
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
		if ((pos.getY() == 0 || (pos.getY() - 1) % 6 == 0 && pos.getY() > 4) && placedSpawners.size() < maxHeight / 3) {
			if (random.nextInt(98) < 2) {
				boolean flag = false;
				for (int idx = 0; idx < placedSpawners.size(); idx++) {
					int distance = (int) Math.round(Math.sqrt(placedSpawners.get(idx).distSqr(pos.above(pos.getY()))));
					// Each spawner should not be within 6 blocks of each other
					if (distance < 6) {
						flag = true;
					}
				}

				if (!flag) {
					placedSpawners.add(pos.above(pos.getY()));
					return true;
				}
			}
		}
		return false;
	}

	private boolean placeChest(Random random, BlockPos pos) {
		if ((pos.getY() == 0 || (pos.getY() - 1) % 6 == 0 && pos.getY() > 4) && placedChests.size() < placedSpawners.size() * 2 && random.nextInt(28) < 3) {
			boolean flag = false;
			for (int idx = 0; idx < placedChests.size(); idx++) {
				int distance = (int) Math.round(Math.sqrt(placedChests.get(idx).distSqr(pos.above(pos.getY()))));
				// Each spawner should not be within 3 blocks of each other
				if (distance < 3) {
					flag = true;
				}
			}

			if (!flag) {
				placedChests.add(pos.above(pos.getY()));
				return true;
			}
		}
		return false;
	}

	private int getColumnHeight(int x, int z) {
		return maxHeight - (Math.max(Math.abs(x), Math.abs(z)) / 2) * 2;
	}

	public static int halfWidth(int height) {
		return 2 * (height / 2) - 1;
	}
}