package com.grim3212.assorted.world.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grim3212.assorted.world.common.util.RuinUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Helpers and constants shared by AssortedWorld's gametest classes, which import them statically.
 */
final class WorldTestSupport {

    private WorldTestSupport() {
    }

    /** Middle of the 9x9x9 box, one block above its floor - room on every side for a drop. */
    static final BlockPos CENTRE = new BlockPos(4, 1, 4);

    static MobEffectInstance useRuneAs(GameTestHelper helper, int experienceLevel) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.experienceLevel = experienceLevel;
        helper.useBlock(CENTRE, player);

        MobEffectInstance effect = player.getEffect(MobEffects.SPEED);
        helper.assertTrue(effect != null, "using a rad rune gave no speed effect at experience level " + experienceLevel);
        return effect;
    }

    static List<Integer> draw(GameTestHelper helper, BoundingBox box) {
        RandomSource random = RuinUtil.pieceRandom(helper.getLevel(), box);
        return List.of(random.nextInt(30), random.nextInt(30), random.nextInt(30), random.nextInt(30),
                random.nextInt(30), random.nextInt(30), random.nextInt(30), random.nextInt(30));
    }

    static BlockPos runePos(int index) {
        return new BlockPos(index % 8, 1, index / 8);
    }

    static ItemStack craft(GameTestHelper helper, CraftingInput input, String what) {
        // recipeAccess() is the full RecipeManager server side, so a headless test sees every
        // loaded recipe without needing a crafting menu.
        Optional<RecipeHolder<CraftingRecipe>> found = helper.getLevel().recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel());
        helper.assertTrue(found.isPresent(), "no crafting recipe matches " + what);
        return found.get().value().assemble(input);
    }

    /** The mod's own assets are on the classpath even on a headless server, jar or source root. */
    static boolean resourceExists(String path) {
        try (InputStream in = WorldGameTests.class.getResourceAsStream(path)) {
            return in != null;
        } catch (IOException e) {
            return false;
        }
    }

    static JsonObject readJson(GameTestHelper helper, String path) {
        try (InputStream in = WorldGameTests.class.getResourceAsStream(path)) {
            helper.assertTrue(in != null, path + " is not on the classpath");
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            helper.assertTrue(false, "could not read " + path + ": " + e);
            return new JsonObject();
        }
    }

    static boolean hasFeature(Biome biome, Identifier id) {
        for (HolderSet<PlacedFeature> step : biome.getGenerationSettings().features()) {
            for (Holder<PlacedFeature> feature : step) {
                if (feature.unwrapKey().filter(key -> key.identifier().equals(id)).isPresent()) {
                    return true;
                }
            }
        }
        return false;
    }
}
