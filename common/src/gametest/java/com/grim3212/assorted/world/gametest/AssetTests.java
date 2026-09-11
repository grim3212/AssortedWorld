package com.grim3212.assorted.world.gametest;

import net.minecraft.locale.Language;
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
        out.accept("every_item_tag_has_a_name", AssetTests::everyItemTagHasAName);
    }

    /**
     * Every block and item has a model and a name, and so does the creative tab. Walking the
     * registries also catches anything registered but never added to the tab.
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
     * Every recipe file either loaded or was skipped by this loader's own load conditions; anything
     * else failed to parse.
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

    /**
     * Every non-vanilla item tag has a {@code tag.item.<namespace>.<path>} name, the check Fabric
     * API warns about at dev startup. Both loaders name the standard c: tags, so anything missing
     * is ours.
     */
    private static void everyItemTagHasAName(GameTestHelper helper) {
        Language language = Language.getInstance();
        List<String> missing = helper.getLevel().registryAccess().lookupOrThrow(Registries.ITEM).getTags()
                .map(tag -> tag.key().location())
                .filter(id -> !"minecraft".equals(id.getNamespace()))
                .map(id -> "tag.item." + id.getNamespace() + "." + id.getPath().replace('/', '.'))
                .filter(key -> !language.has(key))
                .sorted()
                .toList();
        helper.assertTrue(missing.isEmpty(), "item tags with no name in any lang file: " + missing);
        helper.succeed();
    }
}
