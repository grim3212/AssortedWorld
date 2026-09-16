package com.grim3212.assorted.world.client.data;

import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

/**
 * Item models for everything but block items, which {@link WorldBlockstateProvider} models. That is
 * the two plants, whose items are flat sprites rather than their block models.
 */
public class WorldItemModelProvider extends ModelProvider {

    public WorldItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted World item models";
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> !(holder.value() instanceof BlockItem));
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // The reed's item is a flat sprite, not its block model. generateFlatItem derives the
        // texture from the item id, which is what the old `item/<name>` layer0 resolved to anyway.
        itemModels.generateFlatItem(WorldBlocks.GUNPOWDER_REED.get().asItem(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WorldBlocks.GLOWSTONE_SEEDS.get().asItem(), ModelTemplates.FLAT_ITEM);
    }
}
