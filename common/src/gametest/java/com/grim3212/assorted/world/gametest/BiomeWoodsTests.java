package com.grim3212.assorted.world.gametest;

import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.common.gen.feature.BiomeWoods;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.world.gametest.WorldTestSupport.*;

/**
 * The wood a stray sapling or a tree stump is made of, per biome.
 */
final class BiomeWoodsTests {

    private BiomeWoodsTests() {
    }

    /** Biomes whose wood is not up for debate, so a tag that drifts fails here rather than in a world. */
    private static final Map<ResourceKey<Biome>, BiomeWoods> EXPECTED = Map.of(
            Biomes.TAIGA, BiomeWoods.SPRUCE,
            Biomes.JUNGLE, BiomeWoods.JUNGLE,
            Biomes.SAVANNA, BiomeWoods.ACACIA,
            Biomes.DARK_FOREST, BiomeWoods.DARK_OAK,
            Biomes.CHERRY_GROVE, BiomeWoods.CHERRY,
            Biomes.PALE_GARDEN, BiomeWoods.PALE_OAK,
            Biomes.MANGROVE_SWAMP, BiomeWoods.MANGROVE,
            Biomes.BIRCH_FOREST, BiomeWoods.BIRCH);

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("every_wood_suits_some_biome", BiomeWoodsTests::everyWoodSuitsSomeBiome);
        out.accept("wood_matches_the_biome_it_stands_in", BiomeWoodsTests::woodMatchesTheBiomeItStandsIn);
        out.accept("stray_saplings_and_stumps_reach_the_woods", BiomeWoodsTests::straySaplingsAndStumpsReachTheWoods);
    }

    /**
     * Every wood's biome tag holds something. An empty one is silent: the wood simply never comes up
     * and the fallback woods quietly cover for it.
     */
    private static void everyWoodSuitsSomeBiome(GameTestHelper helper) {
        List<String> problems = new ArrayList<>();

        for (BiomeWoods wood : BiomeWoods.all()) {
            boolean used = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).get(wood.biomes())
                    .map(biomes -> biomes.size() > 0).orElse(false);
            if (!used) {
                problems.add("wood " + wood.woodName() + " has no biomes, so it can never be picked");
            }
        }

        helper.assertTrue(problems.isEmpty(), "biome woods: " + String.join("; ", problems));
        helper.succeed();
    }

    /**
     * A biome grows the wood it should, and never one that does not belong to it. Several woods can
     * fit one biome - a forest grows oak and birch - so this pins the tag rather than the roll.
     */
    private static void woodMatchesTheBiomeItStandsIn(GameTestHelper helper) {
        List<String> problems = new ArrayList<>();

        EXPECTED.forEach((key, expected) -> {
            Holder<Biome> biome = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(key);
            if (!biome.is(expected.biomes())) {
                problems.add(key.identifier() + " is not tagged for " + expected.woodName());
            }

            for (int roll = 0; roll < 32; roll++) {
                BiomeWoods picked = BiomeWoods.pick(biome, helper.getLevel().getRandom());
                if (!biome.is(picked.biomes())) {
                    problems.add(key.identifier() + " grew " + picked.woodName() + ", which does not belong there");
                    break;
                }
            }
        });

        // A biome no wood names at all still has to give something back.
        Holder<Biome> desert = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.DESERT);
        helper.assertTrue(BiomeWoods.pick(desert, helper.getLevel().getRandom()).fallback(), "a biome with no wood of its own did not fall back");

        helper.assertTrue(problems.isEmpty(), "biome woods: " + String.join("; ", problems));
        helper.succeed();
    }

    /** The woods a player would actually go looking in. */
    private static final List<ResourceKey<Biome>> WOODLANDS = List.of(Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.TAIGA, Biomes.DARK_FOREST, Biomes.JUNGLE, Biomes.OLD_GROWTH_SPRUCE_TAIGA);

    /**
     * Stray saplings and stumps reach the ordinary woods, and their placement does not measure the
     * ground from the top of the canopy.
     * <p>
     * Both were near invisible in a world: the c: tags they hung off cover only the dense and old
     * growth biomes, and MOTION_BLOCKING counts leaves, so under a canopy every attempt landed above
     * the treetops and failed the ground check.
     */
    private static void straySaplingsAndStumpsReachTheWoods(GameTestHelper helper) {
        List<String> problems = new ArrayList<>();

        for (String name : List.of("patch_saplings", "tree_stumps")) {
            Identifier id = Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);

            for (ResourceKey<Biome> key : WOODLANDS) {
                Biome biome = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).getValueOrThrow(key);
                if (!hasFeature(biome, id)) {
                    problems.add(name + " never generates in " + key.identifier());
                }
            }

            String heightmap = readJson(helper, "/data/" + Constants.MOD_ID + "/worldgen/placed_feature/" + name + ".json").toString();
            if (!heightmap.contains("MOTION_BLOCKING_NO_LEAVES")) {
                problems.add(name + " measures the ground from the canopy rather than the forest floor");
            }
        }

        helper.assertTrue(problems.isEmpty(), "woodland features: " + String.join("; ", problems));
        helper.succeed();
    }
}
