package com.grim3212.assorted.world.common.handler;

import com.google.gson.JsonObject;
import com.grim3212.assorted.world.AssortedWorld;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class EnabledCondition implements ICondition {

	private static final ResourceLocation NAME = new ResourceLocation(AssortedWorld.MODID, "part_enabled");
	private final String part;

	public static final String GUNPOWDER_REEDS_CONDITION = "gunpowder_reeds";

	public EnabledCondition(String part) {
		this.part = part;
	}

	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		switch (part) {
			case GUNPOWDER_REEDS_CONDITION:
				return WorldConfig.COMMON.generateGunpowderReeds.get();
			default:
				return false;
		}
	}

	public static class Serializer implements IConditionSerializer<EnabledCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, EnabledCondition value) {
			json.addProperty("part", value.part);
		}

		@Override
		public EnabledCondition read(JsonObject json) {
			return new EnabledCondition(GsonHelper.getAsString(json, "part"));
		}

		@Override
		public ResourceLocation getID() {
			return EnabledCondition.NAME;
		}
	}
}
