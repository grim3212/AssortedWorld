package com.grim3212.assorted.world.common.crafting;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.world.WorldCommonMod;

public class WorldConditions {

    public static class Parts {
        public static final String GLOWSTONE_SEEDS = "glowstone_seeds";
    }

    public static void init() {
        Services.CONDITIONS.registerPartCondition(Parts.GLOWSTONE_SEEDS, () -> WorldCommonMod.COMMON_CONFIG.glowstoneSeedsEnabled.get());
    }
}
