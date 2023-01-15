package com.grim3212.assorted.world;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.grim3212.assorted.world.client.data.WorldBlockstateProvider;
import com.grim3212.assorted.world.client.data.WorldItemModelProvider;
import com.grim3212.assorted.world.client.proxy.ClientProxy;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.creative.WorldCreativeTab;
import com.grim3212.assorted.world.common.data.WorldBiomeTagProvider;
import com.grim3212.assorted.world.common.data.WorldBlockTagProvider;
import com.grim3212.assorted.world.common.data.WorldGenProvider;
import com.grim3212.assorted.world.common.data.WorldItemTagProvider;
import com.grim3212.assorted.world.common.data.WorldLootProvider;
import com.grim3212.assorted.world.common.data.WorldLootProvider.BlockTables;
import com.grim3212.assorted.world.common.data.WorldRecipes;
import com.grim3212.assorted.world.common.gen.feature.WorldFeatures;
import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.grim3212.assorted.world.common.handler.WorldConfig;
import com.grim3212.assorted.world.common.item.WorldItems;
import com.grim3212.assorted.world.common.proxy.IProxy;

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
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AssortedWorld.MODID)
public class AssortedWorld {
	public static final String MODID = "assortedworld";
	public static final String MODNAME = "Assorted World";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static IProxy proxy = new IProxy() {
	};

	public AssortedWorld() {
		DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
		proxy.starting();

		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		modBus.addListener(this::gatherData);
		modBus.addListener(WorldCreativeTab::registerTabs);

		WorldBlocks.BLOCKS.register(modBus);
		WorldItems.ITEMS.register(modBus);
		WorldStructures.STRUCTURE_TYPES.register(modBus);
		WorldStructures.STRUCTURE_PIECES.register(modBus);
		WorldFeatures.FEATURES.register(modBus);

		ModLoadingContext.get().registerConfig(Type.COMMON, WorldConfig.COMMON_SPEC);
	}

	private void gatherData(final GatherDataEvent event) {
		DataGenerator datagenerator = event.getGenerator();
		PackOutput packOutput = datagenerator.getPackOutput();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		WorldBlockTagProvider blockTagProvider = new WorldBlockTagProvider(packOutput, lookupProvider, fileHelper);
		datagenerator.addProvider(event.includeServer(), blockTagProvider);
		datagenerator.addProvider(event.includeServer(), new WorldBiomeTagProvider(packOutput, lookupProvider, fileHelper));
		datagenerator.addProvider(event.includeServer(), new WorldItemTagProvider(packOutput, lookupProvider, blockTagProvider, fileHelper));
		datagenerator.addProvider(event.includeServer(), new WorldLootProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(BlockTables::new, LootContextParamSets.BLOCK))));
		datagenerator.addProvider(event.includeServer(), new WorldRecipes(packOutput));
		datagenerator.addProvider(event.includeServer(), WorldGenProvider.datpackEntriesProvider(packOutput, lookupProvider));

		datagenerator.addProvider(event.includeClient(), new WorldBlockstateProvider(packOutput, fileHelper));
		datagenerator.addProvider(event.includeClient(), new WorldItemModelProvider(packOutput, fileHelper));
	}
}
