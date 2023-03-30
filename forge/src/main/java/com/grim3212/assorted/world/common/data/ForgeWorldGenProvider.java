package com.grim3212.assorted.world.common.data;

import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.data.WorldGenData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ForgeWorldGenProvider {

    public static DatapackBuiltinEntriesProvider datpackEntriesProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
        RegistrySetBuilder coreBuilder = new RegistrySetBuilder();
        WorldGenData.addToRegistrySetBuilder(coreBuilder);
        return new DatapackBuiltinEntriesProvider(output, registries, coreBuilder, Set.of(Constants.MOD_ID));
    }

}
