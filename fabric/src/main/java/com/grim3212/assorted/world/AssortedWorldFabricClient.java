package com.grim3212.assorted.world;

import com.grim3212.assorted.world.client.WorldClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedWorldFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WorldClient.init();
    }

}