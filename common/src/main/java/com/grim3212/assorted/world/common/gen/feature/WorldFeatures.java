package com.grim3212.assorted.world.common.gen.feature;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.world.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class WorldFeatures {

    public static final RegistryProvider<Feature<?>> FEATURES = RegistryProvider.create(Registries.FEATURE, Constants.MOD_ID);

    public static final IRegistryObject<Feature<NoneFeatureConfiguration>> RUIN_FEATURE = FEATURES.register("ruin", () -> new RuinFeature(NoneFeatureConfiguration.CODEC));
    public static final IRegistryObject<Feature<NoneFeatureConfiguration>> SPIRE_FEATURE = FEATURES.register("spire", () -> new SpireFeature(NoneFeatureConfiguration.CODEC));

    public static void init() {
    }
}
