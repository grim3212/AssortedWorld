package com.grim3212.assorted.world.common.gen.structure.fountain;

import java.util.HashMap;
import java.util.Map;

import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.grim3212.assorted.world.common.handler.WorldConfig;
import com.grim3212.assorted.world.common.lib.WorldLootTables;
import com.grim3212.assorted.world.common.util.RuinUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
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
import net.minecraft.world.level.material.FluidState;

public class FountainPiece extends ScatteredFeaturePiece {

	private final int height;
	private final int type;

	private int placedSpawners;
	private int placedChests;
	private int spawnerSkipCount;
	private boolean runePlaced;

	public FountainPiece(RandomSource random, BlockPos pos, int height, int type) {
		super(WorldStructures.FOUNTAIN_STRUCTURE_PIECE.get(), pos.getX(), pos.getY(), pos.getZ(), height, height, height, getRandomHorizontalDirection(random));
		this.height = height;
		this.type = type;
		this.runePlaced = false;
	}

	public FountainPiece(StructurePieceSerializationContext context, CompoundTag tagCompound) {
		super(WorldStructures.FOUNTAIN_STRUCTURE_PIECE.get(), tagCompound);
		this.height = tagCompound.getInt("height");
		this.type = tagCompound.getInt("type");
		this.runePlaced = false;
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
		super.addAdditionalSaveData(context, tagCompound);
		tagCompound.putInt("height", this.height);
		tagCompound.putInt("type", this.type);
	}

	@Override
	public void postProcess(WorldGenLevel reader, StructureManager structureManager, ChunkGenerator generator, RandomSource rand, BoundingBox bb, ChunkPos chunkPos, BlockPos pos) {
		if (this.updateAverageGroundHeight(reader, bb, 0)) {
			int halfWidth = halfWidth(height);
			int colHeight = 0;

			Map<BlockPos, Block> blockCache = new HashMap<>();

			BlockPos newPos;
			for (int x = -halfWidth; x <= halfWidth; x++) {
				for (int z = -halfWidth; z <= halfWidth; z++) {
					colHeight = getColumnHeight(x, z);
					for (int y = -1; y <= colHeight; y++) {
						newPos = new BlockPos(x, y, z);

						blockCache.put(pos.offset(newPos), blockToPlace(rand, newPos, colHeight));
					}
				}
			}

			blockCache.forEach((p, b) -> setBlockState(reader, p, b.defaultBlockState(), rand));
		}
	}

	private void setBlockState(WorldGenLevel world, BlockPos p, BlockState s, RandomSource rand) {
		world.setBlock(p, s, 2);

		if (s.getBlock() == Blocks.CHEST) {
			BlockEntity te = world.getBlockEntity(p);

			if (te instanceof ChestBlockEntity) {
				((ChestBlockEntity) te).setLootTable(WorldLootTables.CHESTS_FOUNTAIN, rand.nextLong());
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

		if (s.getBlock() == Blocks.WATER) {
			FluidState fluidstate = world.getFluidState(p);
			if (!fluidstate.isEmpty()) {
				world.scheduleTick(p, fluidstate.getType(), 0);
			}
		}
	}

	private Block blockToPlace(RandomSource random, BlockPos pos, int colHeight) {
		if (pos.getX() != 0 && pos.getY() == -1 && pos.getZ() != 0 && (double) random.nextFloat() <= WorldConfig.COMMON.runeChance.get() && !runePlaced) {
			runePlaced = true;
			return RuinUtil.randomRune(random);
		}
		if (placeWater(pos, colHeight)) {
			return Blocks.WATER;
		}
		if (placeStone(random, pos, colHeight)) {
			if (type == 0) {
				if (random.nextBoolean()) {
					return Blocks.COBBLESTONE;
				} else {
					return Blocks.MOSSY_COBBLESTONE;
				}
			} else {
				return randomStoneBrick(random);
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

	private Block randomStoneBrick(RandomSource random) {
		int i = random.nextInt(3);
		if (i == 1) {
			return Blocks.CRACKED_STONE_BRICKS;
		} else if (i == 2) {
			return Blocks.MOSSY_STONE_BRICKS;
		} else {
			return Blocks.STONE_BRICKS;
		}
	}

	private boolean placeWater(BlockPos pos, int colHeight) {
		if (pos.getX() == 0 && pos.getZ() == 0) {
			return colHeight == pos.getY();
		}
		if (pos.getY() == -1) {
			return false;
		} else {
			boolean flag = Math.max(Math.abs(pos.getX()), Math.abs(pos.getZ())) % 2 == 0;
			boolean flag1 = colHeight - pos.getY() < 2;
			return flag && flag1;
		}
	}

	private boolean placeStone(RandomSource random, BlockPos pos, int colHeight) {
		if (pos.getX() == 0 && pos.getZ() == 0) {
			return false;
		}
		if (pos.getY() == -1) {
			return true;
		}
		boolean flag = Math.max(Math.abs(pos.getX()), Math.abs(pos.getZ())) % 2 == 0;
		if (flag && colHeight - pos.getY() == 2) {
			return true;
		}
		if (!flag && colHeight - pos.getY() < 6) {
			if (Math.abs(pos.getX()) != Math.abs(pos.getY()) && colHeight - pos.getY() == 1) {
				return random.nextInt(5) < 3;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private boolean placeSpawner(RandomSource random, BlockPos pos) {
		if (pos.getY() == 0 && placedSpawners < 2) {
			if (spawnerSkipCount == 0) {
				if (random.nextInt(64) < 2) {
					placedSpawners++;
					spawnerSkipCount++;
					return true;
				}
			} else {
				spawnerSkipCount++;
				if (spawnerSkipCount >= 48) {
					spawnerSkipCount = 0;
				}
			}
		}
		return false;
	}

	private boolean placeChest(RandomSource random, BlockPos pos) {
		if (pos.getY() == 0 && placedChests < placedSpawners * 2 && random.nextInt(28) < 3) {
			placedChests++;
			return true;
		} else {
			return false;
		}
	}

	private int getColumnHeight(int x, int z) {
		return height - (Math.max(Math.abs(x), Math.abs(z)) / 2) * 4;
	}

	public static int halfWidth(int height) {
		return 2 * (height / 4) - 1;
	}
}
