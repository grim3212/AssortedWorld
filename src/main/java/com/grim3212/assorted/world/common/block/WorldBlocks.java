package com.grim3212.assorted.world.common.block;

import java.util.function.Function;
import java.util.function.Supplier;

import com.grim3212.assorted.world.AssortedWorld;
import com.grim3212.assorted.world.common.block.RuneBlock.RuneType;
import com.grim3212.assorted.world.common.item.WorldItems;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WorldBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AssortedWorld.MODID);
	public static final DeferredRegister<Item> ITEMS = WorldItems.ITEMS;

	public static final RegistryObject<Block> RANDOMITE_ORE = register("randomite_ore", () -> new DropExperienceBlock(Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.6f, 1.0f).requiresCorrectToolForDrops(), UniformInt.of(2, 5)));
	public static final RegistryObject<Block> DEEPSLATE_RANDOMITE_ORE = register("deepslate_randomite_ore", () -> new DropExperienceBlock(Properties.of(Material.STONE, MaterialColor.DEEPSLATE).sound(SoundType.STONE).strength(4.5F, 3.0f).requiresCorrectToolForDrops(), UniformInt.of(2, 5)));

	public static final RegistryObject<RuneBlock> UR_RUNE = register("ur_rune", () -> new RuneBlock(RuneType.UR));
	public static final RegistryObject<RuneBlock> EOH_RUNE = register("eoh_rune", () -> new RuneBlock(RuneType.EOH));
	public static final RegistryObject<RuneBlock> HAGEL_RUNE = register("hagel_rune", () -> new RuneBlock(RuneType.HAGEL));
	public static final RegistryObject<RuneBlock> EOLH_RUNE = register("eolh_rune", () -> new RuneBlock(RuneType.EOLH));
	public static final RegistryObject<RuneBlock> CEN_RUNE = register("cen_rune", () -> new RuneBlock(RuneType.CEN));
	public static final RegistryObject<RuneBlock> GER_RUNE = register("ger_rune", () -> new RuneBlock(RuneType.GER));
	public static final RegistryObject<RuneBlock> RAD_RUNE = register("rad_rune", () -> new RuneBlock(RuneType.RAD));
	public static final RegistryObject<RuneBlock> IS_RUNE = register("is_rune", () -> new RuneBlock(RuneType.IS));
	public static final RegistryObject<RuneBlock> DAEG_RUNE = register("daeg_rune", () -> new RuneBlock(RuneType.DAEG));
	public static final RegistryObject<RuneBlock> TYR_RUNE = register("tyr_rune", () -> new RuneBlock(RuneType.TYR));
	public static final RegistryObject<RuneBlock> BEORC_RUNE = register("beorc_rune", () -> new RuneBlock(RuneType.BEORC));
	public static final RegistryObject<RuneBlock> LAGU_RUNE = register("lagu_rune", () -> new RuneBlock(RuneType.LAGU));
	public static final RegistryObject<RuneBlock> ODAL_RUNE = register("odal_rune", () -> new RuneBlock(RuneType.ODAL));
	public static final RegistryObject<RuneBlock> NYD_RUNE = register("nyd_rune", () -> new RuneBlock(RuneType.NYD));
	public static final RegistryObject<RuneBlock> THORN_RUNE = register("thorn_rune", () -> new RuneBlock(RuneType.THORN));
	public static final RegistryObject<RuneBlock> OS_RUNE = register("os_rune", () -> new RuneBlock(RuneType.OS));

	public static final RegistryObject<GunpowderReedBlock> GUNPOWDER_REED = register("gunpowder_reed", () -> new GunpowderReedBlock(Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().lightLevel((l) -> 5).explosionResistance(200F).sound(SoundType.GRASS)));

	private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup) {
		return register(name, sup, block -> item(block));
	}

	private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup, Function<RegistryObject<T>, Supplier<? extends Item>> itemCreator) {
		RegistryObject<T> ret = registerNoItem(name, sup);
		ITEMS.register(name, itemCreator.apply(ret));
		return ret;
	}

	private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<? extends T> sup) {
		return BLOCKS.register(name, sup);
	}

	private static Supplier<BlockItem> item(final RegistryObject<? extends Block> block) {
		return () -> new BlockItem(block.get(), new Item.Properties().tab(AssortedWorld.ASSORTED_WORLD_ITEM_GROUP));
	}

	public static Block[] runeBlocks() {
		return new Block[] { UR_RUNE.get(), EOH_RUNE.get(), HAGEL_RUNE.get(), EOLH_RUNE.get(), CEN_RUNE.get(), GER_RUNE.get(), RAD_RUNE.get(), IS_RUNE.get(), DAEG_RUNE.get(), TYR_RUNE.get(), BEORC_RUNE.get(), LAGU_RUNE.get(), ODAL_RUNE.get(), NYD_RUNE.get(), THORN_RUNE.get(), OS_RUNE.get() };
	}
}
