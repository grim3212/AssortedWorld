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
 * Forge's {@code ItemModelProvider} and {@code ItemModelBuilder} are gone, and so is the idea that
 * an item model is a single json: an item points at a data-driven {@code ItemModel} in
 * {@code assets/<ns>/items/}, which names the model to draw. {@link ItemModelGenerators} writes both
 * halves.
 * <p>
 * Block items are not listed here at all - they belong to {@link WorldBlockstateProvider}, which
 * points each one at its block model. The only item this provider owns is the gunpowder reed, whose
 * item is a flat sprite rather than its cross-shaped block model.
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
    }
}
