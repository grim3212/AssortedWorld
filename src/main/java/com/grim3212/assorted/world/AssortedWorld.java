package com.grim3212.assorted.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.grim3212.assorted.world.client.data.WorldBlockstateProvider;
import com.grim3212.assorted.world.client.data.WorldItemModelProvider;
import com.grim3212.assorted.world.client.proxy.ClientProxy;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.data.WorldBiomeTagProvider;
import com.grim3212.assorted.world.common.data.WorldBlockTagProvider;
import com.grim3212.assorted.world.common.data.WorldFeatureProvider;
import com.grim3212.assorted.world.common.data.WorldItemTagProvider;
import com.grim3212.assorted.world.common.data.WorldLootProvider;
import com.grim3212.assorted.world.common.data.WorldRecipes;
import com.grim3212.assorted.world.common.data.WorldStructureProvider;
import com.grim3212.assorted.world.common.gen.feature.WorldFeatures;
import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.grim3212.assorted.world.common.handler.WorldConfig;
import com.grim3212.assorted.world.common.item.WorldItems;
import com.grim3212.assorted.world.common.proxy.IProxy;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

	public static final CreativeModeTab ASSORTED_WORLD_ITEM_GROUP = (new CreativeModeTab(AssortedWorld.MODID) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(WorldBlocks.RANDOMITE_ORE.get());
		}
	});

	public AssortedWorld() {
		DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
		proxy.starting();

		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		modBus.addListener(this::gatherData);

		WorldBlocks.BLOCKS.register(modBus);
		WorldItems.ITEMS.register(modBus);
		WorldStructures.STRUCTURE_TYPES.register(modBus);
		WorldStructures.STRUCTURE_PIECES.register(modBus);
		WorldFeatures.FEATURES.register(modBus);

		ModLoadingContext.get().registerConfig(Type.COMMON, WorldConfig.COMMON_SPEC);
	}

	private void gatherData(final GatherDataEvent event) {
		DataGenerator datagenerator = event.getGenerator();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();

		WorldBlockTagProvider blockTagProvider = new WorldBlockTagProvider(datagenerator, fileHelper);
		datagenerator.addProvider(event.includeServer(), blockTagProvider);
		datagenerator.addProvider(event.includeServer(), new WorldBiomeTagProvider(datagenerator, fileHelper));
		datagenerator.addProvider(event.includeServer(), new WorldItemTagProvider(datagenerator, blockTagProvider, fileHelper));
		datagenerator.addProvider(event.includeServer(), new WorldLootProvider(datagenerator));
		datagenerator.addProvider(event.includeServer(), new WorldRecipes(datagenerator));

		final RegistryAccess registries = RegistryAccess.builtinCopy();
		final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
		datagenerator.addProvider(event.includeServer(), WorldFeatureProvider.getConfiguredFeatures(datagenerator, fileHelper, registries, ops));
		datagenerator.addProvider(event.includeServer(), WorldFeatureProvider.getPlacedFeatures(datagenerator, fileHelper, registries, ops));
		datagenerator.addProvider(event.includeServer(), WorldFeatureProvider.getBiomeModifiers(datagenerator, fileHelper, registries, ops));

		datagenerator.addProvider(event.includeServer(), WorldStructureProvider.getStructures(datagenerator, fileHelper, registries, ops));
		datagenerator.addProvider(event.includeServer(), WorldStructureProvider.getStructureSets(datagenerator, fileHelper, registries, ops));

		datagenerator.addProvider(event.includeClient(), new WorldBlockstateProvider(datagenerator, fileHelper));
		datagenerator.addProvider(event.includeClient(), new WorldItemModelProvider(datagenerator, fileHelper));
	}
}
