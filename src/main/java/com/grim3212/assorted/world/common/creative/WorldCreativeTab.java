package com.grim3212.assorted.world.common.creative;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.item.WorldItems;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;

public class WorldCreativeTab {

	public static void registerTabs(final CreativeModeTabEvent.Register event) {
		event.registerCreativeModeTab(new ResourceLocation(AssortedWorld.MODID, "tab"), builder -> builder.title(Component.translatable("itemGroup.assortedworld")).icon(() -> new ItemStack(WorldBlocks.RANDOMITE_ORE.get())).displayItems((enabledFlags, populator, hasPermissions) -> {
			populator.acceptAll(WorldItems.ITEMS.getEntries().stream().map(itm -> new ItemStack(itm.get())).toList());
		}));
	}
}
