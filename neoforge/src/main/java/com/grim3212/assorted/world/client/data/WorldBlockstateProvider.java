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
 * Forge's {@code BlockStateProvider}, {@code ConfiguredModel} and {@code ExistingFileHelper} are
 * gone. Block states and models come from vanilla's {@link ModelProvider} now, which hands a
 * {@link BlockModelGenerators} to {@link #registerModels}.
 * <p>
 * The item half is {@link WorldItemModelProvider}. Because one {@link ModelProvider} writes both,
 * the two are kept apart by narrowing what each claims to know about - this one owns every block
 * plus the block items, which {@link ModelProvider} points at the block model on its own.
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
    }

    /**
     * A plant drawn as two crossed quads.
     * <p>
     * Vanilla's own {@code createCrossBlock} is private, so the two halves it does are spelled out:
     * bake the {@code cross} template against the block's texture, then emit a single-variant
     * blockstate pointing at it.
     * <p>
     * The 1.20.1 version also called {@code .renderType("minecraft:cutout")}. That key does not
     * exist in 26.2 - the render layer is derived from the texture's own alpha by
     * {@code SpriteContents} and {@code ChunkSectionLayer.byTransparency} - so it is simply dropped
     * rather than translated. The reed still draws as cutout because its texture says so.
     */
    private void cross(BlockModelGenerators blockModels, Block block) {
        MultiVariant model = BlockModelGenerators.plainVariant(ModelTemplates.CROSS.create(block, TextureMapping.cross(block), blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, model));
    }
}
