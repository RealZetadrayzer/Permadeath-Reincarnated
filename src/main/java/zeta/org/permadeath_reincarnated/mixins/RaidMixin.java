package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(Raid.class)
public class RaidMixin {
   @ModifyArg(
      method = "tick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"),
      index = 0
   )
   private MobEffectInstance modifyHeroEffectDuration(MobEffectInstance instance) {
      if (instance.getEffect() == MobEffects.HERO_OF_THE_VILLAGE) {
         long day = DayGlobalCount.CURRENT_DAY;
         int duration = day >= 50L ? 6000 : instance.getDuration();
         return new MobEffectInstance(instance.getEffect(), duration, instance.getAmplifier(), instance.isAmbient(), instance.isVisible(), instance.showIcon());
      } else {
         return instance;
      }
   }
}
