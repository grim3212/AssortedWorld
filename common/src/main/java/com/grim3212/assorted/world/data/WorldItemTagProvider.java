package com.grim3212.assorted.world.data;

import com.grim3212.assorted.lib.data.LibItemTagProvider;
import com.grim3212.assorted.world.api.WorldTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class WorldItemTagProvider extends LibItemTagProvider {

    public WorldItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, TagsProvider<Block> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, IntrinsicTagAppender<Item>> tagger, Consumer<Tuple<TagKey<Block>, TagKey<Item>>> copier) {
        copier.accept(new Tuple<>(WorldTags.Blocks.ORES_RANDOMITE, WorldTags.Items.ORES_RANDOMITE));
        copier.accept(new Tuple<>(WorldTags.Blocks.RUNES, WorldTags.Items.RUNES));
    }
}
