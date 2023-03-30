package com.grim3212.assorted.world.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.client.renderer.RenderType;

public class WorldClient {

    public static void init() {
        ClientServices.CLIENT.registerRenderType(WorldBlocks.GUNPOWDER_REED::get, RenderType.cutout());
    }
}
