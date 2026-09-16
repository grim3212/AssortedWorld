package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.world.api.WorldTags;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * The woods a stray sapling or a tree stump can be, and the pick of one for a biome. A biome gets
 * the woods whose {@code woods/<name>} tag holds it, so a taiga rolls spruce and a savanna acacia;
 * a biome no tag names falls back to the woods marked {@link #fallback}.
 * <p>
 * Same idea as {@link FloatingIslandTypes}, and a datapack can move a wood the same way.
 */
public enum BiomeWoods {

    OAK("oak", Blocks.OAK_SAPLING, Blocks.OAK_LOG, 6, true),
    BIRCH("birch", Blocks.BIRCH_SAPLING, Blocks.BIRCH_LOG, 4, true),
    SPRUCE("spruce", Blocks.SPRUCE_SAPLING, Blocks.SPRUCE_LOG, 4, false),
    JUNGLE("jungle", Blocks.JUNGLE_SAPLING, Blocks.JUNGLE_LOG, 3, false),
    ACACIA("acacia", Blocks.ACACIA_SAPLING, Blocks.ACACIA_LOG, 3, false),
    DARK_OAK("dark_oak", Blocks.DARK_OAK_SAPLING, Blocks.DARK_OAK_LOG, 3, false),
    CHERRY("cherry", Blocks.CHERRY_SAPLING, Blocks.CHERRY_LOG, 2, false),
    PALE_OAK("pale_oak", Blocks.PALE_OAK_SAPLING, Blocks.PALE_OAK_LOG, 2, false),
    MANGROVE("mangrove", Blocks.MANGROVE_PROPAGULE, Blocks.MANGROVE_LOG, 2, false);

    private static final List<BiomeWoods> VALUES = List.of(values());

    private final String name;
    private final BlockState sapling;
    private final BlockState stump;
    private final int weight;
    private final boolean fallback;
    private final TagKey<Biome> biomes;

    BiomeWoods(String name, Block sapling, Block stump, int weight, boolean fallback) {
        this.name = name;
        this.sapling = sapling.defaultBlockState();
        this.stump = stump.defaultBlockState();
        this.weight = weight;
        this.fallback = fallback;
        this.biomes = WorldTags.Biomes.woods(name);
    }

    public String woodName() {
        return this.name;
    }

    /** The biomes this wood grows in. */
    public TagKey<Biome> biomes() {
        return this.biomes;
    }

    /** Whether this wood can stand in a biome no wood's tag names. */
    public boolean fallback() {
        return this.fallback;
    }

    public BlockState sapling() {
        return this.sapling;
    }

    /** A stump is one log left standing where a tree was. */
    public BlockState stump() {
        return this.stump;
    }

    public static List<BiomeWoods> all() {
        return VALUES;
    }

    /** The woods that suit {@code biome}, weighted against each other. */
    public static BiomeWoods pick(Holder<Biome> biome, RandomSource random) {
        List<BiomeWoods> fits = new ArrayList<>();
        for (BiomeWoods wood : VALUES) {
            if (biome.is(wood.biomes())) {
                fits.add(wood);
            }
        }

        if (fits.isEmpty()) {
            for (BiomeWoods wood : VALUES) {
                if (wood.fallback) {
                    fits.add(wood);
                }
            }
        }

        int total = fits.stream().mapToInt(wood -> wood.weight).sum();
        int roll = random.nextInt(total);
        for (BiomeWoods wood : fits) {
            roll -= wood.weight;
            if (roll < 0) {
                return wood;
            }
        }

        return OAK;
    }
}
