package com.grim3212.assorted.world.client.data;

import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldBlockstateProvider extends BlockStateProvider {

    private static final ResourceLocation CUTOUT_RENDER_TYPE = new ResourceLocation("minecraft:cutout");

    public WorldBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Constants.MOD_ID, exFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted World block states";
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(WorldBlocks.RANDOMITE_ORE.get());
        simpleBlock(WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get());

        for (Block rune : WorldBlocks.runeBlocks()) {
            simpleBlock(rune);
        }

        cross(WorldBlocks.GUNPOWDER_REED.get());
    }

    private static String name(Block i) {
        return ForgeRegistries.BLOCKS.getKey(i).getPath();
    }

    private void cross(Block b) {
        String s = name(b);

        getVariantBuilder(b).partialState().setModels(new ConfiguredModel(models().cross(s, blockTexture(b)).renderType(CUTOUT_RENDER_TYPE)));
    }
}
