package com.grim3212.assorted.world.client.data;

import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldItemModelProvider extends ItemModelProvider {

    public WorldItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted World item models";
    }

    @Override
    protected void registerModels() {
        genericBlock(WorldBlocks.RANDOMITE_ORE.get());
        genericBlock(WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get());

        for (Block rune : WorldBlocks.runeBlocks()) {
            genericBlock(rune);
        }

        generatedItem(WorldBlocks.GUNPOWDER_REED.get());
    }

    private ItemModelBuilder generatedItem(String name) {
        return withExistingParent(name, "item/generated").texture("layer0", prefix("item/" + name));
    }

    private ItemModelBuilder generatedItem(Block i) {
        return generatedItem(name(i));
    }

    private ItemModelBuilder genericBlock(Block b) {
        String name = name(b);
        return withExistingParent(name, prefix("block/" + name));
    }

    private static String name(Block i) {
        return ForgeRegistries.BLOCKS.getKey(i).getPath();
    }

    private ResourceLocation prefix(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}
