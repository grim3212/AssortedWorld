package com.grim3212.assorted.world;

import com.grim3212.assorted.lib.data.FabricBiomeTagProvider;
import com.grim3212.assorted.lib.data.FabricBlockTagProvider;
import com.grim3212.assorted.lib.data.FabricItemTagProvider;
import com.grim3212.assorted.lib.data.FabricWorldGenProvider;
import com.grim3212.assorted.world.data.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.List;

public class AssortedWorldFabricDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        FabricBlockTagProvider provider = pack.addProvider((output, registriesFuture) -> new FabricBlockTagProvider(output, registriesFuture, new WorldBlockTagProvider(output, registriesFuture)));
        pack.addProvider((output, registriesFuture) -> new FabricItemTagProvider(output, registriesFuture, provider, new WorldItemTagProvider(output, registriesFuture, provider)));
        pack.addProvider((output, registriesFuture) -> new FabricBiomeTagProvider(output, registriesFuture, new WorldBiomeTagProvider(output, registriesFuture)));

        pack.addProvider((output, registriesFuture) -> new WorldRecipes(output));
        pack.addProvider((output, registriesFuture) -> new LootTableProvider(output, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(WorldBlockLoot::new, LootContextParamSets.BLOCK))));

        pack.addProvider((output, registriesFuture) -> new FabricWorldGenProvider(output, registriesFuture, Constants.MOD_ID, getWorldGenData()));
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        getWorldGenData().addToWorldGem(registryBuilder);
    }

    private WorldGenData worldGenData;

    private WorldGenData getWorldGenData() {
        if (this.worldGenData == null) {
            this.worldGenData = new WorldGenData();
        }
        return this.worldGenData;
    }
}
