package com.grim3212.assorted.world.common.handlers;

import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class WorldCreativeItems {

    public static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        items.add(WorldBlocks.RANDOMITE_ORE.get());
        items.add(WorldBlocks.GUNPOWDER_REED.get());
        for (Block rune : WorldBlocks.runeBlocks()) {
            items.add(rune);
        }

        return items.getItems();
    }
}
