package com.grim3212.assorted.world.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.world.Constants;
import com.grim3212.assorted.world.common.block.WorldBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

/**
 * This mod's section of the instruction manual. Every block and item has to open a page, or the
 * provider refuses to generate.
 */
public class WorldManualProvider extends LibManualProvider {

    public WorldManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addChapters() {
        this.section(40, WorldBlocks.RANDOMITE_ORE.get());

        ChapterBuilder randomite = this.chapter("randomite");
        randomite.items("ore", WorldBlocks.RANDOMITE_ORE.get(), WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get())
                .every(50)
                .opens(WorldBlocks.RANDOMITE_ORE.get(), WorldBlocks.DEEPSLATE_RANDOMITE_ORE.get());
        randomite.image("finding", picture("randomite"), 94, 104);

        Block[] runes = WorldBlocks.runeBlocks();
        ChapterBuilder runeChapter = this.chapter("runes");
        runeChapter.items("runes", runes).opens(runes);

        ChapterBuilder structures = this.chapter("structures");
        structures.image("ruins", picture("ruins"), 113, 104);
        structures.image("spires", picture("spires"), 79, 104);
        structures.image("fountains", picture("fountains"), 98, 104);
        structures.image("pyramids", picture("pyramids"), 113, 104);
        structures.image("snowballs", picture("snowballs"), 58, 104);
        structures.image("water_domes", picture("water_domes"), 128, 95);

        ChapterBuilder plants = this.chapter("plants");
        plants.recipes("gunpowder_reed", "gunpowder_reed").opens(WorldBlocks.GUNPOWDER_REED.get());
        plants.recipes("gunpowder", "gunpowder");
    }

    /** The screenshots under {@code textures/gui/manual}, sized to leave room for the text below. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
