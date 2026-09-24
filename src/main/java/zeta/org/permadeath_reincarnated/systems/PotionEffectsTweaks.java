package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable;
import zeta.org.permadeath_reincarnated.mobEffects.PermadeathMobEffectBuilder;

@EventBusSubscriber
public class PotionEffectsTweaks {
   private static final Holder<MobEffect> MINING_FATIGUE = MobEffects.DIG_SLOWDOWN;
   private static final Holder<MobEffect> POST_MORTEM = PermadeathMobEffectBuilder.POST_MORTEM_EFFECT;
   private static final Holder<MobEffect> INFERNAL_MORTEM = PermadeathMobEffectBuilder.INFERNAL_MORTEM_EFFECT;
   private static final Holder<MobEffect> WITHER_TIMER = PermadeathMobEffectBuilder.WITHER_TIMER_EFFECT;

   @SubscribeEvent
   public static void onEffectApplicable(Applicable event) {
      if (!event.getEntity().level().isClientSide) {
         if (event.getEntity() instanceof ServerPlayer player) {
            int day = DayGlobalCount.CURRENT_DAY;
            if (day >= 50) {
               MobEffectInstance instance = event.getEffectInstance();
               Holder<MobEffect> effect = instance.getEffect();
               if (effect == MINING_FATIGUE) {
                  MobEffectInstanceAccess access = (MobEffectInstanceAccess)instance;
                  access.permadeathReincarnated$setDuration(access.permadeathReincarnated$getDuration() * 2);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEffectCure(Added event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         int var4 = DayGlobalCount.CURRENT_DAY;
         MobEffectInstance effectInstance = event.getEffectInstance();
         if (effectInstance.getEffect().value() == POST_MORTEM.value()) {
            effectInstance.getCures().remove(EffectCures.MILK);
            effectInstance.getCures().remove(EffectCures.PROTECTED_BY_TOTEM);
         }

         if (effectInstance.getEffect().value() == INFERNAL_MORTEM.value()) {
            effectInstance.getCures().remove(EffectCures.MILK);
            effectInstance.getCures().remove(EffectCures.PROTECTED_BY_TOTEM);
         }

         if (effectInstance.getEffect().value() == WITHER_TIMER.value()) {
            effectInstance.getCures().remove(EffectCures.MILK);
            effectInstance.getCures().remove(EffectCures.PROTECTED_BY_TOTEM);
         }

         if (effectInstance.getEffect() == MINING_FATIGUE && var4 >= 50) {
            effectInstance.getCures().remove(EffectCures.MILK);
         }
      }
   }
}
