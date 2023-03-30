package com.grim3212.assorted.world.common.data;

import com.grim3212.assorted.world.data.WorldBiomeTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;

import java.util.concurrent.CompletableFuture;

public class FabricBiomeTagProvider extends BiomeTagsProvider {

    private final WorldBiomeTagProvider commonBiomes;

    public FabricBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.commonBiomes = new WorldBiomeTagProvider(output, lookup);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.commonBiomes.addCommonTags(this::tag);
    }
}
