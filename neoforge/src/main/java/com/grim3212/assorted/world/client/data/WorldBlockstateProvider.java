package com.grim3212.assorted.world.client.data;

import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

/**
 * Block states and block models. This owns every block and block item; {@link
 * WorldItemModelProvider} owns the rest, so the two never write the same file.
 */
public class WorldBlockstateProvider extends ModelProvider {

    public WorldBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted World block states";
    }

    /**
     * Only the block items belong here; everything else is {@link WorldItemModelProvider}'s, so the
     * two providers never write the same file.
     */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(WorldBlocks.RANDOMITE_ORE.get());
        blockModels.createTrivialCube(WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get());

        for (Block rune : WorldBlocks.runeBlocks()) {
            blockModels.createTrivialCube(rune);
        }

        cross(blockModels, WorldBlocks.GUNPOWDER_REED.get());
        // Every growth step looks the same, as it did in GrimPack
        cross(blockModels, WorldBlocks.GLOWSTONE_SEEDS.get());
    }

    /**
     * A plant drawn as two crossed quads: the {@code cross} template baked against the block
     * texture. The texture's alpha makes it cutout.
     */
    private void cross(BlockModelGenerators blockModels, Block block) {
        MultiVariant model = BlockModelGenerators.plainVariant(ModelTemplates.CROSS.create(block, TextureMapping.cross(block), blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, model));
    }
}
