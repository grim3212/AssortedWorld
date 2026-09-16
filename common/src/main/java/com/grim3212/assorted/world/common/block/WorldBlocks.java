package com.grim3212.assorted.world.common.block;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.world.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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

    public static final IRegistryObject<Block> RANDOMITE_ORE = register("randomite_ore", props -> new DropExperienceBlock(UniformInt.of(2, 5), props.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.6f, 1.0f).requiresCorrectToolForDrops()));
    public static final IRegistryObject<Block> DEEPSLATE_RANDOMITE_ORE = register("deepslate_randomite_ore", props -> new DropExperienceBlock(UniformInt.of(2, 5), props.mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(4.5F, 3.0f).requiresCorrectToolForDrops()));

    public static final IRegistryObject<RuneBlock> UR_RUNE = register("ur_rune", props -> new RuneBlock(RuneBlock.RuneType.UR, props));
    public static final IRegistryObject<RuneBlock> EOH_RUNE = register("eoh_rune", props -> new RuneBlock(RuneBlock.RuneType.EOH, props));
    public static final IRegistryObject<RuneBlock> HAGEL_RUNE = register("hagel_rune", props -> new RuneBlock(RuneBlock.RuneType.HAGEL, props));
    public static final IRegistryObject<RuneBlock> EOLH_RUNE = register("eolh_rune", props -> new RuneBlock(RuneBlock.RuneType.EOLH, props));
    public static final IRegistryObject<RuneBlock> CEN_RUNE = register("cen_rune", props -> new RuneBlock(RuneBlock.RuneType.CEN, props));
    public static final IRegistryObject<RuneBlock> GER_RUNE = register("ger_rune", props -> new RuneBlock(RuneBlock.RuneType.GER, props));
    public static final IRegistryObject<RuneBlock> RAD_RUNE = register("rad_rune", props -> new RuneBlock(RuneBlock.RuneType.RAD, props));
    public static final IRegistryObject<RuneBlock> IS_RUNE = register("is_rune", props -> new RuneBlock(RuneBlock.RuneType.IS, props));
    public static final IRegistryObject<RuneBlock> DAEG_RUNE = register("daeg_rune", props -> new RuneBlock(RuneBlock.RuneType.DAEG, props));
    public static final IRegistryObject<RuneBlock> TYR_RUNE = register("tyr_rune", props -> new RuneBlock(RuneBlock.RuneType.TYR, props));
    public static final IRegistryObject<RuneBlock> BEORC_RUNE = register("beorc_rune", props -> new RuneBlock(RuneBlock.RuneType.BEORC, props));
    public static final IRegistryObject<RuneBlock> LAGU_RUNE = register("lagu_rune", props -> new RuneBlock(RuneBlock.RuneType.LAGU, props));
    public static final IRegistryObject<RuneBlock> ODAL_RUNE = register("odal_rune", props -> new RuneBlock(RuneBlock.RuneType.ODAL, props));
    public static final IRegistryObject<RuneBlock> NYD_RUNE = register("nyd_rune", props -> new RuneBlock(RuneBlock.RuneType.NYD, props));
    public static final IRegistryObject<RuneBlock> THORN_RUNE = register("thorn_rune", props -> new RuneBlock(RuneBlock.RuneType.THORN, props));
    public static final IRegistryObject<RuneBlock> OS_RUNE = register("os_rune", props -> new RuneBlock(RuneBlock.RuneType.OS, props));

    // forceSolidOff() is deprecated in 26.2 and is redundant here: BlockStateBase#calculateSolid
    // already reports a block with an empty collision shape as non solid, and noCollision() gives
    // exactly that. Vanilla's own sugar cane dropped the call for the same reason.
    public static final IRegistryObject<GunpowderReedBlock> GUNPOWDER_REED = register("gunpowder_reed", props -> new GunpowderReedBlock(props.mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, pos) -> false).noCollision().randomTicks().instabreak().lightLevel((l) -> 5).explosionResistance(200F).sound(SoundType.GRASS)));

    // noCollision() with an empty shape is what makes this non solid; see the gunpowder reed above.
    // Light level 12 matches the 0.8F the seeds glowed at before light levels were integers.
    public static final IRegistryObject<GlowstoneSeedBlock> GLOWSTONE_SEEDS = register("glowstone_seeds", props -> new GlowstoneSeedBlock(props.mapColor(MapColor.SAND).pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, pos) -> false).noCollision().randomTicks().instabreak().lightLevel((l) -> 12).sound(SoundType.GLASS)));

    private static <T extends Block> IRegistryObject<T> register(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        return register(name, factory, block -> item(name, block));
    }

    private static <T extends Block> IRegistryObject<T> register(String name, Function<BlockBehaviour.Properties, ? extends T> factory, Function<IRegistryObject<T>, Supplier<? extends Item>> itemCreator) {
        IRegistryObject<T> ret = registerNoItem(name, factory);
        ITEMS.register(name, itemCreator.apply(ret));
        return ret;
    }

    private static <T extends Block> IRegistryObject<T> registerNoItem(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        // Since 1.21.2 every block has to know its own id before it is constructed, so the
        // properties are built here where the registration name is known.
        final ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return BLOCKS.register(name, () -> factory.apply(BlockBehaviour.Properties.of().setId(key)));
    }

    private static Supplier<BlockItem> item(final String name, final IRegistryObject<? extends Block> block) {
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return () -> new BlockItem(block.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key));
    }

    public static Block[] runeBlocks() {
        return new Block[]{UR_RUNE.get(), EOH_RUNE.get(), HAGEL_RUNE.get(), EOLH_RUNE.get(), CEN_RUNE.get(), GER_RUNE.get(), RAD_RUNE.get(), IS_RUNE.get(), DAEG_RUNE.get(), TYR_RUNE.get(), BEORC_RUNE.get(), LAGU_RUNE.get(), ODAL_RUNE.get(), NYD_RUNE.get(), THORN_RUNE.get(), OS_RUNE.get()};
    }


    public static void init() {
    }
}
