package com.grim3212.assorted.world.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

public class GunpowderReedBlock extends SugarCaneBlock {

	public GunpowderReedBlock(Properties props) {
		super(props);
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		BlockState plant = plantable.getPlant(world, pos.relative(facing));
		if (plant.getBlock() == WorldBlocks.GUNPOWDER_REED.get())
			return true;

		return super.canSustainPlant(state, world, pos, facing, plantable);
	}
}
