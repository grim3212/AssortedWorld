package com.grim3212.assorted.world.common.handlers;

import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class WorldCreativeItems {

    public static final RegistryProvider<CreativeModeTab> CREATIVE_TABS = RegistryProvider.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "tab"));

    // CreativeModeTab.Output is protected in 26.2 vanilla, so a display items generator cannot be
    // written against the plain game jar. The tab is registered empty and filled through the
    // library's modifyCreativeTab hook instead, which both loaders already implement on top of
    // their own creative tab events.
    // CreativeModeTab.builder(Row, int) is deprecated by NeoForge's patches only; the vanilla jar
    // this module compiles against has no other builder.
    @SuppressWarnings("deprecation")
    public static final IRegistryObject CREATIVE_TAB = CREATIVE_TABS.register("tab", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + Constants.MOD_ID))
            .icon(() -> new ItemStack(WorldBlocks.RANDOMITE_ORE.get()))
            .build());

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        items.add(WorldBlocks.RANDOMITE_ORE.get());
        items.add(WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get());
        items.add(WorldBlocks.GUNPOWDER_REED.get());
        for (Block rune : WorldBlocks.runeBlocks()) {
            items.add(rune);
        }

        return items.getItems();
    }

    public static void init() {
        Services.PLATFORM.modifyCreativeTab(CREATIVE_TAB_KEY, WorldCreativeItems::getCreativeItems);
    }
}
