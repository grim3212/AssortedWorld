package com.grim3212.assorted.world.client;

public class WorldClient {

    public static void init() {
        // TODO(26.2): the registerRenderType call for GUNPOWDER_REED that used to live here is gone.
        //  What it did: told ItemBlockRenderTypes that the gunpowder reed draws in the cutout chunk
        //  layer. Why it cannot be expressed: ItemBlockRenderTypes was deleted and RenderType lost
        //  its solid()/cutout()/translucent() factories - the chunk layer is a ChunkSectionLayer
        //  derived per quad while baking, from the transparency of the sprite the quad uses (see
        //  BakedQuad.MaterialInfo#of). A block declares its layer from its model json with
        //  "render_type" instead, so the gunpowder reed block model needs
        //  "render_type": "minecraft:cutout" adding in datagen. AssortedLib keeps
        //  IClientHelper#registerRenderType as a no-op on both loaders; calling it would have
        //  looked correct and done nothing.
    }
}
