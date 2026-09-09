package com.grim3212.assorted.world.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class WorldRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public WorldRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, Items.GUNPOWDER, 1).requires(WorldBlocks.GUNPOWDER_REED.get()).unlockedBy("has_gunpowder_reeds", has(WorldBlocks.GUNPOWDER_REED.get())).save(this.output, key("gunpowder"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, WorldBlocks.GUNPOWDER_REED.get(), 1).define('X', LibCommonTags.Items.GUNPOWDER).define('R', Items.SUGAR_CANE).pattern("XXX").pattern("XRX").pattern("XXX").unlockedBy("has_gunpowder", has(LibCommonTags.Items.GUNPOWDER)).save(this.output, key(name(WorldBlocks.GUNPOWDER_REED.get())));
    }

    /**
     * Recipe providers are not data providers any more - a {@link RecipeProvider.Runner} owns the
     * file writing and builds a fresh provider around the {@link RecipeOutput} it hands out. This is
     * what the loader datagen entry points register.
     */
    public static class Runner extends ConditionalRecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, Constants.MOD_ID);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new WorldRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }

    /**
     * Recipes are addressed by {@code ResourceKey<Recipe<?>>} rather than a raw id now.
     */
    private static ResourceKey<Recipe<?>> key(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, path));
    }
}
