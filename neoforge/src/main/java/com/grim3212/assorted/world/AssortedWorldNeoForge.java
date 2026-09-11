package com.grim3212.assorted.world;

import com.grim3212.assorted.world.client.data.WorldLanguageProvider;
import com.grim3212.assorted.lib.data.ForgeBiomeTagProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.lib.data.ForgeDatapackRegistryProvider;
import com.grim3212.assorted.world.client.data.WorldBlockstateProvider;
import com.grim3212.assorted.world.client.data.WorldItemModelProvider;
import com.grim3212.assorted.world.data.WorldBiomeTagProvider;
import com.grim3212.assorted.world.data.WorldBlockLoot;
import com.grim3212.assorted.world.data.WorldBlockTagProvider;
import com.grim3212.assorted.world.data.WorldChestLoot;
import com.grim3212.assorted.world.data.WorldGenData;
import com.grim3212.assorted.world.data.WorldItemTagProvider;
import com.grim3212.assorted.world.data.WorldRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedWorldNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedWorldNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        WorldCommonMod.init();
    }

    /**
     * {@code ExistingFileHelper} was removed from datagen, the event owns the provider list now
     * ({@code addProvider}), and the include flags are gone because the server and client halves are
     * separate events. Getting this split wrong is quiet: the wrong event runs and reports
     * "All providers took: 0 ms" with a successful build.
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ForgeBlockTagProvider blockTagProvider = event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new WorldBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, new WorldItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        event.addProvider(new ForgeBiomeTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new WorldBiomeTagProvider(packOutput, lookupProvider)));
        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new WorldRecipes.Runner(packOutput, lookupProvider));
        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(WorldBlockLoot::new, LootContextParamSets.BLOCK), new LootTableProvider.SubProviderEntry(WorldChestLoot::new, LootContextParamSets.CHEST)), lookupProvider));
        event.addProvider(new ForgeDatapackRegistryProvider(Constants.MOD_ID, new WorldGenData()).datpackEntriesProvider(packOutput, lookupProvider));
    }

    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new WorldBlockstateProvider(packOutput));
        event.addProvider(new WorldItemModelProvider(packOutput));
        event.addProvider(new WorldLanguageProvider(packOutput));
    }
}
