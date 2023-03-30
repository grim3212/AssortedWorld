package com.grim3212.assorted.world.common.data;

import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.data.WorldBlockTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ForgeBlockTagProvider extends BlockTagsProvider {

    private final WorldBlockTagProvider commonBlocks;

    public ForgeBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Constants.MOD_ID, existingFileHelper);
        this.commonBlocks = new WorldBlockTagProvider(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.commonBlocks.addCommonTags(this::tag);
    }

    @Override
    public String getName() {
        return "Assorted World block tags";
    }
}
