package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.WorldCommonMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.List;

/**
 * An island hanging in the air. Its shape comes from {@link FloatingIslandShape}, and what it is
 * made of and what grows on it from a {@link FloatingIslandType} that fits the biome underneath, so
 * a snowy biome gets spruce and snow while a jungle gets jungle trees.
 * <p>
 * GrimPack made these by lifting a bowl of terrain into the sky and leaving the crater behind. This
 * builds a new island instead and leaves the ground alone: a feature may only write to the chunk it
 * is decorating and its neighbours, and scooping the ground meant rewriting terrain well outside
 * that.
 */
public class FloatingIslandFeature extends Feature<NoneFeatureConfiguration> {

    /** The air an island keeps under it when the build limit will not fit the configured minimum. */
    private static final int SQUEEZED_GAP = 4;

    /**
     * How far from the centre a tree may be planted. A feature may write at least 16 blocks from its
     * origin, and the widest vanilla trees - fancy oak branches, mangrove roots - spread about 9.
     */
    private static final int TREE_REACH = 7;

    public FloatingIslandFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        FloatingIslandShape shape = FloatingIslandShape.roll(random, rollSize(random));
        Holder<Biome> biome = level.getBiome(origin);
        FloatingIslandType type = FloatingIslandTypes.pick(random, biome);

        // Measured from the highest ground anywhere under the island, so an island over a mountain
        // clears the peak instead of being turned away by it.
        int ground = highestGround(level, origin, shape);
        int gap = rollGap(random);
        int treeHeadroom = type.treeHeadroom();

        // Near the build limit, sink towards the ground first and then leave the trees off: an
        // island over the tallest peaks still generates, just lower and bare.
        int spare = spare(level, ground, gap, shape, treeHeadroom);
        if (spare < 0) {
            gap = Math.max(Math.min(gap, SQUEEZED_GAP), gap + spare);
            spare = spare(level, ground, gap, shape, treeHeadroom);
        }
        if (spare < 0 && treeHeadroom > 0) {
            treeHeadroom = 0;
            spare = spare(level, ground, gap, shape, treeHeadroom);
        }
        if (spare < 0) {
            return false;
        }

        // The base level is where a column with no rise has its surface. The deepest column's lowest
        // block then sits exactly gap blocks above the ground.
        BlockPos base = new BlockPos(origin.getX(), ground + gap + shape.deepest() - 1, origin.getZ());

        for (FloatingIslandShape.Column column : shape.columns()) {
            int fillerDepth = 1 + random.nextInt(3);
            BlockPos surface = surface(base, column);

            for (int down = 0; down < column.height(); down++) {
                BlockPos at = surface.below(down);
                level.setBlock(at, layer(random, type, down, fillerDepth, at.getY()), Block.UPDATE_CLIENTS);
            }
        }

        if (WorldCommonMod.COMMON_CONFIG.floatingIslandOres.get()) {
            seedOres(level, random, base, shape);
        }

        // Trees before plants, so a trunk is never refused a spot a flower took; plants and snow
        // after the island is whole, so canSurvive sees the finished surface.
        if (treeHeadroom > 0) {
            growTrees(level, context.chunkGenerator(), random, type, base, shape);
        }

        for (FloatingIslandShape.Column column : shape.columns()) {
            BlockPos above = surface(base, column).above();
            type.plant(random).ifPresent(plant -> placePlant(level, above, plant));

            if (type.snowCover()) {
                placePlant(level, above, Blocks.SNOW.defaultBlockState());
            }
        }

        return true;
    }

    private static BlockPos surface(BlockPos base, FloatingIslandShape.Column column) {
        return base.offset(column.x(), column.rise(), column.z());
    }

    /** Cover on top, a little filler under it, then the kind's core the rest of the way down. */
    private static BlockState layer(RandomSource random, FloatingIslandType type, int down, int fillerDepth, int y) {
        if (down == 0) {
            return type.cover(random);
        }
        return down <= fillerDepth ? type.filler() : type.core(random, y);
    }

    /**
     * Grows the kind's trees on soil near the middle. The configured features are vanilla's own, so
     * each checks its own room and simply does nothing where a tree will not fit.
     */
    private static void growTrees(WorldGenLevel level, ChunkGenerator generator, RandomSource random, FloatingIslandType type, BlockPos base, FloatingIslandShape shape) {
        List<FloatingIslandShape.Column> spots = shape.columns().stream()
                .filter(column -> Math.max(Math.abs(column.x()), Math.abs(column.z())) <= TREE_REACH)
                .toList();
        if (spots.isEmpty()) {
            return;
        }

        Registry<ConfiguredFeature<?, ?>> features = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE);
        int count = type.treeCount(random, shape.columns().size());

        for (int i = 0; i < count; i++) {
            BlockPos ground = surface(base, spots.get(random.nextInt(spots.size())));
            // Vanilla trees leave checking the ground to their placement rules, which this skips.
            // Not #dirt: since 26.2 that is only dirt, coarse dirt and rooted dirt.
            if (!level.getBlockState(ground).is(BlockTags.SUBSTRATE_OVERWORLD) || !level.isEmptyBlock(ground.above())) {
                continue;
            }

            features.get(type.tree(random)).ifPresent(tree -> tree.value().place(level, generator, random, ground.above()));
        }
    }

    /**
     * Vanilla's own ore veins and stone pockets, grown inside the island's core. Every one of them
     * only replaces {@code #stone_ore_replaceables}, so a stone core gets veins and a sandstone,
     * terracotta or ice core is left as it is without this needing to know which is which.
     */
    private static void seedOres(WorldGenLevel level, RandomSource random, BlockPos base, FloatingIslandShape shape) {
        // Tall enough for a vein to sit inside the core rather than break out of the surface.
        List<FloatingIslandShape.Column> spots = shape.columns().stream()
                .filter(column -> Math.max(Math.abs(column.x()), Math.abs(column.z())) <= ORE_REACH && column.height() >= MIN_ORE_HEIGHT)
                .toList();
        if (spots.isEmpty()) {
            return;
        }

        Registry<ConfiguredFeature<?, ?>> features = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE);
        int veins = Math.max(1, shape.columns().size() / COLUMNS_PER_VEIN);

        for (int i = 0; i < veins; i++) {
            FloatingIslandShape.Column spot = spots.get(random.nextInt(spots.size()));
            // Somewhere below the filler, which is at most three blocks under the surface.
            int down = 4 + random.nextInt(spot.height() - 4);
            BlockPos at = surface(base, spot).below(down);
            features.get(ORES.getRandomOrThrow(random))
                    .filter(ore -> ore.value().config() instanceof OreConfiguration)
                    .ifPresent(ore -> growVein(level, random, (OreConfiguration) ore.value().config(), at));
        }
    }

    /**
     * A random walk as many steps as the vein's size, placing ore wherever vanilla's rules allow.
     * Not {@link Feature#ORE} itself: it gives up on any vein above the ground's heightmap, which
     * is every vein in an island.
     */
    private static void growVein(WorldGenLevel level, RandomSource random, OreConfiguration config, BlockPos start) {
        BlockPos.MutableBlockPos at = start.mutable();
        for (int step = 0; step < config.size; step++) {
            BlockState current = level.getBlockState(at);
            for (OreConfiguration.TargetBlockState target : config.targetStates) {
                if (OreFeature.canPlaceOre(current, level::getBlockState, random, config, target, at)) {
                    level.setBlock(at, target.state, Block.UPDATE_CLIENTS);
                    break;
                }
            }

            Direction direction = Direction.getRandom(random);
            at.move(direction);
            if (at.distManhattan(start) > VEIN_SPREAD || Math.abs(at.get(direction.getAxis()) - start.get(direction.getAxis())) > VEIN_SPREAD / 2) {
                at.move(direction.getOpposite());
            }
        }
    }

    /**
     * How far from the centre a vein may start, and how far one may wander from its start. Together
     * they stay inside the 15 blocks an island may reach.
     */
    private static final int ORE_REACH = 8;
    private static final int VEIN_SPREAD = 6;
    private static final int MIN_ORE_HEIGHT = 6;
    private static final int COLUMNS_PER_VEIN = 12;

    /** Weighted about as the ground has them near the surface: coal and iron common, diamond rare. */
    private static final WeightedList<ResourceKey<ConfiguredFeature<?, ?>>> ORES = WeightedList.<ResourceKey<ConfiguredFeature<?, ?>>>builder()
            .add(ore("ore_coal"), 10)
            .add(ore("ore_iron_small"), 8)
            .add(ore("ore_copper_small"), 6)
            .add(ore("ore_redstone"), 3)
            .add(ore("ore_gold"), 3)
            .add(ore("ore_lapis"), 2)
            .add(ore("ore_emerald"), 1)
            .add(ore("ore_diamond_small"), 1)
            .add(ore("ore_granite"), 2)
            .add(ore("ore_diorite"), 2)
            .add(ore("ore_andesite"), 2)
            .add(ore("ore_gravel"), 2)
            .add(ore("ore_dirt"), 2)
            .build();

    private static ResourceKey<ConfiguredFeature<?, ?>> ore(String id) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.withDefaultNamespace(id));
    }

    /** Every ore feature islands can seed, for checking they all exist. */
    public static List<ResourceKey<ConfiguredFeature<?, ?>>> ores() {
        return ORES.unwrap().stream().map(Weighted::value).toList();
    }

    /** A plant or snow layer in an empty spot it can survive in. Tall plants need the block above as well. */
    private static void placePlant(WorldGenLevel level, BlockPos pos, BlockState plant) {
        if (!level.isEmptyBlock(pos) || !plant.canSurvive(level, pos)) {
            return;
        }

        if (plant.getBlock() instanceof DoublePlantBlock) {
            if (level.isEmptyBlock(pos.above())) {
                DoublePlantBlock.placeAt(level, plant, pos, Block.UPDATE_CLIENTS);
            }
            return;
        }

        level.setBlock(pos, plant, Block.UPDATE_CLIENTS);
    }

    /** The first free height above the tallest column under any part of the island. */
    private static int highestGround(WorldGenLevel level, BlockPos origin, FloatingIslandShape shape) {
        int highest = level.getMinY();
        for (FloatingIslandShape.Column column : shape.columns()) {
            highest = Math.max(highest, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, origin.getX() + column.x(), origin.getZ() + column.z()));
        }
        return highest;
    }

    /**
     * Blocks left under the build limit once the island, a plant on top and its tallest tree are in.
     * Negative means it does not fit.
     */
    private static int spare(WorldGenLevel level, int ground, int gap, FloatingIslandShape shape, int treeHeadroom) {
        int base = ground + gap + shape.deepest() - 1;
        int top = base + shape.highest() + 1 + treeHeadroom;
        return level.getMaxY() - top;
    }

    /**
     * An even roll between the configured bounds. They are clamped here as well as in the config,
     * because the upper bound is what keeps the island inside the chunks it may write to.
     */
    private static int rollSize(RandomSource random) {
        int min = Math.clamp(WorldCommonMod.COMMON_CONFIG.floatingIslandMinSize.get(), FloatingIslandShape.MIN_SIZE, FloatingIslandShape.MAX_REACH);
        int max = Math.clamp(WorldCommonMod.COMMON_CONFIG.floatingIslandMaxSize.get(), FloatingIslandShape.MIN_SIZE, FloatingIslandShape.MAX_REACH);
        return Math.min(min, max) + random.nextInt(Math.abs(max - min) + 1);
    }

    /** The air between the ground and the island's lowest point, evenly between the configured bounds. */
    private static int rollGap(RandomSource random) {
        int min = WorldCommonMod.COMMON_CONFIG.floatingIslandMinHeight.get();
        int max = WorldCommonMod.COMMON_CONFIG.floatingIslandMaxHeight.get();
        return Math.min(min, max) + random.nextInt(Math.abs(max - min) + 1);
    }
}
