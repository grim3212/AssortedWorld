package com.grim3212.assorted.world.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.world.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block, item or entity whose name is its id in title case needs
 * no line here (see {@link LibLanguageProvider}); these are the names that read differently, and
 * every key that is not a name.
 */
public class WorldLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public WorldLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedworld", "Assorted World");

        this.add("block.assortedworld.ur_rune", "\u00A73UR\u00A7r Rune");
        this.add("block.assortedworld.eoh_rune", "\u00A7dEOH\u00A7r Rune");
        this.add("block.assortedworld.hagel_rune", "\u00A73HAGEL\u00A7r Rune");
        this.add("block.assortedworld.eolh_rune", "\u00A73EOLH\u00A7r Rune");
        this.add("block.assortedworld.cen_rune", "\u00A73CEN\u00A7r Rune");
        this.add("block.assortedworld.ger_rune", "\u00A73GER\u00A7r Rune");
        this.add("block.assortedworld.rad_rune", "\u00A73RAD\u00A7r Rune");
        this.add("block.assortedworld.is_rune", "\u00A7dIS\u00A7r Rune");
        this.add("block.assortedworld.daeg_rune", "\u00A7dDAEG\u00A7r Rune");
        this.add("block.assortedworld.tyr_rune", "\u00A7dTYR\u00A7r Rune");
        this.add("block.assortedworld.beorc_rune", "\u00A73BEORC\u00A7r Rune");
        this.add("block.assortedworld.lagu_rune", "\u00A73LAGU\u00A7r Rune");
        this.add("block.assortedworld.odal_rune", "\u00A73ODAL\u00A7r Rune");
        this.add("block.assortedworld.nyd_rune", "\u00A7dNYD\u00A7r Rune");
        this.add("block.assortedworld.thorn_rune", "\u00A7dTHORN\u00A7r Rune");
        this.add("block.assortedworld.os_rune", "\u00A7dOS\u00A7r Rune");

        this.add("tag.item.assortedworld.runes", "Runes");
        this.add("tag.item.c.ores.randomite", "Randomite Ores");

        this.addManual();
    }

    /** The chapters in {@code assets/assortedworld/manual} name these keys. */
    private void addManual() {
        this.add("manual.assortedworld.title", "Assorted World");
        this.add("manual.assortedworld.description",
                "What this mod buries, builds and grows out in the world, and what is worth going to look for.");

        this.addRandomiteChapter();
        this.addRunesChapter();
        this.addStructuresChapter();
        this.addPlantsChapter();
    }

    private void addRandomiteChapter() {
        this.add("manual.assortedworld.chapter.randomite", "Randomite");

        this.add("manual.assortedworld.chapter.randomite.ore.title", "Randomite Ore");
        this.add("manual.assortedworld.chapter.randomite.ore",
                "Randomite looks like one more ore in the wall and drops as any of them. What it gives is "
                        + "decided when it breaks, not when it generates, so two blocks side by side rarely "
                        + "drop the same.");

        this.add("manual.assortedworld.chapter.randomite.finding.title", "Finding Randomite");
        this.add("manual.assortedworld.chapter.randomite.finding",
                "It generates all over and looks enough like the other ores you could mistake it for another.");
    }

    private void addRunesChapter() {
        this.add("manual.assortedworld.chapter.runes", "Runes");

        this.add("manual.assortedworld.chapter.runes.runes.title", "Runes");
        this.add("manual.assortedworld.chapter.runes.runes",
                "Runes are carved blocks that hold an effect and hand it to whoever stands or uses them. There are "
                        + "sixteen, and they are not crafted: every one in the world was placed by a Ancient Structure.");
    }

    private void addStructuresChapter() {
        this.add("manual.assortedworld.chapter.structures", "Structures");

        this.add("manual.assortedworld.chapter.structures.ruins.title", "Ruins");
        this.add("manual.assortedworld.chapter.structures.ruins",
                "Ruins are what is left of a civilisation that was here first. Most have decayed down to a "
                        + "footprint and a few standing walls, but they still hold treasures worth exploring.");

        this.add("manual.assortedworld.chapter.structures.spires.title", "Spires");
        this.add("manual.assortedworld.chapter.structures.spires",
                "Spires are towers of stone reaching for the sky.");

        this.add("manual.assortedworld.chapter.structures.fountains.title", "Fountains");
        this.add("manual.assortedworld.chapter.structures.fountains",
                "Fountains are large stone structures surrounded by flowing water. Watch out for what is inside.");

        this.add("manual.assortedworld.chapter.structures.pyramids.title", "Pyramids");
        this.add("manual.assortedworld.chapter.structures.pyramids",
                "Pyramids are big decaying structures lost to time in the desert.");

        this.add("manual.assortedworld.chapter.structures.snowballs.title", "Snowballs");
        this.add("manual.assortedworld.chapter.structures.snowballs",
                "Snowballs are enormous stacked drifts of snow and ice that look a great deal like a snowman left to grow.");

        this.add("manual.assortedworld.chapter.structures.water_domes.title", "Water Domes");
        this.add("manual.assortedworld.chapter.structures.water_domes",
                "Water domes sit on the sea floor, ribbed with a material that varies from dome to dome.");
    }

    private void addPlantsChapter() {
        this.add("manual.assortedworld.chapter.plants", "Gunpowder Reed");

        this.add("manual.assortedworld.chapter.plants.gunpowder_reed.title", "Gunpowder Reed");
        this.add("manual.assortedworld.chapter.plants.gunpowder_reed",
                "Gunpowder reed grows like sugar cane and glows faintly. Plant it once and it keeps growing, "
                        + "which turns a one off pile of gunpowder into a supply.");

        this.add("manual.assortedworld.chapter.plants.gunpowder.title", "Back to Gunpowder");
        this.add("manual.assortedworld.chapter.plants.gunpowder",
                "A reed breaks back down into gunpowder one for one.");
    }
}
