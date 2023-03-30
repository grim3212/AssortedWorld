package com.grim3212.assorted.world;

import com.grim3212.assorted.lib.data.ForgeBiomeTagProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.world.client.WorldClient;
import com.grim3212.assorted.world.client.data.WorldBlockstateProvider;
import com.grim3212.assorted.world.client.data.WorldItemModelProvider;
import com.grim3212.assorted.world.common.data.ForgeWorldGenProvider;
import com.grim3212.assorted.world.data.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedWorld {

    public AssortedWorld() {
        // Initialize client side
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> WorldClient::init);

        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::gatherData);

        WorldCommonMod.init();
    }

    private void gatherData(final GatherDataEvent event) {
        DataGenerator datagenerator = event.getGenerator();
        PackOutput packOutput = datagenerator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ForgeBlockTagProvider blockTagProvider = new ForgeBlockTagProvider(packOutput, lookupProvider, fileHelper, Constants.MOD_ID, new WorldBlockTagProvider(packOutput, lookupProvider));
        datagenerator.addProvider(event.includeServer(), blockTagProvider);
        datagenerator.addProvider(event.includeServer(), new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider, fileHelper, Constants.MOD_ID, new WorldItemTagProvider(packOutput, lookupProvider, blockTagProvider)));
        datagenerator.addProvider(event.includeServer(), new ForgeBiomeTagProvider(packOutput, lookupProvider, fileHelper, Constants.MOD_ID, new WorldBiomeTagProvider(packOutput, lookupProvider)));

        datagenerator.addProvider(event.includeServer(), new WorldRecipes(packOutput));
        datagenerator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(WorldBlockLoot::new, LootContextParamSets.BLOCK))));

        datagenerator.addProvider(event.includeServer(), ForgeWorldGenProvider.datpackEntriesProvider(packOutput, lookupProvider));

        datagenerator.addProvider(event.includeClient(), new WorldBlockstateProvider(packOutput, fileHelper));
        datagenerator.addProvider(event.includeClient(), new WorldItemModelProvider(packOutput, fileHelper));
    }
}
