package com.grim3212.assorted.world.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.MinecraftServer;
import com.grim3212.assorted.lib.platform.Services;
import java.io.BufferedReader;
import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.common.handlers.WorldCreativeItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.world.gametest.WorldTestSupport.*;

/**
 * What the mod ships: a model and a name for everything, and recipes that load.
 */
final class AssetTests {

    private AssetTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("every_block_and_item_has_a_model_and_a_name", AssetTests::everyBlockAndItemHasAModelAndAName);
        out.accept("every_recipe_loads_or_is_conditioned_off", AssetTests::everyRecipeLoadsOrIsConditionedOff);
    }

    /**
     * Every block and item this mod registers needs a model and a name, and so does the creative
     * tab. Missing models and missing lang keys are the most repeated failure of this port and
     * neither says anything at runtime - a missing model is a purple cube and a missing lang key is
     * the raw translation string.
     * <p>
     * The tab's contents are this mod's own blocks, added in {@code WorldCreativeItems}, so walking
     * the registries covers the same ground and also catches anything registered but never added.
     */
    private static void everyBlockAndItemHasAModelAndAName(GameTestHelper helper) {
        JsonObject lang = readJson(helper, "/assets/" + Constants.MOD_ID + "/lang/en_us.json");
        List<String> problems = new ArrayList<>();

        if (!BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(WorldCreativeItems.CREATIVE_TAB_KEY)) {
            problems.add("the creative tab " + WorldCreativeItems.CREATIVE_TAB_KEY.identifier() + " is not registered");
        }
        String tabKey = "itemGroup." + Constants.MOD_ID;
        if (!lang.has(tabKey)) {
            problems.add("the creative tab has no lang key " + tabKey);
        }

        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            if (!resourceExists("/assets/" + Constants.MOD_ID + "/blockstates/" + id.getPath() + ".json")) {
                problems.add("block " + id + " has no blockstate json");
            }
            if (!lang.has(block.getDescriptionId())) {
                problems.add("block " + id + " has no lang key " + block.getDescriptionId());
            }
        }

        for (Item item : BuiltInRegistries.ITEM) {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            // Item models moved out of models/item into their own items/ directory in 1.21.4.
            if (!resourceExists("/assets/" + Constants.MOD_ID + "/items/" + id.getPath() + ".json")) {
                problems.add("item " + id + " has no item model json");
            }
            if (!lang.has(item.getDescriptionId())) {
                problems.add("item " + id + " has no lang key " + item.getDescriptionId());
            }
        }

        // All of them at once: one failure per run would make fixing these a slow loop.
        helper.assertTrue(problems.isEmpty(), problems.size() + " asset problems: " + String.join("; ", problems));
        helper.succeed();
    }

    /**
     * Every recipe file this mod ships either loaded, or carries this loader's load conditions and was
     * skipped by them. A file with neither failed to parse. On Fabric that was every conditional
     * recipe for a while: Fabric's datagen wrote them without conditions, and the NeoForge copy that
     * shadowed it carries a key Fabric ignores - so only this loader's own key counts.
     */
    private static void everyRecipeLoadsOrIsConditionedOff(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        FileToIdConverter recipes = FileToIdConverter.json("recipe");
        String conditionsKey = Services.PLATFORM.getPlatformName().equals("Fabric") ? "fabric:load_conditions" : "neoforge:conditions";
        List<String> failed = new ArrayList<>();

        recipes.listMatchingResources(server.getResourceManager()).forEach((file, resource) -> {
            Identifier id = recipes.fileToId(file);
            if (!id.getNamespace().equals(Constants.MOD_ID) || server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, id)).isPresent()) {
                return;
            }

            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (!json.has(conditionsKey)) {
                    failed.add(id.toString());
                }
            } catch (IOException e) {
                failed.add(id + " (" + e.getMessage() + ")");
            }
        });

        helper.assertTrue(failed.isEmpty(), failed.size() + " recipes failed to load without being conditioned off: " + String.join(", ", failed.subList(0, Math.min(10, failed.size()))));
        helper.succeed();
    }
}
