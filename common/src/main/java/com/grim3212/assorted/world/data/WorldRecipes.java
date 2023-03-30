package com.grim3212.assorted.world.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class WorldRecipes extends ConditionalRecipeProvider {

    public WorldRecipes(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public void registerConditions() {
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> consumer) {
        super.buildRecipes(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GUNPOWDER, 1).requires(WorldBlocks.GUNPOWDER_REED.get()).unlockedBy("has_gunpowder_reeds", has(WorldBlocks.GUNPOWDER_REED.get())).save(consumer, new ResourceLocation(Constants.MOD_ID, "gunpowder"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WorldBlocks.GUNPOWDER_REED.get(), 1).define('X', LibCommonTags.Items.GUNPOWDER).define('R', Items.SUGAR_CANE).pattern("XXX").pattern("XRX").pattern("XXX").unlockedBy("has_gunpowder", has(LibCommonTags.Items.GUNPOWDER)).save(consumer);
    }
}