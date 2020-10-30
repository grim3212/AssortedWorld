package com.grim3212.assorted.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.grim3212.assorted.world.client.data.WorldBlockstateProvider;
import com.grim3212.assorted.world.client.data.WorldItemModelProvider;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.data.WorldBlockTagProvider;
import com.grim3212.assorted.world.common.data.WorldItemTagProvider;
import com.grim3212.assorted.world.common.data.WorldLootProvider;
import com.grim3212.assorted.world.common.gen.WorldGeneration;
import com.grim3212.assorted.world.common.handler.WorldConfig;
import com.grim3212.assorted.world.common.item.WorldItems;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AssortedWorld.MODID)
public class AssortedWorld {
	public static final String MODID = "assortedworld";
	public static final String MODNAME = "Assorted World";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final ItemGroup ASSORTED_WORLD_ITEM_GROUP = (new ItemGroup(AssortedWorld.MODID) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(WorldBlocks.RANDOMITE_ORE.get());
		}
	});

	public AssortedWorld() {

		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		modBus.addListener(this::setup);
		modBus.addListener(this::gatherData);

		WorldBlocks.BLOCKS.register(modBus);
		WorldItems.ITEMS.register(modBus);

		ModLoadingContext.get().registerConfig(Type.CLIENT, WorldConfig.CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(Type.COMMON, WorldConfig.COMMON_SPEC);

		MinecraftForge.EVENT_BUS.register(new WorldGeneration());
	}

	private void setup(final FMLCommonSetupEvent event) {
	}

	private void gatherData(GatherDataEvent event) {
		DataGenerator datagenerator = event.getGenerator();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();

		if (event.includeServer()) {
			WorldBlockTagProvider blockTagProvider = new WorldBlockTagProvider(datagenerator, fileHelper);
			datagenerator.addProvider(blockTagProvider);
			datagenerator.addProvider(new WorldItemTagProvider(datagenerator, blockTagProvider, fileHelper));
			datagenerator.addProvider(new WorldLootProvider(datagenerator));
		}

		if (event.includeClient()) {
			datagenerator.addProvider(new WorldBlockstateProvider(datagenerator, fileHelper));
			datagenerator.addProvider(new WorldItemModelProvider(datagenerator, fileHelper));
		}
	}
}
