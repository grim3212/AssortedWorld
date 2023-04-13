package com.grim3212.assorted.world;

import com.grim3212.assorted.world.common.block.WorldBlocks;
import com.grim3212.assorted.world.common.gen.WorldBiomeModifiers;
import com.grim3212.assorted.world.common.gen.feature.WorldFeatures;
import com.grim3212.assorted.world.common.gen.structure.WorldStructures;
import com.grim3212.assorted.world.common.handlers.WorldCreativeItems;
import com.grim3212.assorted.world.config.WorldCommonConfig;

public class WorldCommonMod {

    public static final WorldCommonConfig COMMON_CONFIG = new WorldCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        WorldBlocks.init();
        WorldStructures.init();
        WorldFeatures.init();
        WorldBiomeModifiers.init();
        WorldCreativeItems.init();
    }
}
