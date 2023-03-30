package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.WorldCommonMod;
import com.grim3212.assorted.world.api.WorldLootTables;
import com.grim3212.assorted.world.common.util.RuinUtil;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class RuinFeature extends Feature<NoneFeatureConfiguration> {

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

    public RuinFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource rand = context.random();
        BlockPos pos = context.origin();
        WorldGenLevel level = context.level();

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
        if (isAreaClear(level, pos)) {
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
                        fillWater(level, newPos);
                        if (skip != 0) {
                            if (!skipper) {
                                generateColumn(level, rand, newPos);
                            }
                            if (skipCounter == skip) {
                                skipCounter = 0;
                                skipper = !skipper;
                            } else {
                                skipCounter++;
                            }
                        } else {
                            generateColumn(level, rand, newPos);
                        }
                        continue;
                    }
                    if (RuinUtil.distanceBetween(pos.getX(), pos.getZ(), newPos.getX(), newPos.getZ()) < radius) {
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
                if (Math.abs(pos.getY() - newPos.getY()) > 3) {
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
                level.setBlock(newPos, Blocks.SAND.defaultBlockState(), 2);
                continue;
            }
            if (state.canOcclude()) {
                flag = true;
            }
        }
    }

    private void generateColumn(WorldGenLevel level, RandomSource random, BlockPos pos) {
        int y = pos.getY();
        pos = level.getHeightmapPos(Types.WORLD_SURFACE_WG, pos);
        int topY = pos.getY();

        for (; topY < y; topY++) {
            if (!runePlaced && (double) random.nextFloat() <= WorldCommonMod.COMMON_CONFIG.runeChance.get()) {
                runePlaced = true;
                level.setBlock(pos, RuinUtil.randomRune(random).defaultBlockState(), 2);
                continue;
            }
            int blockType = random.nextInt(30);
            if (blockType <= 13) {
                level.setBlock(pos, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
                continue;
            }
            if (blockType <= 25) {
                level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 2);
            }
        }

        if (topY > y + 3) {
            pos = new BlockPos(pos.getX(), y, pos.getZ());
        }
        for (int off = 0; off < 5; off++) {
            if (!level.isEmptyBlock(pos.above(off)) && !(level.getBlockState(pos.above(off)).getBlock() instanceof LeavesBlock)) {
                continue;
            }
            int blockType = random.nextInt(30 + off * 10);
            if (blockType < 13) {
                level.setBlock(pos.above(off), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
                continue;
            }
            if (blockType < 25) {
                level.setBlock(pos.above(off), Blocks.COBBLESTONE.defaultBlockState(), 2);
                continue;
            }
            BlockState stateUp = level.getBlockState(pos.above(off));
            if (off == 0 || type == 7 || !stateUp.isFaceSturdy(level, pos.above(off), Direction.UP) || numTorches >= 8) {
                continue;
            }
            if (torchSkip < 8) {
                torchSkip++;
            } else {
                level.setBlock(pos.above(off), Blocks.TORCH.defaultBlockState(), 2);
                numTorches++;
                torchSkip = 0;
            }
        }

    }

    private void clearArea(WorldGenLevel level, RandomSource random, BlockPos pos) {
        int y = pos.getY();
        pos = level.getHeightmapPos(Types.WORLD_SURFACE_WG, pos);
        int topY = pos.getY();
        if (type == 2 || type == 6) {
            for (; topY < y; topY++) {
                int blockType = random.nextInt(3);
                if (blockType == 1) {
                    level.setBlock(pos, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
                    continue;
                }
                if (blockType == 2) {
                    level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 2);
                }
            }

        }
        if (topY > y + 3) {
            pos = new BlockPos(pos.getX(), y, pos.getZ());
        }
        for (int off = 0; off < 5; off++) {
            if (!level.isEmptyBlock(pos.above(off))) {
                level.setBlock(pos.above(off), Blocks.AIR.defaultBlockState(), 2);
            }
        }

        if (type == 3 || type == 7) {
            int blockType = random.nextInt(3);
            if (type == 7) {
                blockType = random.nextInt(1);
            }
            if (blockType == 0) {
                level.setBlock(pos.above(5), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
            } else if (blockType == 1) {
                level.setBlock(pos.above(5), Blocks.COBBLESTONE.defaultBlockState(), 2);
            }
            int genType = random.nextInt(10);
            if (type == 7) {
                genType = random.nextInt(20);
            }
            if (genType == 1) {
                generateColumn(level, random, pos);
            } else if (genType > 5 && random.nextInt(50) == 1 && type == 7) {
                genMobSpawner(level, random, pos);
                return;
            }
        }
        if (type == 4 || type == 8) {
            if (random.nextInt(5) == 1) {
                generateColumn(level, random, pos);
            }
        }
        if (random.nextInt(250) == 1 && type < 4) {
            genChest(level, random, pos);
        } else if (type == 7 && placedSpawn && random.nextInt(2) == 1) {
            genChest(level, random, pos);
        }
    }

    private void genChest(WorldGenLevel level, RandomSource random, BlockPos pos) {
        if (!placedChest) {
            level.setBlock(pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.from2DDataValue(random.nextInt(4))), 2);
            ChestBlockEntity tileentitychest = (ChestBlockEntity) level.getBlockEntity(pos);
            tileentitychest.setLootTable(WorldLootTables.CHESTS_RUIN, random.nextLong());

            placedChest = true;
        }
    }

    private void genMobSpawner(WorldGenLevel level, RandomSource random, BlockPos pos) {
        if (!placedSpawn) {
            level.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
            SpawnerBlockEntity tileentitymobspawner = (SpawnerBlockEntity) level.getBlockEntity(pos);
            EntityType<?> type = RuinUtil.getRandomRuneMob(random);
            if (type == null) {
                type = EntityType.ZOMBIE;
            }

            tileentitymobspawner.setEntityId(type, random);
            placedSpawn = true;
        }
    }

}
