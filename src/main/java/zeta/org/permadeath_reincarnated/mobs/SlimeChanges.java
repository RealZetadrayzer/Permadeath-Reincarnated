package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class SlimeChanges {
   @SubscribeEvent
   public static void onLivingDamage(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         LivingEntity target = event.getEntity();
         Entity attacker = event.getSource().getEntity();
         if (attacker != null) {
            if (!target.level().isClientSide) {
               if (target.isAffectedByPotions()) {
                  if (attacker instanceof Slime) {
                     if (attacker.getClass() != MagmaCube.class) {
                        if (attacker.getTags().contains("fromOozingStickySlime")) {
                           int day = DayGlobalCount.CURRENT_DAY;
                           target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, day >= 50 ? 6000 : 200, day >= 50 ? 3 : 1, false, true));
                           if (day >= 50) {
                              target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 6000, 2, false, true));
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 50) {
            if (event.getEntity() instanceof Slime slime) {
               if (slime.getClass() != MagmaCube.class) {
                  if (slime.getTags().contains("fromOozingStickySlime")) {
                     slime.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 2, false, true));
                  }
               }
            }
         }
      }
   }
}
