package com.grim3212.assorted.world.common.block;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.world.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;
import java.util.function.Supplier;

public class WorldBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID);
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID);

    public static final IRegistryObject<Block> RANDOMITE_ORE = register("randomite_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.6f, 1.0f).requiresCorrectToolForDrops(), UniformInt.of(2, 5)));
    public static final IRegistryObject<Block> DEEPSLATE_RANDOMITE_ORE = register("deepslate_randomite_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(4.5F, 3.0f).requiresCorrectToolForDrops(), UniformInt.of(2, 5)));

    public static final IRegistryObject<RuneBlock> UR_RUNE = register("ur_rune", () -> new RuneBlock(RuneBlock.RuneType.UR));
    public static final IRegistryObject<RuneBlock> EOH_RUNE = register("eoh_rune", () -> new RuneBlock(RuneBlock.RuneType.EOH));
    public static final IRegistryObject<RuneBlock> HAGEL_RUNE = register("hagel_rune", () -> new RuneBlock(RuneBlock.RuneType.HAGEL));
    public static final IRegistryObject<RuneBlock> EOLH_RUNE = register("eolh_rune", () -> new RuneBlock(RuneBlock.RuneType.EOLH));
    public static final IRegistryObject<RuneBlock> CEN_RUNE = register("cen_rune", () -> new RuneBlock(RuneBlock.RuneType.CEN));
    public static final IRegistryObject<RuneBlock> GER_RUNE = register("ger_rune", () -> new RuneBlock(RuneBlock.RuneType.GER));
    public static final IRegistryObject<RuneBlock> RAD_RUNE = register("rad_rune", () -> new RuneBlock(RuneBlock.RuneType.RAD));
    public static final IRegistryObject<RuneBlock> IS_RUNE = register("is_rune", () -> new RuneBlock(RuneBlock.RuneType.IS));
    public static final IRegistryObject<RuneBlock> DAEG_RUNE = register("daeg_rune", () -> new RuneBlock(RuneBlock.RuneType.DAEG));
    public static final IRegistryObject<RuneBlock> TYR_RUNE = register("tyr_rune", () -> new RuneBlock(RuneBlock.RuneType.TYR));
    public static final IRegistryObject<RuneBlock> BEORC_RUNE = register("beorc_rune", () -> new RuneBlock(RuneBlock.RuneType.BEORC));
    public static final IRegistryObject<RuneBlock> LAGU_RUNE = register("lagu_rune", () -> new RuneBlock(RuneBlock.RuneType.LAGU));
    public static final IRegistryObject<RuneBlock> ODAL_RUNE = register("odal_rune", () -> new RuneBlock(RuneBlock.RuneType.ODAL));
    public static final IRegistryObject<RuneBlock> NYD_RUNE = register("nyd_rune", () -> new RuneBlock(RuneBlock.RuneType.NYD));
    public static final IRegistryObject<RuneBlock> THORN_RUNE = register("thorn_rune", () -> new RuneBlock(RuneBlock.RuneType.THORN));
    public static final IRegistryObject<RuneBlock> OS_RUNE = register("os_rune", () -> new RuneBlock(RuneBlock.RuneType.OS));

    public static final IRegistryObject<GunpowderReedBlock> GUNPOWDER_REED = register("gunpowder_reed", () -> new GunpowderReedBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, pos) -> false).forceSolidOff().noCollission().randomTicks().instabreak().lightLevel((l) -> 5).explosionResistance(200F).sound(SoundType.GRASS)));

    private static <T extends Block> IRegistryObject<T> register(String name, Supplier<? extends T> sup) {
        return register(name, sup, block -> item(block));
    }

    private static <T extends Block> IRegistryObject<T> register(String name, Supplier<? extends T> sup, Function<IRegistryObject<T>, Supplier<? extends Item>> itemCreator) {
        IRegistryObject<T> ret = registerNoItem(name, sup);
        ITEMS.register(name, itemCreator.apply(ret));
        return ret;
    }

    private static <T extends Block> IRegistryObject<T> registerNoItem(String name, Supplier<? extends T> sup) {
        return BLOCKS.register(name, sup);
    }

    private static Supplier<BlockItem> item(final IRegistryObject<? extends Block> block) {
        return () -> new BlockItem(block.get(), new Item.Properties());
    }

    public static Block[] runeBlocks() {
        return new Block[]{UR_RUNE.get(), EOH_RUNE.get(), HAGEL_RUNE.get(), EOLH_RUNE.get(), CEN_RUNE.get(), GER_RUNE.get(), RAD_RUNE.get(), IS_RUNE.get(), DAEG_RUNE.get(), TYR_RUNE.get(), BEORC_RUNE.get(), LAGU_RUNE.get(), ODAL_RUNE.get(), NYD_RUNE.get(), THORN_RUNE.get(), OS_RUNE.get()};
    }


    public static void init() {
    }
}
