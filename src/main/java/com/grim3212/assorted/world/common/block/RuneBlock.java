package com.grim3212.assorted.world.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public class RuneBlock extends Block {

	private final RuneType runeType;

	public RuneBlock(RuneType runeType) {
		super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(50f));
		this.runeType = runeType;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		player.addEffect(getPotionEffect(player.experienceLevel, 1.0F, 0.06F));
		return InteractionResult.SUCCESS;
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entity;
			int xpLevel = 1;
			if (living instanceof Player) {
				xpLevel = ((Player) living).experienceLevel;
			}
			living.addEffect(getPotionEffect(xpLevel, 1.0F, 0.06F));
		}
	}

	private MobEffectInstance getPotionEffect(int xpLevel, float durationMod, float amplifierMod) {
		// TODO: Add in config options for modifiers
		// XPLVLMOD, DURATIONMOD, AMPLIFIERMOD
		int duration = (int) (1000F + (float) (500F + (xpLevel * 100)) * durationMod);
		int amplifier = (int) (0.0D + Math.floor(amplifierMod * (float) xpLevel));

		return new MobEffectInstance(this.runeType.getEffect(), duration, amplifier);
	}

	public static enum RuneType implements StringRepresentable {
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
		private MobEffect effectType;
		public static final RuneType values[] = values();

		private RuneType(String runeName, String effectName) {
			this.runeName = runeName;
			this.effectName = effectName;
		}

		public static String[] names() {
			RuneType[] states = values;
			String[] names = new String[states.length];

			for (int i = 0; i < states.length; i++) {
				names[i] = states[i].getSerializedName();
			}

			return names;
		}

		public MobEffect getEffect() {
			if (effectType != null) {
				return effectType;
			} else {
				effectType = get(this.effectName);
				return effectType;
			}
		}

		public static MobEffect get(String loc) {
			return Registry.MOB_EFFECT.get(new ResourceLocation(loc));
		}

		@Override
		public String getSerializedName() {
			return this.runeName;
		}
	}

}
