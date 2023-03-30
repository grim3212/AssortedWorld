package com.grim3212.assorted.world.common.data;

import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.data.WorldBiomeTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ForgeBiomeTagProvider extends BiomeTagsProvider {

    private final WorldBiomeTagProvider commonBiomes;

    public ForgeBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper existingFileHelper) {
        super(output, lookup, Constants.MOD_ID, existingFileHelper);
        this.commonBiomes = new WorldBiomeTagProvider(output, lookup);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.commonBiomes.addCommonTags(this::tag);
    }

    @Override
    public String getName() {
        return "Assorted World biome tags";
    }
}
