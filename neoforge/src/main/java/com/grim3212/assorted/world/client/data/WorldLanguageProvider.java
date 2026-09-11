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
    }
}
