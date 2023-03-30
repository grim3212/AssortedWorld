package com.grim3212.assorted.world.common.data;

import com.grim3212.assorted.world.data.WorldBlockLoot;
import com.grim3212.assorted.world.data.WorldRecipes;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.List;

public class AssortedWorldFabricDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        
        FabricBlockTagProvider provider = pack.addProvider(FabricBlockTagProvider::new);
        pack.addProvider((output, registriesFuture) -> new FabricItemTagProvider(output, registriesFuture, provider));
        pack.addProvider((output, registriesFuture) -> new FabricBiomeTagProvider(output, registriesFuture));

        pack.addProvider((output, registriesFuture) -> new WorldRecipes(output));
        pack.addProvider((output, registriesFuture) -> new LootTableProvider(output, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(WorldBlockLoot::new, LootContextParamSets.BLOCK))));
    }
}
