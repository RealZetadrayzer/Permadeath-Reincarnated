package zeta.org.permadeath_reincarnated.mobEffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PostMortemEffect extends MobEffect {
   public PostMortemEffect(MobEffectCategory category, int color) {
      super(category, color);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
