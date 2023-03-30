package com.grim3212.assorted.world.data;

import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.world.api.WorldTags;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.NotImplementedException;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class WorldBlockTagProvider extends VanillaBlockTagsProvider {

    public WorldBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        throw new NotImplementedException();
    }

    @Override
    protected IntrinsicTagAppender<Block> tag(TagKey<Block> tag) {
        throw new NotImplementedException();
    }

    public void addCommonTags(Function<TagKey<Block>, IntrinsicTagAppender<Block>> tagger) {
        tagger.apply(LibCommonTags.Blocks.ORES).addTag(WorldTags.Blocks.ORES_RANDOMITE);
        tagger.apply(WorldTags.Blocks.ORES_RANDOMITE).add(WorldBlocks.RANDOMITE_ORE.get(), WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get());
        tagger.apply(WorldTags.Blocks.RUNES).add(WorldBlocks.runeBlocks());

        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(WorldBlocks.runeBlocks());
        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(WorldBlocks.RANDOMITE_ORE.get(), WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get());
        tagger.apply(BlockTags.NEEDS_STONE_TOOL).add(WorldBlocks.RANDOMITE_ORE.get(), WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get());

        tagger.apply(BlockTags.MINEABLE_WITH_AXE).add(WorldBlocks.GUNPOWDER_REED.get());
    }
}
