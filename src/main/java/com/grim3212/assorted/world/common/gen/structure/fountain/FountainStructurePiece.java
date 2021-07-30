package com.grim3212.assorted.world.common.gen.structure.fountain;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;
import com.grim3212.assorted.world.common.gen.structure.WorldStructurePieceTypes;
import com.grim3212.assorted.world.common.handler.WorldConfig;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.registries.ForgeRegistries;

public class FountainStructurePiece extends ScatteredFeaturePiece {

	private final int height;
	private final int type;

	private int placedSpawners;
	private int placedChests;
	private int spawnerSkipCount;
	private boolean runePlaced;
	private boolean initialLoad;
	private Map<BlockPos, ResourceLocation> fountain;

	public FountainStructurePiece(Random random, BlockPos pos, int height, int type) {
		super(WorldStructurePieceTypes.FOUNTAIN, pos.getX(), pos.getY(), pos.getZ(), height, height, height, getRandomHorizontalDirection(random));
		this.height = height;
		this.type = type;
		this.runePlaced = false;
		this.initialLoad = true;
		this.fountain = Maps.newHashMap();
	}

	public FountainStructurePiece(ServerLevel level, CompoundTag tagCompound) {
		super(WorldStructurePieceTypes.FOUNTAIN, tagCompound);
		this.height = tagCompound.getInt("height");
		this.type = tagCompound.getInt("type");
		this.initialLoad = false;
		this.runePlaced = false;

		this.fountain = Maps.newHashMap();
		ListTag blocks = tagCompound.getList("fountain", 10);
		for (int i = 0; i < blocks.size(); i++) {
			CompoundTag blockCompound = (CompoundTag) blocks.get(i);
			BlockPos pos = NbtUtils.readBlockPos(blockCompound.getCompound("pos"));
			ResourceLocation block = new ResourceLocation(blockCompound.getString("block"));
			this.fountain.put(pos, block);
		}
	}

	@Override
	protected void addAdditionalSaveData(ServerLevel level, CompoundTag tagCompound) {
		super.addAdditionalSaveData(level, tagCompound);
		tagCompound.putInt("height", this.height);
		tagCompound.putInt("type", this.type);

		ListTag blocks = new ListTag();
		this.fountain.forEach((p, s) -> {
			CompoundTag compound = new CompoundTag();
			compound.put("pos", NbtUtils.writeBlockPos(p));
			compound.putString("block", s.toString());
			blocks.add(compound);
		});
		tagCompound.put("fountain", blocks);
	}

	@Override
	public boolean postProcess(WorldGenLevel reader, StructureFeatureManager structureManager, ChunkGenerator generator, Random rand, BoundingBox bb, ChunkPos chunkPos, BlockPos pos) {

		int halfWidth = halfWidth(height);
		int colHeight = 0;

		if (initialLoad) {
			BlockPos newPos;
			for (int x = -halfWidth; x <= halfWidth; x++) {
				for (int z = -halfWidth; z <= halfWidth; z++) {
					colHeight = getColumnHeight(x, z);
					for (int y = -1; y <= colHeight; y++) {
						newPos = new BlockPos(x, y, z);

						this.fountain.put(pos.offset(newPos), blockToPlace(rand, newPos, colHeight).getRegistryName());
					}
				}
			}
		}

		// Outside the triple for is actually saving a lot of time
		// 38 size was generating in about ~16s
		// Now it is generating in about ~2s
		this.fountain.forEach((p, s) -> setBlockState(reader, p, s, rand));
		this.initialLoad = false;

		return true;
	}

	private void setBlockState(ServerLevelAccessor world, BlockPos p, ResourceLocation s, Random rand) {
		if (s == Blocks.CHEST.getRegistryName()) {
			setBlockState(world, p, Blocks.CHEST.defaultBlockState(), rand);
		} else if (s == Blocks.SPAWNER.getRegistryName()) {
			setBlockState(world, p, Blocks.SPAWNER.defaultBlockState(), rand);
		} else if (s == Blocks.COBBLESTONE.getRegistryName()) {
			setBlockState(world, p, Blocks.COBBLESTONE.defaultBlockState(), rand);
		} else if (s == Blocks.MOSSY_COBBLESTONE.getRegistryName()) {
			setBlockState(world, p, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), rand);
		} else if (s == Blocks.CRACKED_STONE_BRICKS.getRegistryName()) {
			setBlockState(world, p, Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), rand);
		} else if (s == Blocks.MOSSY_STONE_BRICKS.getRegistryName()) {
			setBlockState(world, p, Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), rand);
		} else if (s == Blocks.STONE_BRICKS.getRegistryName()) {
			setBlockState(world, p, Blocks.STONE_BRICKS.defaultBlockState(), rand);
		} else if (s == Blocks.WATER.getRegistryName()) {
			setBlockState(world, p, Blocks.WATER.defaultBlockState(), rand);
		} else if (s == Blocks.AIR.getRegistryName()) {
			setBlockState(world, p, Blocks.AIR.defaultBlockState(), rand);
		} else {
			// Hopefully ends up a bit more performant having this as a fallback
			setBlockState(world, p, ForgeRegistries.BLOCKS.getValue(s).defaultBlockState(), rand);
		}
	}

	private void setBlockState(ServerLevelAccessor world, BlockPos p, BlockState s, Random rand) {
		world.setBlock(p, s, 2);

		if (this.initialLoad) {
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
		}

		if (s.getBlock() == Blocks.WATER) {
			FluidState fluidstate = world.getFluidState(p);
			if (!fluidstate.isEmpty()) {
				world.getLiquidTicks().scheduleTick(p, fluidstate.getType(), 0);
			}
		}
	}

	private Block blockToPlace(Random random, BlockPos pos, int colHeight) {
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

	private Block randomStoneBrick(Random random) {
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

	private boolean placeStone(Random random, BlockPos pos, int colHeight) {
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

	private boolean placeSpawner(Random random, BlockPos pos) {
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

	private boolean placeChest(Random random, BlockPos pos) {
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
