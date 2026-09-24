package zeta.org.permadeath_reincarnated.systems;

import java.util.Set;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;

public interface MobEffectInstanceAccess {
   int permadeathReincarnated$getDuration();

   int permadeathReincarnated$getAmplifier();

   boolean permadeathReincarnated$isAmbient();

   boolean permadeathReincarnated$isVisible();

   boolean permadeathReincarnated$showIcon();

   MobEffectInstance permadeathReincarnated$getHiddenEffect();

   Set<EffectCure> permadeathReincarnated$getCures();

   void permadeathReincarnated$setDuration(int var1);

   void permadeathReincarnated$setAmplifier(int var1);

   void permadeathReincarnated$setAmbient(boolean var1);

   void permadeathReincarnated$setVisible(boolean var1);

   void permadeathReincarnated$setShowIcon(boolean var1);

   void permadeathReincarnated$setHiddenEffect(MobEffectInstance var1);

   void permadeathReincarnated$setCures(Set<EffectCure> var1);
}
