package zeta.org.permadeath_reincarnated.systems.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PermadeathFeatures {
   public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, "permadeath_reincarnated");
   public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> BEGINNING_FLOWERS = FEATURES.register(
      "beginning_flowers", () -> new PurpurChorusFeature(NoneFeatureConfiguration.CODEC)
   );
}
