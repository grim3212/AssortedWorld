package com.grim3212.assorted.world.common.data;

import java.util.function.Consumer;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

public class WorldRecipes extends RecipeProvider {

	public WorldRecipes(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(Items.GUNPOWDER, 1).requires(WorldBlocks.GUNPOWDER_REED.get()).unlockedBy("has_gunpowder_reeds", has(WorldBlocks.GUNPOWDER_REED.get())).save(consumer);
		ShapedRecipeBuilder.shaped(WorldBlocks.GUNPOWDER_REED.get(), 1).define('X', Tags.Items.GUNPOWDER).define('R', Items.SUGAR_CANE).pattern("XXX").pattern("XRX").pattern("XXX").unlockedBy("has_gunpowder", has(Tags.Items.GUNPOWDER)).save(consumer);
	}

	private ResourceLocation prefix(String name) {
		return new ResourceLocation(AssortedWorld.MODID, name);
	}

	@Override
	public String getName() {
		return "Assorted World recipes";
	}
}