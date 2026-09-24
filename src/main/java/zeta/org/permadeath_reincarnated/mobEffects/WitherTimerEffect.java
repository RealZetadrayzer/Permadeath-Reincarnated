package zeta.org.permadeath_reincarnated.mobEffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class WitherTimerEffect extends MobEffect {
   public WitherTimerEffect(MobEffectCategory category, int color) {
      super(category, color);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
