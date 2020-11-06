package com.grim3212.assorted.world.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class RuneBlock extends Block {

	private final RuneType runeType;

	public RuneBlock(RuneType runeType) {
		super(Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(50f));
		this.runeType = runeType;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		player.addPotionEffect(getPotionEffect(player.experienceLevel, 1.0F, 0.06F));
		return ActionResultType.SUCCESS;
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		if (entityIn instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entityIn;
			int xpLevel = 1;
			if (living instanceof PlayerEntity) {
				xpLevel = ((PlayerEntity) living).experienceLevel;
			}
			living.addPotionEffect(getPotionEffect(xpLevel, 1.0F, 0.06F));
		}
	}

	private EffectInstance getPotionEffect(int xpLevel, float durationMod, float amplifierMod) {
		// TODO: Add in config options for modifiers
		// XPLVLMOD, DURATIONMOD, AMPLIFIERMOD
		int duration = (int) (1000F + (float) (500F + (xpLevel * 100)) * durationMod);
		int amplifier = (int) (0.0D + Math.floor(amplifierMod * (float) xpLevel));

		return new EffectInstance(this.runeType.getEffect(), duration, amplifier);
	}

	public static enum RuneType implements IStringSerializable {
		UR("ur", "strength"),
		EOH("eoh", "poison"),
		HAGEL("hagel", "jump_boost"),
		EOLH("eolh", "resistance"),
		CEN("cen", "fire_resistance"),
		GER("ger", "haste"),
		RAD("rad", "speed"),
		IS("is", "slowness"),
		DAEG("daeg", "instant_health"),
		TYR("tyr", "nausea"),
		BEORC("beorc", "regeneration"),
		LAGU("lagu", "water_breathing"),
		ODAL("odal", "night_vision"),
		NYD("nyd", "hunger"),
		THORN("thorn", "weakness"),
		OS("os", "blindness");

		private final String runeName;
		private final String effectName;
		private Effect effectType;
		public static final RuneType values[] = values();

		private RuneType(String runeName, String effectName) {
			this.runeName = runeName;
			this.effectName = effectName;
		}

		public static String[] names() {
			RuneType[] states = values;
			String[] names = new String[states.length];

			for (int i = 0; i < states.length; i++) {
				names[i] = states[i].getString();
			}

			return names;
		}

		public Effect getEffect() {
			if (effectType != null) {
				return effectType;
			} else {
				effectType = get(this.effectName);
				return effectType;
			}
		}

		public static Effect get(String loc) {
			return Registry.EFFECTS.getOrDefault(new ResourceLocation(loc));
		}

		@Override
		public String getString() {
			return this.runeName;
		}
	}

}
