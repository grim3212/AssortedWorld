package com.grim3212.assorted.world;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.gen.WorldBiomeModifiers;
import com.grim3212.assorted.world.common.gen.feature.WorldFeatures;
import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.grim3212.assorted.world.common.handlers.WorldCreativeItems;
import com.grim3212.assorted.world.config.WorldCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class WorldCommonMod {

    public static final WorldCommonConfig COMMON_CONFIG = new WorldCommonConfig();

    public static void init() {
        WorldBlocks.init();
        WorldStructures.init();
        WorldFeatures.init();
        WorldBiomeModifiers.init();

        Services.PLATFORM.registerCreativeTab(new ResourceLocation(Constants.MOD_ID, "tab"), Component.translatable("itemGroup." + Constants.MOD_ID), () -> new ItemStack(WorldBlocks.RANDOMITE_ORE.get()), () -> WorldCreativeItems.getCreativeItems());
    }
}
