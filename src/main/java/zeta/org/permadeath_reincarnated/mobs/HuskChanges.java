package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Husk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

@EventBusSubscriber
public class HuskChanges {
   @SubscribeEvent
   public static void onLivingDamage(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         LivingEntity target = event.getEntity();
         Entity attacker = event.getSource().getEntity();
         if (attacker != null) {
            if (!target.level().isClientSide) {
               if (target.isAffectedByPotions()) {
                  if (attacker instanceof Husk) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     if (day >= 15) {
                        if (day >= 40) {
                           target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 1200, 9, false, true));
                           target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1200, 2, false, true));
                        } else {
                           target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 400, 4, false, true));
                        }

                        if (target instanceof ServerPlayer player) {
                           PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.HUSK_MALNUTRITION_ID);
                           if (day >= 40) {
                              PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.HUSK_INANITION_ID);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
